import java.util.Locale;
import java.util.Objects;

/**
 * Представляє одну задачу проєкту.
 *
 * <p>Об'єкт має незмінний стан після створення.
 * Усі основні інваріанти перевіряються під час створення.</p>
 */
public final class ProjectTask {

    private final String title;
    private final String assignee;
    private final double estimateHours;
    private final int priority;
    private final boolean done;

    /**
     * Створює задачу з указаними параметрами.
     *
     * @param title назва задачі
     * @param assignee виконавець задачі
     * @param estimateHours оцінка тривалості в годинах
     * @param priority пріоритет задачі
     * @param done ознака виконання задачі
     * @throws NullPointerException якщо title або assignee дорівнює null
     * @throws IllegalArgumentException якщо title або assignee порожні,
     *                                  estimateHours або priority від'ємні
     */
    public ProjectTask(
            String title,
            String assignee,
            double estimateHours,
            int priority,
            boolean done) {

        this.title = Objects.requireNonNull(
                title,
                "title не може бути null"
        );

        this.assignee = Objects.requireNonNull(
                assignee,
                "assignee не може бути null"
        );

        if (title.isBlank()) {
            throw new IllegalArgumentException(
                    "порожня назва задачі"
            );
        }

        if (assignee.isBlank()) {
            throw new IllegalArgumentException(
                    "порожній виконавець"
            );
        }

        if (estimateHours < 0) {
            throw new IllegalArgumentException(
                    "оцінка годин не може бути від'ємною"
            );
        }

        if (priority < 0) {
            throw new IllegalArgumentException(
                    "пріоритет не може бути від'ємним"
            );
        }

        this.estimateHours = estimateHours;
        this.priority = priority;
        this.done = done;
    }

    /**
     * Створює ProjectTask із CSV-рядка.
     *
     * <p>Формат:
     * title;assignee;estimateHours;priority;done</p>
     *
     * @param csvLine один CSV-рядок
     * @return створена задача
     * @throws NullPointerException якщо csvLine дорівнює null
     * @throws IllegalArgumentException якщо кількість полів неправильна
     *                                  або числове/boolean поле має
     *                                  неправильний формат
     */
    public static ProjectTask fromCsv(String csvLine) {
        Objects.requireNonNull(
                csvLine,
                "csvLine не може бути null"
        );

        String[] fields = csvLine.split(";", -1);

        if (fields.length != 5) {
            throw new IllegalArgumentException(
                    "неправильна кількість полів"
            );
        }

        String title = fields[0].trim();
        String assignee = fields[1].trim();
        String estimateText = fields[2].trim();
        String priorityText = fields[3].trim();
        String doneText = fields[4].trim();

        double estimateHours;

        try {
            estimateHours = Double.parseDouble(estimateText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "оцінка годин має бути числом",
                    e
            );
        }

        int priority;

        try {
            priority = Integer.parseInt(priorityText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "пріоритет має бути цілим числом",
                    e
            );
        }

        if (!doneText.equalsIgnoreCase("true")
                && !doneText.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException(
                    "done має бути true або false"
            );
        }

        boolean done = Boolean.parseBoolean(doneText);

        return new ProjectTask(
                title,
                assignee,
                estimateHours,
                priority,
                done
        );
    }

    /**
     * Повертає назву задачі.
     *
     * @return назва задачі
     */
    public String getTitle() {
        return title;
    }

    /**
     * Повертає виконавця задачі.
     *
     * @return ім'я виконавця
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * Повертає оцінку тривалості задачі.
     *
     * @return оцінка в годинах
     */
    public double getEstimateHours() {
        return estimateHours;
    }

    /**
     * Повертає пріоритет задачі.
     *
     * @return числове значення пріоритету
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Перевіряє, чи виконана задача.
     *
     * @return true, якщо задача виконана
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Повертає текстове представлення задачі.
     *
     * @return текстове представлення об'єкта
     */
    @Override
    public String toString() {
        return String.format(
                Locale.ROOT,
                "ProjectTask{title='%s', assignee='%s', "
                        + "estimateHours=%.2f, priority=%d, done=%s}",
                title,
                assignee,
                estimateHours,
                priority,
                done
        );
    }
}