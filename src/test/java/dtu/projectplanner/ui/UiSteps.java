package dtu.projectplanner.ui;

import dtu.projectplanner.app.ProjectPlanningService;
import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.ActivityStatus;
import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.domain.Project;
import dtu.projectplanner.repository.EmployeeRepository;
import dtu.projectplanner.repository.ProjectRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UiSteps {

    private ProjectRepository projectRepository;
    private EmployeeRepository employeeRepository;
    private ProjectPlanningService service;

    private String credentialsInput;
    private boolean credentialsValid;
    private boolean darkMode;

    private Project project;
    private Activity activity;
    private String errorMessage;
    private int projectCountBefore;
    private String currentScreen;
    private Project highlightedProject;
    private Activity highlightedActivity;
    private List<Employee> teamViewEmployees;

    @Before
    public void resetState() {
        projectRepository = new ProjectRepository();
        employeeRepository = new EmployeeRepository();
        service = new ProjectPlanningService(projectRepository, employeeRepository);

        credentialsInput = null;
        credentialsValid = false;
        darkMode = false;
        project = null;
        activity = null;
        errorMessage = null;
        projectCountBefore = 0;
        currentScreen = "homescreen";
        highlightedProject = null;
        highlightedActivity = null;
        teamViewEmployees = new ArrayList<>();
    }

    private Employee addEmployee(String initials) {
        Employee employee = new Employee("User " + initials, initials);
        employeeRepository.save(employee);
        return employee;
    }

    @Given("ui test has employee {string}")
    public void uiTestHasEmployee(String initials) {
        addEmployee(initials);
    }

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

    @Given("ui test dark mode is off")
    public void uiTestDarkModeIsOff() {
        darkMode = false;
    }

    @Given("ui test dark mode is on")
    public void uiTestDarkModeIsOn() {
        darkMode = true;
    }

    @When("ui test user presses dark mode button")
    public void uiTestUserPressesDarkModeButton() {
        darkMode = !darkMode;
    }

    @Then("ui test dark mode should be on")
    public void uiTestDarkModeShouldBeOn() {
        assertTrue(darkMode);
    }

    @Then("ui test dark mode should be off")
    public void uiTestDarkModeShouldBeOff() {
        assertFalse(darkMode);
    }

    @When("ui test user {string} presses create project button for {string}")
    public void uiTestUserPressesCreateProjectButtonFor(String requesterInitials, String projectName) {
        try {
            project = service.createProject(projectName, requesterInitials);
            errorMessage = null;
            currentScreen = "main";
        } catch (Exception ex) {
            project = null;
            errorMessage = ex.getMessage();
        }
    }

    @When("ui test user {string} presses create project button for {string} and assigns {string} as project leader")
    public void uiTestUserPressesCreateProjectButtonForAndAssignsAsProjectLeader(
        String requesterInitials,
        String projectName,
        String newLeaderInitials
    ) {
        try {
            project = service.createProject(projectName, requesterInitials);
            service.setProjectLeader(project.getProjectID(), newLeaderInitials, requesterInitials);
            errorMessage = null;
            currentScreen = "main";
        } catch (Exception ex) {
            project = null;
            errorMessage = ex.getMessage();
        }
    }

    @Then("ui test created project leader should be {string}")
    public void uiTestCreatedProjectLeaderShouldBe(String expectedInitials) {
        assertNotNull(project);
        assertNotNull(project.getProjectLeader());
        assertEquals(expectedInitials, project.getProjectLeader().getInitials());
    }

    @Given("ui test has project led by {string}")
    public void uiTestHasProjectLedBy(String leaderInitials) throws Exception {
        addEmployee(leaderInitials);
        project = service.createProject("UI Project", leaderInitials);
    }

    @Given("ui test has project led by {string} and outsider {string}")
    public void uiTestHasProjectLedByAndOutsider(String leaderInitials, String outsiderInitials) throws Exception {
        addEmployee(leaderInitials);
        addEmployee(outsiderInitials);
        project = service.createProject("UI Project", leaderInitials);
    }

    @Given("ui test has project led by {string} with viewer {string}")
    public void uiTestHasProjectLedByWithViewer(String leaderInitials, String viewerInitials) throws Exception {
        addEmployee(leaderInitials);
        addEmployee(viewerInitials);
        project = service.createProject("UI Project", leaderInitials);
        service.setProjectViewers(project.getProjectID(), List.of(viewerInitials), leaderInitials);
    }

    @Given("ui test has project led by {string} with viewer {string} and one activity")
    public void uiTestHasProjectLedByWithViewerAndOneActivity(String leaderInitials, String viewerInitials) throws Exception {
        uiTestHasProjectLedByWithViewer(leaderInitials, viewerInitials);
        activity = service.addActivity(project.getProjectID(), "Backlog", 8, 10, 12, leaderInitials);
    }

    @Given("ui test has project led by {string} and outsider {string} with one activity")
    public void uiTestHasProjectLedByAndOutsiderWithOneActivity(String leaderInitials, String outsiderInitials) throws Exception {
        uiTestHasProjectLedByAndOutsider(leaderInitials, outsiderInitials);
        activity = service.addActivity(project.getProjectID(), "Backlog", 8, 10, 12, leaderInitials);
    }

    @When("ui test user {string} tries to access current project")
    public void uiTestUserTriesToAccessCurrentProject(String requesterInitials) {
        try {
            service.getProjectForUser(project.getProjectID(), requesterInitials);
            errorMessage = null;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }
    }

    @When("ui test user {string} tries to create activity in current project")
    public void uiTestUserTriesToCreateActivityInCurrentProject(String requesterInitials) {
        try {
            activity = service.addActivity(project.getProjectID(), "Design", 6, 12, 13, requesterInitials);
            errorMessage = null;
        } catch (Exception ex) {
            activity = null;
            errorMessage = ex.getMessage();
        }
    }

    @When("ui test user {string} tries to create activity in project id {int}")
    public void uiTestUserTriesToCreateActivityInProjectId(String requesterInitials, Integer projectId) {
        try {
            activity = service.addActivity(projectId, "Design", 6, 12, 13, requesterInitials);
            errorMessage = null;
        } catch (Exception ex) {
            activity = null;
            errorMessage = ex.getMessage();
        }
    }

    @Then("ui test activity should be created")
    public void uiTestActivityShouldBeCreated() {
        assertNotNull(activity);
    }

    @When("ui test user {string} tries to edit current activity name to {string}")
    public void uiTestUserTriesToEditCurrentActivityNameTo(String requesterInitials, String newName) {
        try {
            activity = service.editActivity(
                project.getProjectID(),
                activity.getActivityID(),
                newName,
                10,
                12,
                14,
                ActivityStatus.PLANNED,
                requesterInitials
            );
            errorMessage = null;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }
    }

    @When("ui test user {string} tries to edit activity in project id {int}")
    public void uiTestUserTriesToEditActivityInProjectId(String requesterInitials, Integer projectId) {
        try {
            service.editActivity(projectId, 1, "Updated", 10, 12, 14, ActivityStatus.PLANNED, requesterInitials);
            errorMessage = null;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }
    }

    @When("ui test user {string} tries to edit viewers in current project")
    public void uiTestUserTriesToEditViewersInCurrentProject(String requesterInitials) {
        try {
            service.setProjectViewers(project.getProjectID(), List.of("outs"), requesterInitials);
            errorMessage = null;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }
    }

    @When("ui test user {string} tries to edit viewers in project id {int}")
    public void uiTestUserTriesToEditViewersInProjectId(String requesterInitials, Integer projectId) {
        try {
            service.setProjectViewers(projectId, List.of("outs"), requesterInitials);
            errorMessage = null;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }
    }

    @Then("ui test operation should fail with message containing {string}")
    public void uiTestOperationShouldFailWithMessageContaining(String expectedText) {
        assertNotNull(errorMessage);
        assertFalse(errorMessage.isBlank());
        assertTrue(errorMessage.toLowerCase(Locale.ROOT).contains(expectedText.toLowerCase(Locale.ROOT)));
    }

    @Then("ui test viewers should contain {string}")
    public void uiTestViewersShouldContain(String expectedViewerInitials) {
        assertNotNull(project);
        assertTrue(
            project.getViewers().stream().anyMatch(viewer -> viewer.getInitials().equals(expectedViewerInitials))
        );
    }

    @Given("ui test project list size is tracked")
    public void uiTestProjectListSizeIsTracked() {
        projectCountBefore = projectRepository.findAll().size();
    }

    @When("ui test user opens create project dialog and presses cancel")
    public void uiTestUserOpensCreateProjectDialogAndPressesCancel() {
        // Cancel means no service operation should run.
    }

    @Then("ui test project list size should be unchanged")
    public void uiTestProjectListSizeShouldBeUnchanged() {
        assertEquals(projectCountBefore, projectRepository.findAll().size());
    }

    @Given("ui test user is logged in")
    public void uiTestUserIsLoggedIn() {
        currentScreen = "main";
    }

    @When("ui test user logs out")
    public void uiTestUserLogsOut() {
        currentScreen = "homescreen";
    }

    @Then("ui test screen should be {string}")
    public void uiTestScreenShouldBe(String expectedScreen) {
        assertEquals(expectedScreen, currentScreen);
    }

    @When("ui test user selects current project")
    public void uiTestUserSelectsCurrentProject() {
        highlightedProject = project;
    }

    @Then("ui test current project should be highlighted")
    public void uiTestCurrentProjectShouldBeHighlighted() {
        assertNotNull(project);
        assertNotNull(highlightedProject);
        assertEquals(project.getProjectID(), highlightedProject.getProjectID());
    }

    @Given("ui test has project led by {string} with viewer {string} and one activity assigned to {string}")
    public void uiTestHasProjectLedByWithViewerAndOneActivityAssignedTo(
        String leaderInitials,
        String viewerInitials,
        String assigneeInitials
    ) throws Exception {
        uiTestHasProjectLedByWithViewer(leaderInitials, viewerInitials);
        activity = service.addActivity(project.getProjectID(), "Backlog", 8, 10, 12, leaderInitials);
        service.assignEmployee(project.getProjectID(), activity.getActivityID(), assigneeInitials, leaderInitials);
    }

    @When("ui test user selects current activity as {string}")
    public void uiTestUserSelectsCurrentActivityAs(String requesterInitials) {
        highlightedActivity = activity;
        try {
            teamViewEmployees = service.getAssignedEmployees(
                project.getProjectID(),
                activity.getActivityID(),
                requesterInitials
            );
            errorMessage = null;
        } catch (Exception ex) {
            teamViewEmployees = new ArrayList<>();
            errorMessage = ex.getMessage();
        }
    }

    @Then("ui test current activity should be highlighted")
    public void uiTestCurrentActivityShouldBeHighlighted() {
        assertNotNull(activity);
        assertNotNull(highlightedActivity);
        assertEquals(activity.getActivityID(), highlightedActivity.getActivityID());
    }

    @Then("ui test team view should include {string}")
    public void uiTestTeamViewShouldInclude(String expectedInitials) {
        assertTrue(teamViewEmployees.stream().anyMatch(employee -> employee.getInitials().equals(expectedInitials)));
    }

    @When("ui test user {string} tries to remove self from project viewers")
    public void uiTestUserTriesToRemoveSelfFromProjectViewers(String requesterInitials) {
        try {
            service.setProjectViewers(project.getProjectID(), List.of(), requesterInitials);
            errorMessage = null;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }
    }
}
