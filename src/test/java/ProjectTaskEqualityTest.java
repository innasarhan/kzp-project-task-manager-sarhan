import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ProjectTaskEqualityTest {

    @Test
    void shouldBeEqualForSameBaseTasks() {
        ProjectTask first = new ProjectTask(
                "Розробити API",
                "Іван",
                12.5,
                1,
                true
        );

        ProjectTask second = new ProjectTask(
                "Розробити API",
                "Іван",
                12.5,
                1,
                true
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldWorkCorrectlyInHashSet() {
        Set<ProjectTask> tasks = new HashSet<>();

        tasks.add(new ProjectTask(
                "Розробити API",
                "Іван",
                12.5,
                1,
                true
        ));

        tasks.add(new ProjectTask(
                "Розробити API",
                "Іван",
                12.5,
                1,
                true
        ));

        assertEquals(1, tasks.size());
    }

    @Test
    void shouldDistinguishDifferentSubtypes() {
        ProjectTask development = new DevelopmentTask(
                "Розробити API",
                "Іван",
                12.5,
                1,
                true
        );

        ProjectTask testing = new TestingTask(
                "Розробити API",
                "Іван",
                12.5,
                1,
                true
        );

        assertNotEquals(development, testing);
    }
}
