
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Універсальний експортер об'єктів у CSV на основі анотації CsvColumn.
 *
 * <p>Поля CSV визначаються анотацією {@link CsvColumn} та
 * сортуються за назвами Java-полів.</p>
 */
public class CsvExporter {

    /**
     * Експортує список об'єктів у CSV-файл.
     *
     * @param items список об'єктів для експорту
     * @param path шлях до файлу
     * @param <T> тип об'єктів
     * @throws CsvFormatException якщо експорт неможливо виконати
     */
    public <T> void export(List<T> items, Path path) {
        Objects.requireNonNull(items, "items");
        Objects.requireNonNull(path, "path");

        if (items.isEmpty()) {
            try {
                Files.writeString(path, "", StandardCharsets.UTF_8);
                return;
            } catch (IOException exception) {
                throw new CsvFormatException(
                        "Failed to export CSV to " + path, exception);
            }
        }

        try {
            List<Field> fields = annotatedFields(items.get(0).getClass());

            if (fields.isEmpty()) {
                throw new CsvFormatException(
                        "No fields annotated with @CsvColumn");
            }

            StringBuilder csv = new StringBuilder();

            appendRow(csv, fields.stream()
                    .map(field ->
                            field.getAnnotation(CsvColumn.class).value())
                    .toList());

            for (T item : items) {
                if (item == null
                        || item.getClass() != items.get(0).getClass()) {
                    throw new CsvFormatException(
                            "All exported objects must have the same class");
                }

                List<String> values = new ArrayList<>();

                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(item);
                    values.add(value == null ? "" : value.toString());
                }

                appendRow(csv, values);
            }

            Files.writeString(path, csv.toString(), StandardCharsets.UTF_8);
        } catch (IOException | IllegalAccessException exception) {
            throw new CsvFormatException(
                    "Failed to export CSV to " + path, exception);
        }
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

    private void appendRow(StringBuilder csv, List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                csv.append(',');
            }

            csv.append(escape(values.get(i)));
        }

        csv.append("\r\n");
    }

    private String escape(String value) {
        boolean needsQuotes = value.contains(",")
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r");

        if (!needsQuotes) {
            return value;
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
