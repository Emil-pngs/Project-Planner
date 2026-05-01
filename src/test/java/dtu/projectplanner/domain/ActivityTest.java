package dtu.projectplanner.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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

    @Test
    void constructorRejectsInvalidWeeks() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Activity(1, "Planning", 10, 0, 12)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new Activity(1, "Planning", 10, 10, 54)
        );
    }

    @Test
    void constructorRejectsEndWeekBeforeStartWeek() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Activity(1, "Planning", 10, 12, 10)
        );
    }

    @Test
    void settersRejectInvalidWeek() {
        Activity activity = new Activity(1, "Planning", 10, 10, 12);

        assertThrows(IllegalArgumentException.class, () -> activity.setStartWeek(0));
        assertThrows(IllegalArgumentException.class, () -> activity.setStartWeek(13));
        assertThrows(IllegalArgumentException.class, () -> activity.setEndWeek(54));
        assertThrows(IllegalArgumentException.class, () -> activity.setEndWeek(9));
    }

    @Test
    void duplicateEmployeeAssignmentIsPrevented() {
        Activity activity = new Activity(1, "Planning", 12, 10, 12);
        Employee employee = new Employee("Sara", "sara");

        activity.assignEmployee(employee);
        activity.assignEmployee(employee);

        assertEquals(1, employee.getAssignedActivities().size());
    }

    @Test
    void registeredTimeReducesRemainingHours() {
        Activity activity = new Activity(2, "Implementation", 10, 12, 16);
        Employee employee = new Employee("Mads", "mads");
        activity.assignEmployee(employee);

        activity.registerTimeEntry(new TimeEntry(LocalDate.now(), 3, employee, activity));
        activity.registerTimeEntry(new TimeEntry(LocalDate.now(), 2, employee, activity));

        assertEquals(5, activity.remainingHours());
    }
}
