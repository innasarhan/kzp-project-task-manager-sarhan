import java.util.List;

/**
 * Обчислює статистичні показники для списку задач.
 */
public class TaskMetrics {

    /**
     * Обчислює сумарну оцінку тривалості всіх задач.
     *
     * @param tasks список задач
     * @return сумарна оцінка в годинах
     */
    public double totalEstimateHours(List<Task> tasks) {
        double total = 0.0;

        for (Task task : tasks) {
            total += task.getEstimateHours();
        }

        return total;
    }

    /**
     * Обчислює середній пріоритет задач.
     *
     * <p>Для порожнього списку повертається 0.0.</p>
     *
     * @param tasks список задач
     * @return середнє значення пріоритету
     */
    public double averagePriority(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0.0;
        }

        int total = 0;

        for (Task task : tasks) {
            total += task.getPriority();
        }

        return (double) total / tasks.size();
    }

    /**
     * Підраховує кількість виконаних задач.
     *
     * @param tasks список задач
     * @return кількість задач зі статусом виконання true
     */
    public int countCompletedTasks(List<Task> tasks) {
        int count = 0;

        for (Task task : tasks) {
            if (task.isDone()) {
                count++;
            }
        }

        return count;
    }
}
