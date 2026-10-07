import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Виконує потокову обробку колекції проєктних задач.
 *
 * <p>Клас містить окремі Stream API-запити, передбачені
 * лабораторною роботою №4.</p>
 */
public class ProjectTaskQueries {

    /**
     * Повертає невиконані задачі.
     *
     * @param tasks колекція задач
     * @return список невиконаних задач
     */
    public List<ProjectTask> unfinishedTasks(List<ProjectTask> tasks) {
        return tasks.stream()
                .filter(task -> !task.isDone())
                .toList();
    }

    /**
     * Повертає назви всіх задач.
     *
     * @param tasks колекція задач
     * @return список назв задач
     */
    public List<String> taskTitles(List<ProjectTask> tasks) {
        return tasks.stream()
                .map(ProjectTask::getTitle)
                .toList();
    }

    /**
     * Обчислює сумарну кількість годин для кожного виконавця.
     *
     * @param tasks колекція задач
     * @return відображення "виконавець -> сумарні години"
     */
    public Map<String, Double> hoursByAssignee(List<ProjectTask> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(
                        ProjectTask::getAssignee,
                        Collectors.summingDouble(
                                ProjectTask::getEstimateHours
                        )
                ));
    }

    /**
     * Обчислює статистику оцінених годин.
     *
     * <p>Для порожньої колекції повертається порожня статистика
     * з нульовою кількістю та сумою.</p>
     *
     * @param tasks колекція задач
     * @return статистика кількості, суми, мінімуму, максимуму та середнього
     */
    public DoubleSummaryStatistics estimateStatistics(
            List<ProjectTask> tasks) {

        if (tasks.isEmpty()) {
            return new DoubleSummaryStatistics();
        }

        return tasks.stream()
                .collect(
                        Collectors.summarizingDouble(
                                ProjectTask::getEstimateHours
                        )
                );
    }

    /**
     * Повертає перші N задач за спаданням обчисленого пріоритету.
     *
     * <p>Якщо пріоритет однаковий, задачі сортуються
     * за оцінкою годин за зростанням.</p>
     *
     * @param tasks колекція задач
     * @param n максимальна кількість результатів
     * @return список задач з найвищим пріоритетом
     * @throws IllegalArgumentException якщо n менше нуля
     */
    public List<ProjectTask> topByPriority(
            List<ProjectTask> tasks,
            int n) {

        if (n < 0) {
            throw new IllegalArgumentException(
                    "Кількість задач не може бути від'ємною"
            );
        }

        return tasks.stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        ProjectTask::evaluatePriority
                                )
                                .reversed()
                                .thenComparing(
                                        Comparator.comparingDouble(
                                                ProjectTask::getEstimateHours
                                        )
                                )
                )
                .limit(n)
                .toList();
    }

    /**
     * Шукає першу задачу за точною назвою.
     *
     * @param tasks колекція задач
     * @param title назва задачі
     * @return Optional із першою знайденою задачею або порожній Optional
     */
    public Optional<ProjectTask> findByTitle(
            List<ProjectTask> tasks,
            String title) {

        return tasks.stream()
                .filter(task -> task.getTitle().equals(title))
                .findFirst();
    }
}