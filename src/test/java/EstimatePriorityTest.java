import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class EstimatePriorityTest {

    @Test
    void shouldProvideAccessors() {
        EstimatePriority value = new EstimatePriority(12.5, 2);

        assertEquals(12.5, value.estimateHours());
        assertEquals(2, value.priority());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        EstimatePriority first = new EstimatePriority(12.5, 2);
        EstimatePriority second = new EstimatePriority(12.5, 2);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesDiffer() {
        EstimatePriority first = new EstimatePriority(12.5, 2);
        EstimatePriority second = new EstimatePriority(8.0, 1);

        assertNotEquals(first, second);
    }
}