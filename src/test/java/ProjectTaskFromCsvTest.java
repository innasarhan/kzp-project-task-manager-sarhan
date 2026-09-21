import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ProjectTaskFromCsvTest {

    @Test
    void shouldCreateProjectTaskFromValidCsv() {
        ProjectTask task = ProjectTask.fromCsv(
                "Розробити API;Іван;12.5;1;true"
        );

        assertEquals("Розробити API", task.getTitle());
        assertEquals("Іван", task.getAssignee());
        assertEquals(12.5, task.getEstimateHours());
        assertEquals(1, task.getPriority());
        assertEquals(true, task.isDone());
    }

    @Test
    void shouldRejectWrongNumberOfFields() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ProjectTask.fromCsv(
                        "Розробити API;Іван;12.5"
                )
        );
    }

    @Test
    void shouldRejectInvalidEstimate() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ProjectTask.fromCsv(
                        "Розробити API;Іван;abc;1;true"
                )
        );
    }

    @Test
    void shouldRejectNegativeEstimate() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ProjectTask.fromCsv(
                        "Розробити API;Іван;-4.0;1;true"
                )
        );
    }

    @Test
    void shouldRejectInvalidPriority() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ProjectTask.fromCsv(
                        "Розробити API;Іван;12.5;abc;true"
                )
        );
    }

    @Test
    void shouldRejectNegativePriority() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ProjectTask.fromCsv(
                        "Розробити API;Іван;12.5;-1;true"
                )
        );
    }

    @Test
    void shouldRejectInvalidDoneValue() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ProjectTask.fromCsv(
                        "Розробити API;Іван;12.5;1;yes"
                )
        );
    }
}