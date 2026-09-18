import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskMetricsTest {

    @Test
    void shouldCalculateMetrics() {
        List<Task> tasks = List.of(
                new Task("Розробити API", "Іван", 12.5, 1, true),
                new Task("Створити дизайн", "Олена", 8.0, 2, false),
                new Task("Написати тести", "Андрій", 5.5, 3, true)
        );

        TaskMetrics metrics = new TaskMetrics();

        assertEquals(26.0, metrics.totalEstimateHours(tasks));
        assertEquals(2.0, metrics.averagePriority(tasks));
        assertEquals(2, metrics.countCompletedTasks(tasks));
    }
}