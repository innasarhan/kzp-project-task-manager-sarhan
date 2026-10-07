import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Перевіряє еквівалентність циклової та потокової реалізацій
 * основних статистичних обчислень і контрольного звіту.
 */
public class TaskMetricsEquivalenceTest {

    private List<ProjectTask> sampleTasks() {
        return List.of(
                new DevelopmentTask(
                        "Розробити API",
                        "Іван",
                        12.5,
                        1,
                        true
                ),
                new DevelopmentTask(
                        "Створити дизайн",
                        "Олена",
                        8.0,
                        2,
                        false
                ),
                new TestingTask(
                        "Написати тести",
                        "Андрій",
                        5.5,
                        3,
                        true
                )
        );
    }

    @Test
    void shouldProduceEquivalentResultsForCycleAndStreamImplementations() {
        List<ProjectTask> tasks = sampleTasks();

        TaskMetrics cycleMetrics = new TaskMetrics();
        StreamTaskMetrics streamMetrics = new StreamTaskMetrics();

        assertEquals(
                cycleMetrics.totalEstimateHours(tasks),
                streamMetrics.totalEstimateHours(tasks)
        );

        assertEquals(
                cycleMetrics.averagePriority(tasks),
                streamMetrics.averagePriority(tasks)
        );

        assertEquals(
                cycleMetrics.countCompletedTasks(tasks),
                streamMetrics.countCompletedTasks(tasks)
        );
    }

    @Test
    void shouldProduceEquivalentControlReports() {
        List<ProjectTask> tasks = sampleTasks();

        TaskMetrics cycleMetrics = new TaskMetrics();
        StreamTaskMetrics streamMetrics = new StreamTaskMetrics();
        ReportFormatter formatter = new ReportFormatter();

        String cycleReport = formatter.format(
                tasks.size(),
                cycleMetrics.totalEstimateHours(tasks),
                cycleMetrics.averagePriority(tasks),
                cycleMetrics.countCompletedTasks(tasks)
        );

        String streamReport = formatter.format(
                tasks.size(),
                streamMetrics.totalEstimateHours(tasks),
                streamMetrics.averagePriority(tasks),
                (int) streamMetrics.countCompletedTasks(tasks)
        );

        assertEquals(cycleReport, streamReport);
    }
}