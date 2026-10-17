
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Універсальний імпортер CSV на основі анотації {@link CsvColumn}.
 *
 * <p>Клас запису повинен мати конструктор, параметри якого названі
 * так само, як відповідні анотовані поля. Для збереження назв параметрів
 * компілятор має використовувати прапорець -parameters.</p>
 */
public class CsvImporter {

    /**
     * Імпортує CSV-файл у список об'єктів заданого типу.
     *
     * @param path шлях до CSV-файлу
     * @param type клас об'єктів для створення
     * @param <T> тип об'єктів
     * @return незмінний список імпортованих об'єктів
     * @throws CsvFormatException якщо файл має неправильний формат
     */
    public <T> List<T> importFile(Path path, Class<T> type) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(type, "type");

        final String content;
        try {
            content = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new CsvFormatException(
                    "Failed to read CSV file: " + path, exception);
        }

        if (content.isEmpty()) {
            return List.of();
        }

        List<List<String>> rows = parseCsv(content);
        if (rows.isEmpty()) {
            return List.of();
        }

        List<Field> fields = annotatedFields(type);
        if (fields.isEmpty()) {
            throw new CsvFormatException(
                    "No fields annotated with @CsvColumn");
        }

        List<String> expectedHeaders = fields.stream()
                .map(field -> field.getAnnotation(CsvColumn.class).value())
                .toList();

        if (!rows.get(0).equals(expectedHeaders)) {
            throw new CsvFormatException(
                    "CSV headers do not match the expected columns");
        }

        Constructor<T> constructor = findConstructor(type, fields);
        List<T> result = new ArrayList<>();

        for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
            List<String> values = rows.get(rowIndex);

            if (values.size() != fields.size()) {
                throw new CsvFormatException(
                        "Invalid field count on CSV row " + (rowIndex + 1));
            }

            result.add(createObject(
                    constructor, fields, values, rowIndex + 1));
        }

        return List.copyOf(result);
    }

    private List<Field> annotatedFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(CsvColumn.class)) {
                fields.add(field);
            }
        }

        fields.sort(Comparator.comparing(Field::getName));
        return fields;
    }

    private <T> Constructor<T> findConstructor(
            Class<T> type, List<Field> fields) {

        for (Constructor<?> candidate : type.getDeclaredConstructors()) {
            Parameter[] parameters = candidate.getParameters();

            if (parameters.length != fields.size()) {
                continue;
            }

            boolean matches = true;

            for (Parameter parameter : parameters) {
                if (!parameter.isNamePresent()
                        || fields.stream().noneMatch(field ->
                                field.getName().equals(parameter.getName())
                                && field.getType().equals(parameter.getType()))) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                try {
                    @SuppressWarnings("unchecked")
                    Constructor<T> result =
                            (Constructor<T>) candidate;
                    result.setAccessible(true);
                    return result;
                } catch (SecurityException exception) {
                    throw new CsvFormatException(
                            "Cannot access constructor for " + type.getName(),
                            exception);
                }
            }
        }

        throw new CsvFormatException(
                "No constructor found whose parameter names and types "
                        + "match the @CsvColumn fields of " + type.getName());
    }

    private <T> T createObject(
            Constructor<T> constructor,
            List<Field> fields,
            List<String> values,
            int rowNumber) {

        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            int fieldIndex = -1;

            for (int j = 0; j < fields.size(); j++) {
                if (fields.get(j).getName().equals(parameter.getName())) {
                    fieldIndex = j;
                    break;
                }
            }

            if (fieldIndex < 0) {
                throw new CsvFormatException(
                        "No CSV column for parameter " + parameter.getName());
            }

            arguments[i] = convert(
                    values.get(fieldIndex), parameter.getType(), rowNumber);
        }

        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException
                 | InvocationTargetException exception) {
            Throwable cause = exception instanceof InvocationTargetException
                    && exception.getCause() != null
                    ? exception.getCause()
                    : exception;

            throw new CsvFormatException(
                    "Cannot create object from CSV row " + rowNumber,
                    cause);
        }
    }

    private Object convert(String value, Class<?> type, int rowNumber) {
        try {
            if (type == String.class) {
                return value;
            }
            if (type == double.class || type == Double.class) {
                return Double.parseDouble(value);
            }
            if (type == int.class || type == Integer.class) {
                return Integer.parseInt(value);
            }
            if (type == long.class || type == Long.class) {
                return Long.parseLong(value);
            }
            if (type == float.class || type == Float.class) {
                return Float.parseFloat(value);
            }
            if (type == boolean.class || type == Boolean.class) {
                if (!value.equalsIgnoreCase("true")
                        && !value.equalsIgnoreCase("false")) {
                    throw new IllegalArgumentException(
                            "Expected true or false");
                }
                return Boolean.parseBoolean(value);
            }
            if (type.isEnum()) {
                return enumValue(type, value);
            }
        } catch (IllegalArgumentException exception) {
            throw new CsvFormatException(
                    "Invalid value '" + value + "' for type "
                            + type.getSimpleName() + " on CSV row " + rowNumber,
                    exception);
        }

        throw new CsvFormatException(
                "Unsupported CSV field type: " + type.getName());
    }

    private Object enumValue(Class<?> type, String value) {
        for (Object constant : type.getEnumConstants()) {
            if (((Enum<?>) constant).name().equals(value)) {
                return constant;
            }
        }

        throw new IllegalArgumentException(
                "Unknown enum constant '" + value + "'");
    }

    private List<List<String>> parseCsv(String content) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean fieldStarted = false;
        boolean afterClosingQuote = false;

        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);

            if (inQuotes) {
                if (ch == '"') {
                    if (i + 1 < content.length()
                            && content.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                        afterClosingQuote = true;
                    }
                } else {
                    field.append(ch);
                }
                continue;
            }

            if (afterClosingQuote && ch != ',' && ch != '\r' && ch != '\n') {
                throw new CsvFormatException(
                        "Unexpected character after closing CSV quote");
            }

            if (ch == '"' && !fieldStarted && field.isEmpty()) {
                inQuotes = true;
                fieldStarted = true;
            } else if (ch == ',') {
                row.add(field.toString());
                field.setLength(0);
                fieldStarted = false;
                afterClosingQuote = false;
            } else if (ch == '\n' || ch == '\r') {
                if (ch == '\r' && i + 1 < content.length()
                        && content.charAt(i + 1) == '\n') {
                    i++;
                }

                row.add(field.toString());
                addRow(rows, row);
                row = new ArrayList<>();
                field.setLength(0);
                fieldStarted = false;
                afterClosingQuote = false;
            } else {
                field.append(ch);
                fieldStarted = true;
            }
        }

        if (inQuotes) {
            throw new CsvFormatException("Unclosed quoted CSV field");
        }

        if (!row.isEmpty() || !field.isEmpty() || fieldStarted
                || afterClosingQuote) {
            row.add(field.toString());
            addRow(rows, row);
        }

        return rows;
    }

    private void addRow(List<List<String>> rows, List<String> row) {
        if (!(row.size() == 1 && row.get(0).isEmpty())) {
            rows.add(row);
        }
    }
}
