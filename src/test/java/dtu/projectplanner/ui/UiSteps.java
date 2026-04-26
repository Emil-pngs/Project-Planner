package dtu.projectplanner.ui;

import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.domain.Project;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UiSteps {

    private String credentialsInput;
    private boolean credentialsValid;
    private Project project;
    private Employee employee;
    private boolean createActivityButtonEnabled;

    @Given("ui test has credential input {string}")
    public void uiTestHasCredentialInput(String input) {
        credentialsInput = input;
    }

    @When("ui test validates credentials format")
    public void uiTestValidatesCredentialsFormat() {
        String raw = credentialsInput == null ? "" : credentialsInput.trim().toLowerCase(Locale.ROOT);
        credentialsValid = raw.matches("[a-zA-Z]{4,}");
    }

    @Then("ui test credentials should be {word}")
    public void uiTestCredentialsShouldBe(String expected) {
        assertEquals(Boolean.parseBoolean(expected), credentialsValid);
    }

    @Given("ui test project leader is {string} and current user is {string}")
    public void uiTestProjectLeaderIsAndCurrentUserIs(String leaderInitials, String currentUserInitials) {
        project = new Project(260099, "UI Project");
        Employee leader = new Employee("Leader", leaderInitials);
        project.setProjectLeader(leader);
        employee = new Employee("Current User", currentUserInitials);
    }

    @When("ui test checks whether create activity button is enabled")
    public void uiTestChecksWhetherCreateActivityButtonIsEnabled() {
        boolean isLeader = project != null
            && project.getProjectLeader() != null
            && employee != null
            && project.getProjectLeader().getInitials().equals(employee.getInitials());
        createActivityButtonEnabled = isLeader;
    }

    @Then("ui test create activity button enabled should be {word}")
    public void uiTestCreateActivityButtonEnabledShouldBe(String expected) {
        assertEquals(Boolean.parseBoolean(expected), createActivityButtonEnabled);
    }
}
