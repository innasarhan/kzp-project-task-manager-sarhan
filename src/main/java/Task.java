/**
 * Представляє одну задачу проєкту.
 *
 * <p>Задача містить назву, виконавця, оцінку часу,
 * пріоритет та ознаку виконання.</p>
 */
public class Task {

    private String title;
    private String assignee;
    private double estimateHours;
    private int priority;
    private boolean done;

    /**
     * Створює задачу з указаними параметрами.
     *
     * @param title назва задачі
     * @param assignee виконавець задачі
     * @param estimateHours оцінка тривалості в годинах
     * @param priority пріоритет задачі
     * @param done ознака виконання задачі
     */
    public Task(String title, String assignee, double estimateHours,
                int priority, boolean done) {
        this.title = title;
        this.assignee = assignee;
        this.estimateHours = estimateHours;
        this.priority = priority;
        this.done = done;
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
     * @return true, якщо задача виконана, інакше false
     */
    public boolean isDone() {
        return done;
    }
}
