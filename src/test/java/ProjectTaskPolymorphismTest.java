import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ProjectTaskPolymorphismTest {

    @Test
    void shouldUsePolymorphicPriorityEvaluation() {
        List<ProjectTask> tasks = List.of(
                new DevelopmentTask(
                        "Розробити API",
                        "Іван",
                        12.5,
                        1,
                        true
                ),
                new TestingTask(
                        "Написати тести",
                        "Андрій",
                        5.5,
                        3,
                        false
                )
        );

        assertEquals(2, tasks.get(0).evaluatePriority());
        assertEquals(4, tasks.get(1).evaluatePriority());
    }

    @Test
    void shouldReturnCorrectTaskKinds() {
        ProjectTask development = new DevelopmentTask(
                "Розробити API",
                "Іван",
                12.5,
                1,
                true
        );

        ProjectTask testing = new TestingTask(
                "Написати тести",
                "Андрій",
                5.5,
                3,
                false
        );

        assertEquals(TaskKind.DEVELOPMENT, development.getKind());
        assertEquals(TaskKind.TESTING, testing.getKind());
    }

    @Test
    void baseProjectTaskShouldKeepOriginalPriorityBehavior() {
        ProjectTask task = new ProjectTask(
                "Загальна задача",
                "Олена",
                8.0,
                2,
                false
        );

        assertEquals(2, task.evaluatePriority());
        assertEquals(TaskKind.DEVELOPMENT, task.getKind());
    }
}
