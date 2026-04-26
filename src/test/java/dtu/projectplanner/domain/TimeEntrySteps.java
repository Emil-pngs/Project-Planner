package dtu.projectplanner.domain;

import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.domain.TimeEntry;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeEntrySteps {

    private TimeEntry timeEntry;

    @Given("time entry test has entry with hours {int}")
    public void timeEntryTestHasEntryWithHours(Integer hours) {
        Employee entryEmployee = new Employee("Kasper", "kasp");
        Activity entryActivity = new Activity(11, "Refactor", 20, 9, 13);
        timeEntry = new TimeEntry(LocalDate.now(), hours, entryEmployee, entryActivity);
    }

    @Then("time entry test getHours should return {int}")
    public void timeEntryTestGetHoursShouldReturn(Integer expected) {
        assertEquals(expected.intValue(), timeEntry.getHours());
    }
}
