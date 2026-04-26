package dtu.projectplanner.app;

import dtu.projectplanner.app.DemoDataSeeder;
import dtu.projectplanner.app.ProjectPlanningService;
import dtu.projectplanner.repository.EmployeeRepository;
import dtu.projectplanner.repository.ProjectRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DemoDataSeederSteps {

    private ProjectRepository projectRepository;
    private EmployeeRepository employeeRepository;
    private ProjectPlanningService service;
    private String output;

    @Before
    public void resetState() {
        output = null;
    }

    @Given("demo data seeder test has empty repositories")
    public void demoDataSeederTestHasEmptyRepositories() {
        projectRepository = new ProjectRepository();
        employeeRepository = new EmployeeRepository();
        service = new ProjectPlanningService(projectRepository, employeeRepository);
    }

    @When("demo data seeder test populates once")
    public void demoDataSeederTestPopulatesOnce() throws Exception {
        output = DemoDataSeeder.populate(service, projectRepository, employeeRepository);
    }

    @Then("demo data seeder test first run should mention projects {string}")
    public void demoDataSeederTestFirstRunShouldMentionProjects(String expected) {
        assertNotNull(output);
        assertTrue(output.contains(expected));
    }

    @When("demo data seeder test populates twice")
    public void demoDataSeederTestPopulatesTwice() throws Exception {
        DemoDataSeeder.populate(service, projectRepository, employeeRepository);
        output = DemoDataSeeder.populate(service, projectRepository, employeeRepository);
    }

    @Then("demo data seeder test second run should mention activities {string}")
    public void demoDataSeederTestSecondRunShouldMentionActivities(String expected) {
        assertNotNull(output);
        assertTrue(output.contains(expected));
    }
}
