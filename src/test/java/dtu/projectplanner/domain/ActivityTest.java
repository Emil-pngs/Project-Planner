package dtu.projectplanner.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActivityTest {

    @Test
    void storeZeroBudgetedHours() {
        Activity activity = new Activity(1, "Planning", 0, 10, 12);

        assertEquals(0, activity.getBudgetedHours());
    }

    @Test
    void rejectNegativeBudgetedHours() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Activity(1, "Planning", -1, 10, 12)
        );
    }

    @Test
    void setterRejectsNegativeValue() {
        Activity activity = new Activity(1, "Planning", 10, 10, 12);

        assertThrows(
            IllegalArgumentException.class,
            () -> activity.setBudgetedHours(-1)
        );
    }
}
