package dtu.projectplanner.domain;

import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.domain.TimeEntry;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActivitySteps {

    private Activity activity;
    private Employee employee;

    @Given("activity test has activity with budget {int}")
    public void activityTestHasActivityWithBudget(Integer budget) {
        activity = new Activity(1, "Planning", budget, 10, 12);
        employee = new Employee("Sara", "sara");
    }

    @When("activity test assigns same employee twice")
    public void activityTestAssignsSameEmployeeTwice() {
        activity.assignEmployee(employee);
        activity.assignEmployee(employee);
    }

    @Then("activity test employee assignment count should be {int}")
    public void activityTestEmployeeAssignmentCountShouldBe(Integer expected) {
        assertEquals(expected.intValue(), employee.getAssignedActivities().size());
    }

    @Given("activity test has tracked activity budget {int}")
    public void activityTestHasTrackedActivityBudget(Integer budget) {
        activity = new Activity(2, "Implementation", budget, 12, 16);
        employee = new Employee("Mads", "mads");
        activity.assignEmployee(employee);
    }

    @When("activity test registers hours {int} and {int}")
    public void activityTestRegistersHoursAnd(Integer first, Integer second) {
        activity.registerTimeEntry(new TimeEntry(LocalDate.now(), first, employee, activity));
        activity.registerTimeEntry(new TimeEntry(LocalDate.now(), second, employee, activity));
    }

    @Then("activity test remaining hours should be {int}")
    public void activityTestRemainingHoursShouldBe(Integer expected) {
        assertEquals(expected.intValue(), activity.remainingHours());
    }
}
