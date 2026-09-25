import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ProjectTaskTest {

    @Test
    void shouldCreateValidProjectTask() {
        ProjectTask task = new ProjectTask(
                "Розробити API",
                "Іван",
                12.5,
                1,
                true
        );

        assertEquals("Розробити API", task.getTitle());
        assertEquals("Іван", task.getAssignee());
        assertEquals(12.5, task.getEstimateHours());
        assertEquals(1, task.getPriority());
        assertEquals(true, task.isDone());
    }

    @Test
    void shouldRejectNullTitle() {
        assertThrows(
                NullPointerException.class,
                () -> new ProjectTask(
                        null,
                        "Іван",
                        10.0,
                        1,
                        true
                )
        );
    }

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProjectTask(
                        "   ",
                        "Іван",
                        10.0,
                        1,
                        true
                )
        );
    }

    @Test
    void shouldRejectNullAssignee() {
        assertThrows(
                NullPointerException.class,
                () -> new ProjectTask(
                        "Розробити API",
                        null,
                        10.0,
                        1,
                        true
                )
        );
    }

    @Test
    void shouldRejectBlankAssignee() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProjectTask(
                        "Розробити API",
                        "   ",
                        10.0,
                        1,
                        true
                )
        );
    }

    @Test
    void shouldRejectNegativeEstimate() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProjectTask(
                        "Розробити API",
                        "Іван",
                        -1.0,
                        1,
                        true
                )
        );
    }

    @Test
    void shouldRejectNegativePriority() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProjectTask(
                        "Розробити API",
                        "Іван",
                        10.0,
                        -1,
                        true
                )
        );
    }

    @Test
    void shouldAcceptZeroEstimateAndPriority() {
        ProjectTask task = new ProjectTask(
                "Підготовка",
                "Іван",
                0.0,
                0,
                false
        );

        assertEquals(0.0, task.getEstimateHours());
        assertEquals(0, task.getPriority());
    }

    @Test
    void shouldRejectNaNEstimate() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProjectTask(
                        "Розробити API",
                        "Іван",
                        Double.NaN,
                        1,
                        true
                )
        );
    }
}