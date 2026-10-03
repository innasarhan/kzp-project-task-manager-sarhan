import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskKindTest {

    @Test
    void shouldContainRequiredTaskKinds() {
        assertEquals(2, TaskKind.values().length);
        assertNotEquals(TaskKind.DEVELOPMENT, TaskKind.TESTING);
    }

    @Test
    void shouldReturnCorrectNames() {
        assertEquals("DEVELOPMENT", TaskKind.DEVELOPMENT.name());
        assertEquals("TESTING", TaskKind.TESTING.name());
    }
}
