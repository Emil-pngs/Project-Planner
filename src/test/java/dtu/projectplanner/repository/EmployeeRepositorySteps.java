package dtu.projectplanner.repository;

import dtu.projectplanner.domain.Employee;
import dtu.projectplanner.repository.EmployeeRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class EmployeeRepositorySteps {

    private EmployeeRepository employeeRepository;
    private Employee employee;
    private Employee foundEmployee;

    @Given("employee repository test has empty repository")
    public void employeeRepositoryTestHasEmptyRepository() {
        employeeRepository = new EmployeeRepository();
    }

    @When("employee repository test saves employee {string} with initials {string}")
    public void employeeRepositoryTestSavesEmployeeWithInitials(String name, String initials) {
        employee = new Employee(name, initials);
        employeeRepository.save(employee);
        foundEmployee = employeeRepository.findByInitials(initials);
    }

    @Then("employee repository test find result should be same instance")
    public void employeeRepositoryTestFindResultShouldBeSameInstance() {
        assertSame(employee, foundEmployee);
    }

    @When("employee repository test saves same instance twice")
    public void employeeRepositoryTestSavesSameInstanceTwice() {
        employee = new Employee("Anna", "anna");
        employeeRepository.save(employee);
        employeeRepository.save(employee);
    }

    @Then("employee repository test size should be {int}")
    public void employeeRepositoryTestSizeShouldBe(Integer expected) {
        assertEquals(expected.intValue(), employeeRepository.findAll().size());
    }
}
