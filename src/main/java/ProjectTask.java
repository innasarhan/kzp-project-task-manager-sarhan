import java.util.Locale;
import java.util.Objects;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Представляє одну задачу проєкту.
 *
 * <p>Клас містить спільні дані та інваріанти для всіх типів
 * проєктних задач. Конкретні підтипи можуть перевизначати
 * поліморфну оцінку пріоритету.</p>
 */
public class ProjectTask {

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
     * @throws NullPointerException якщо title або assignee null
     * @throws IllegalArgumentException якщо порушено інваріанти
     */
    @SuppressFBWarnings(
            value = "CT_CONSTRUCTOR_THROW",
            justification = "Конструктор виконує валідацію вхідних даних згідно з інваріантами доменної моделі."
    )
    public ProjectTask(
            String title,
            String assignee,
            double estimateHours,
            int priority,
            boolean done) {

        Objects.requireNonNull(title, "title не може бути null");
        Objects.requireNonNull(assignee, "assignee не може бути null");

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

        if (!Double.isFinite(estimateHours) || estimateHours < 0) {
            throw new IllegalArgumentException(
                    "оцінка годин має бути невід'ємним скінченним числом"
            );
        }

        if (priority < 0) {
            throw new IllegalArgumentException(
                    "пріоритет не може бути від'ємним"
            );
        }

        this.title = title;
        this.assignee = assignee;
        this.estimateHours = estimateHours;
        this.priority = priority;
        this.done = done;
    }

    /**
     * Створює ProjectTask із CSV-рядка.
     *
     * <p>Формат залишається сумісним із попередніми лабораторними:
     * title;assignee;estimateHours;priority;done</p>
     *
     * @param csvLine один CSV-рядок
     * @return створена задача
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
     * @return виконавець
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * Повертає оцінку тривалості.
     *
     * @return оцінка в годинах
     */
    public double getEstimateHours() {
        return estimateHours;
    }

    /**
     * Повертає базовий пріоритет.
     *
     * @return пріоритет
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
     * Повертає тип задачі.
     *
     * <p>Для базового ProjectTask повертається DEVELOPMENT.
     * Конкретні підтипи перевизначають цей метод.</p>
     *
     * @return тип задачі
     */
    public TaskKind getKind() {
        return TaskKind.DEVELOPMENT;
    }

    /**
     * Поліморфно оцінює пріоритет задачі.
     *
     * @return оцінений пріоритет
     */
    public int evaluatePriority() {
        return priority;
    }

    /**
     * Перевіряє рівність задач.
     *
     * @param other інший об'єкт
     * @return true, якщо задачі мають однаковий тип і дані
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        ProjectTask that = (ProjectTask) other;

        return Double.compare(
                estimateHours,
                that.estimateHours
        ) == 0
                && priority == that.priority
                && done == that.done
                && title.equals(that.title)
                && assignee.equals(that.assignee);
    }

    /**
     * Обчислює хеш-код задачі.
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                getClass(),
                title,
                assignee,
                estimateHours,
                priority,
                done
        );
    }

    /**
     * Повертає текстове представлення задачі.
     *
     * @return текстове представлення
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
