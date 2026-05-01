package dtu.projectplanner.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeTest {

    @Test
    void constructorStoresFourLetterInitials() {
        Employee employee = new Employee("Sara", "sara");

        assertEquals("sara", employee.getInitials());
    }

    @Test
    void constructorRejectsMissingInitials() {
        assertThrows(IllegalArgumentException.class, () -> new Employee("Sara", null));
        assertThrows(IllegalArgumentException.class, () -> new Employee("Sara", " "));
    }

    @Test
    void constructorRejectsInitialsLongerThanFourCharacters() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Employee("Sara", "saraa")
        );
    }

    @Test
    void constructorRejectsInitialsWithNonLetters() {
        assertThrows(IllegalArgumentException.class, () -> new Employee("Sara", "sa1"));
        assertThrows(IllegalArgumentException.class, () -> new Employee("Sara", "s!"));
        assertThrows(IllegalArgumentException.class, () -> new Employee("Sara", "&€"));
    }
}
