package dtu.projectplanner.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeEntryTest {

    private final Employee employee = new Employee("Kasper", "kasp");
    private final Activity activity = new Activity(1, "Planning", 10, 10, 12);

    @Test
    void constructorStoresPositiveHours() {
        TimeEntry entry = new TimeEntry(LocalDate.now(), 7, employee, activity);

        assertEquals(7, entry.getHours());
    }

    @Test
    void constructorRejectsZeroAndNegativeHours() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new TimeEntry(LocalDate.now(), 0, employee, activity)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new TimeEntry(LocalDate.now(), -1, employee, activity)
        );
    }
}
