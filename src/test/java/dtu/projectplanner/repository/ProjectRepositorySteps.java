package dtu.projectplanner.repository;

import dtu.projectplanner.domain.Project;
import dtu.projectplanner.repository.ProjectRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ProjectRepositorySteps {

    private ProjectRepository projectRepository;
    private Project project;
    private Project foundProject;
    private int generatedProjectId;

    @Before
    public void resetState() {
        projectRepository = new ProjectRepository();
        project = null;
        foundProject = null;
        generatedProjectId = 0;
    }

    @Given("project repository test has empty repository")
    public void projectRepositoryTestHasEmptyRepository() {
        projectRepository = new ProjectRepository();
    }

    @When("project repository test saves project id {int} named {string}")
    public void projectRepositoryTestSavesProjectIdNamed(Integer id, String name) {
        project = new Project(id, name);
        projectRepository.save(project);
        foundProject = projectRepository.findByID(id);
    }

    @Then("project repository test should return same project instance")
    public void projectRepositoryTestShouldReturnSameProjectInstance() {
        assertSame(project, foundProject);
    }

    @Given("project repository test has current year sequences {int} and {int}")
    public void projectRepositoryTestHasCurrentYearSequencesAnd(Integer first, Integer second) {
        int year = LocalDate.now().getYear() % 100;
        int prefix = year * 1000;
        projectRepository.save(new Project(prefix + first, "A"));
        projectRepository.save(new Project(prefix + second, "B"));
    }

    @When("project repository test generates next id")
    public void projectRepositoryTestGeneratesNextId() {
        generatedProjectId = projectRepository.generateNextID();
    }

    @Then("project repository test next id sequence should be {int}")
    public void projectRepositoryTestNextIdSequenceShouldBe(Integer expected) {
        assertEquals(expected.intValue(), generatedProjectId % 1000);
    }
}
