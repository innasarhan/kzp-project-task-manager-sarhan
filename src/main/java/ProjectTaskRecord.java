import java.util.Objects;

/**
 * Запис проєктної задачі для CSV-експорту та імпорту.
 */
public final class ProjectTaskRecord {

    @CsvColumn("назва")
    private String name;

    @CsvColumn("виконавець")
    private String assignee;

    @CsvColumn("години")
    private double estimatedHours;

    @CsvColumn("статус")
    private TaskStatus status;

    @CsvColumn("пріоритет")
    private TaskPriority priority;

    /**
     * Створює запис проєктної задачі.
     *
     * @param name назва задачі
     * @param assignee виконавець
     * @param estimatedHours оцінка тривалості в годинах
     * @param status стан задачі
     * @param priority пріоритет задачі
     */
    public ProjectTaskRecord(
            String name,
            String assignee,
            double estimatedHours,
            TaskStatus status,
            TaskPriority priority) {

        this.name = requireText(name, "name");
        this.assignee = requireText(assignee, "assignee");

        if (!Double.isFinite(estimatedHours) || estimatedHours < 0) {
            throw new IllegalArgumentException(
                    "estimatedHours must be finite and non-negative");
        }

        this.estimatedHours = estimatedHours;
        this.status = Objects.requireNonNull(status, "status");
        this.priority = Objects.requireNonNull(priority, "priority");
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName);

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }

        return value;
    }

    /**
     * @return назва задачі
     */
    public String getName() {
        return name;
    }

    /**
     * @return виконавець задачі
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * @return оцінка тривалості в годинах
     */
    public double getEstimatedHours() {
        return estimatedHours;
    }

    /**
     * @return стан задачі
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * @return пріоритет задачі
     */
    public TaskPriority getPriority() {
        return priority;
    }

    /**
     * Перевіряє логічний ідентифікатор за назвою та виконавцем.
     *
     * @param other інший запис
     * @return true, якщо назва та виконавець збігаються
     */
    public boolean hasSameIdentity(ProjectTaskRecord other) {
        return other != null
                && name.equals(other.name)
                && assignee.equals(other.assignee);
    }

    /**
     * Порівнює записи за всіма полями.
     *
     * @param object об'єкт для порівняння
     * @return true, якщо всі поля збігаються
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof ProjectTaskRecord other)) {
            return false;
        }

        return Double.compare(estimatedHours, other.estimatedHours) == 0
                && name.equals(other.name)
                && assignee.equals(other.assignee)
                && status == other.status
                && priority == other.priority;
    }

    /**
     * @return хеш-код запису
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                name, assignee, estimatedHours, status, priority);
    }

    /**
     * @return текстове представлення запису
     */
    @Override
    public String toString() {
        return "ProjectTaskRecord{"
                + "name='" + name + '\''
                + ", assignee='" + assignee + '\''
                + ", estimatedHours=" + estimatedHours
                + ", status=" + status
                + ", priority=" + priority
                + '}';
    }
}
