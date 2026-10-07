import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Перевіряє потокові запити для проєктних задач.
 */
public class ProjectTaskQueriesTest {

    private final ProjectTaskQueries queries = new ProjectTaskQueries();

    private List<ProjectTask> sampleTasks() {
        return List.of(
                new DevelopmentTask(
                        "Розробити API", "Іван", 12.5, 1, true
                ),
                new DevelopmentTask(
                        "Створити дизайн", "Олена", 8.0, 2, false
                ),
                new TestingTask(
                        "Написати тести", "Андрій", 5.5, 3, true
                ),
                new TestingTask(
                        "Перевірити помилку", "Олена", 4.0, 2, false
                )
        );
    }

    @Test
    void shouldReturnUnfinishedTasks() {
        List<ProjectTask> result =
                queries.unfinishedTasks(sampleTasks());

        assertEquals(2, result.size());
        assertEquals(
                "Створити дизайн",
                result.get(0).getTitle()
        );
        assertEquals(
                "Перевірити помилку",
                result.get(1).getTitle()
        );
    }

    @Test
    void shouldReturnTaskTitles() {
        List<String> result = queries.taskTitles(sampleTasks());

        assertEquals(
                List.of(
                        "Розробити API",
                        "Створити дизайн",
                        "Написати тести",
                        "Перевірити помилку"
                ),
                result
        );
    }

    @Test
    void shouldGroupHoursByAssignee() {
        Map<String, Double> result =
                queries.hoursByAssignee(sampleTasks());

        assertEquals(12.5, result.get("Іван"));
        assertEquals(12.0, result.get("Олена"));
        assertEquals(5.5, result.get("Андрій"));
        assertEquals(3, result.size());
    }

    @Test
    void shouldCalculateEstimateStatistics() {
        var result =
                queries.estimateStatistics(sampleTasks());

        assertEquals(4, result.getCount());
        assertEquals(30.0, result.getSum());
        assertEquals(4.0, result.getMin());
        assertEquals(12.5, result.getMax());
        assertEquals(7.5, result.getAverage());
    }

    @Test
    void shouldReturnEmptyEstimateStatisticsForEmptyTasks() {
        var result = queries.estimateStatistics(List.of());

        assertEquals(0, result.getCount());
        assertEquals(0.0, result.getSum());
        assertEquals(0.0, result.getAverage());
    }

    @Test
    void shouldReturnTopTasksByPriority() {
        List<ProjectTask> result =
                queries.topByPriority(sampleTasks(), 3);

        assertEquals(3, result.size());
        assertEquals(
                "Перевірити помилку",
                result.get(0).getTitle()
        );
        assertEquals(
                "Написати тести",
                result.get(1).getTitle()
        );
        assertEquals(
                "Створити дизайн",
                result.get(2).getTitle()
        );
    }

    @Test
    void shouldLimitTopResultsToFiveTasks() {
        List<ProjectTask> tasks = List.of(
                new DevelopmentTask(
                        "Задача 1", "Іван", 10.0, 5, true
                ),
                new DevelopmentTask(
                        "Задача 2", "Олена", 9.0, 4, true
                ),
                new TestingTask(
                        "Задача 3", "Андрій", 8.0, 4, false
                ),
                new TestingTask(
                        "Задача 4", "Марія", 7.0, 3, false
                ),
                new DevelopmentTask(
                        "Задача 5", "Петро", 6.0, 2, true
                ),
                new TestingTask(
                        "Задача 6", "Іван", 5.0, 1, true
                )
        );

        List<ProjectTask> result =
                queries.topByPriority(tasks, 5);

        assertEquals(5, result.size());
        assertEquals(
                "Задача 1",
                result.get(0).getTitle()
        );
        assertEquals(
                "Задача 5",
                result.get(4).getTitle()
        );
    }

    @Test
    void shouldReturnEmptyListWhenTopNIsZero() {
        assertTrue(
                queries.topByPriority(sampleTasks(), 0).isEmpty()
        );
    }

    @Test
    void shouldReturnAllTasksWhenTopNIsGreaterThanSize() {
        assertEquals(
                4,
                queries.topByPriority(sampleTasks(), 10).size()
        );
    }

    @Test
    void shouldRejectNegativeTopN() {
        assertThrows(
                IllegalArgumentException.class,
                () -> queries.topByPriority(sampleTasks(), -1)
        );
    }

    @Test
    void shouldFindTaskByTitle() {
        Optional<ProjectTask> result =
                queries.findByTitle(
                        sampleTasks(),
                        "Створити дизайн"
                );

        assertTrue(result.isPresent());
        assertEquals(
                "Олена",
                result.orElseThrow().getAssignee()
        );
    }

    @Test
    void shouldReturnEmptyOptionalWhenTitleIsMissing() {
        Optional<ProjectTask> result =
                queries.findByTitle(
                        sampleTasks(),
                        "Неіснуюча задача"
                );

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnFirstTaskWhenTitlesAreDuplicated() {
        List<ProjectTask> tasks = List.of(
                new DevelopmentTask(
                        "Одна назва", "Іван", 5.0, 1, true
                ),
                new TestingTask(
                        "Одна назва", "Олена", 7.0, 3, false
                )
        );

        Optional<ProjectTask> result =
                queries.findByTitle(tasks, "Одна назва");

        assertTrue(result.isPresent());
        assertEquals(
                "Іван",
                result.orElseThrow().getAssignee()
        );
    }

    @Test
    void shouldNotMutateInputCollection() {
        List<ProjectTask> tasks = sampleTasks();
        List<ProjectTask> original = List.copyOf(tasks);

        queries.unfinishedTasks(tasks);
        queries.taskTitles(tasks);
        queries.hoursByAssignee(tasks);
        queries.estimateStatistics(tasks);
        queries.topByPriority(tasks, 3);
        queries.findByTitle(
                tasks,
                "Створити дизайн"
        );

        assertEquals(original, tasks);
    }

    @Test
    void shouldHandleEmptyInputForAllQueries() {
        List<ProjectTask> emptyTasks = List.of();

        assertTrue(
                queries.unfinishedTasks(emptyTasks).isEmpty()
        );

        assertTrue(
                queries.taskTitles(emptyTasks).isEmpty()
        );

        assertTrue(
                queries.hoursByAssignee(emptyTasks).isEmpty()
        );

        var statistics =
                queries.estimateStatistics(emptyTasks);

        assertEquals(0, statistics.getCount());
        assertEquals(0.0, statistics.getSum());
        assertEquals(0.0, statistics.getAverage());

        assertTrue(
                queries.topByPriority(emptyTasks, 5).isEmpty()
        );

        assertTrue(
                queries.findByTitle(
                        emptyTasks,
                        "Неіснуюча задача"
                ).isEmpty()
        );
    }
}