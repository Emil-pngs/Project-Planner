package dtu.projectplanner.domain;

import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.Employee;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeSteps {

    private Employee employee;
    private Activity activity;
    private boolean availability;

    @Given("employee test has employee {string} with initials {string}")
    public void employeeTestHasEmployeeWithInitials(String name, String initials) {
        employee = new Employee(name, initials);
        activity = new Activity(3, "Design", 8, 11, 12);
    }

    @When("employee test assigns that employee to an activity")
    public void employeeTestAssignsThatEmployeeToAnActivity() {
        activity.assignEmployee(employee);
    }

    @Then("employee test assignment should be {word}")
    public void employeeTestAssignmentShouldBe(String expected) {
        assertEquals(Boolean.parseBoolean(expected), employee.isAssignedTo(activity));
    }

    @Given("employee test has assigned employee {string} with initials {string}")
    public void employeeTestHasAssignedEmployeeWithInitials(String name, String initials) {
        employee = new Employee(name, initials);
        activity = new Activity(4, "Testing", 6, 13, 14);
        activity.assignEmployee(employee);
    }

    @When("employee test checks availability for same activity")
    public void employeeTestChecksAvailabilityForSameActivity() {
        availability = employee.isAvailableFor(activity);
    }

    @Then("employee test availability should be {word}")
    public void employeeTestAvailabilityShouldBe(String expected) {
        assertEquals(Boolean.parseBoolean(expected), availability);
    }
}
