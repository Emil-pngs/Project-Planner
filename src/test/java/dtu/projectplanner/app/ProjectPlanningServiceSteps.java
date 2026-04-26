package dtu.projectplanner.app;

import dtu.projectplanner.app.ProjectPlanningService;
import dtu.projectplanner.domain.Activity;
import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.domain.Project;
import dtu.projectplanner.domain.TimeEntry;
import dtu.projectplanner.repository.EmployeeRepository;
import dtu.projectplanner.repository.ProjectRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectPlanningServiceSteps {

    private ProjectRepository projectRepository;
    private EmployeeRepository employeeRepository;
    private ProjectPlanningService service;
    private Employee leader;
    private Employee worker;
    private Project createdProject;
    private Activity activity;
    private String errorMessage;

    @Before
    public void resetState() {
        projectRepository = new ProjectRepository();
        employeeRepository = new EmployeeRepository();
        service = new ProjectPlanningService(projectRepository, employeeRepository);
        leader = null;
        worker = null;
        createdProject = null;
        activity = null;
        errorMessage = null;
    }

    @Given("project planning service test has employee {string}")
    public void projectPlanningServiceTestHasEmployee(String initials) {
        employeeRepository.save(new Employee("User " + initials, initials));
    }

    @When("project planning service test creates project {string} by {string}")
    public void projectPlanningServiceTestCreatesProjectBy(String name, String requesterInitials) throws Exception {
        createdProject = service.createProject(name, requesterInitials);
    }

    @Then("project planning service test leader initials should be {string}")
    public void projectPlanningServiceTestLeaderInitialsShouldBe(String expected) {
        assertNotNull(createdProject);
        assertNotNull(createdProject.getProjectLeader());
        assertEquals(expected, createdProject.getProjectLeader().getInitials());
    }

    @Given("project planning service test has leader {string} and worker {string} with one activity")
    public void projectPlanningServiceTestHasLeaderAndWorkerWithOneActivity(String leaderInitials, String workerInitials) throws Exception {
        leader = new Employee("Leader", leaderInitials);
        worker = new Employee("Worker", workerInitials);
        employeeRepository.save(leader);
        employeeRepository.save(worker);

        createdProject = service.createProject("Service Project", leaderInitials);
        activity = service.addActivity(createdProject.getProjectID(), "Backlog", 12, 15, 17, leaderInitials);
    }

    @When("project planning service test checks registration error")
    public void projectPlanningServiceTestChecksRegistrationError() {
        try {
            TimeEntry entry = new TimeEntry(LocalDate.now(), 2, worker, activity);
            service.registerTime(
                createdProject.getProjectID(),
                activity.getActivityID(),
                worker.getInitials(),
                entry,
                leader.getInitials()
            );
            errorMessage = "";
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }
    }

    @Then("project planning service test should report assignment error")
    public void projectPlanningServiceTestShouldReportAssignmentError() {
        assertNotNull(errorMessage);
        assertTrue(errorMessage.toLowerCase().contains("not assigned"));
    }
}
