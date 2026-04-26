package dtu.projectplanner.domain;

import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.domain.Project;
import dtu.projectplanner.domain.TimeEntry;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectSteps {

    private Project project;
    private Employee leader;
    private Activity activity;
    private Employee employee;
    private int remainingHours;

    @Given("project test has project id {int}")
    public void projectTestHasProjectId(Integer id) {
        project = new Project(id, "Alpha");
    }

    @When("project test sets leader")
    public void projectTestSetsLeader() {
        leader = new Employee("Anna", "anna");
        project.setProjectLeader(leader);
    }

    @Then("project test leader should have view access")
    public void projectTestLeaderShouldHaveViewAccess() {
        assertTrue(project.canBeViewedBy(leader));
    }

    @Given("project test has project with one activity budget {int}")
    public void projectTestHasProjectWithOneActivityBudget(Integer budget) {
        project = new Project(260001, "Beta");
        activity = new Activity(10, "Build", budget, 10, 11);
        employee = new Employee("Mads", "mads");
        project.addActivity(activity);
    }

    @When("project test calculates remaining work")
    public void projectTestCalculatesRemainingWork() {
        project.registerTime(activity.getActivityID(), new TimeEntry(LocalDate.now(), 4, employee, activity));
        remainingHours = project.getRemainingWork();
    }

    @Then("project test remaining work should be {int}")
    public void projectTestRemainingWorkShouldBe(Integer expected) {
        assertEquals(expected.intValue(), remainingHours);
    }
}
