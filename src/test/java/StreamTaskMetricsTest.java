import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Перевіряє статистичні показники, реалізовані через Stream API.
 */
public class StreamTaskMetricsTest {

    private final StreamTaskMetrics metrics = new StreamTaskMetrics();

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
    void shouldCalculateTotalEstimateHoursUsingStreams() {
        assertEquals(
                26.0,
                metrics.totalEstimateHours(sampleTasks())
        );
    }

    @Test
    void shouldCalculateAveragePriorityUsingStreams() {
        assertEquals(
                7.0 / 3.0,
                metrics.averagePriority(sampleTasks())
        );
    }

    @Test
    void shouldCountCompletedTasksUsingStreams() {
        assertEquals(
                2,
                metrics.countCompletedTasks(sampleTasks())
        );
    }

    @Test
    void shouldReturnZeroForEmptyTasks() {
        List<ProjectTask> tasks = List.of();

        assertEquals(
                0.0,
                metrics.totalEstimateHours(tasks)
        );
        assertEquals(
                0.0,
                metrics.averagePriority(tasks)
        );
        assertEquals(
                0,
                metrics.countCompletedTasks(tasks)
        );
    }
}
