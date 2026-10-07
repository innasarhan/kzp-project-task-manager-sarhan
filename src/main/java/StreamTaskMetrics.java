import java.util.List;

/**
 * Обчислює статистичні показники для списку задач
 * за допомогою Stream API.
 */
public class StreamTaskMetrics {

    /**
     * Обчислює сумарну оцінку тривалості всіх задач.
     *
     * @param tasks список задач
     * @return сумарна оцінка в годинах
     */
    public double totalEstimateHours(List<ProjectTask> tasks) {
        return tasks.stream()
                .mapToDouble(ProjectTask::getEstimateHours)
                .sum();
    }

    /**
     * Обчислює середній пріоритет задач.
     *
     * <p>Для порожнього списку повертається 0.0.</p>
     *
     * @param tasks список задач
     * @return середнє значення пріоритету
     */
    public double averagePriority(List<ProjectTask> tasks) {
        return tasks.stream()
                .mapToInt(ProjectTask::evaluatePriority)
                .average()
                .orElse(0.0);
    }

    /**
     * Підраховує кількість виконаних задач.
     *
     * @param tasks список задач
     * @return кількість задач зі статусом виконання true
     */
    public long countCompletedTasks(List<ProjectTask> tasks) {
        return tasks.stream()
                .filter(ProjectTask::isDone)
                .count();
    }
}
