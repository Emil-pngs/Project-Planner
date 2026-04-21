package hellocucumber;

import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {
	
	/* The only purpose of this constructor is to test
	 * if Cucumber Dependency Injection using Picocontainer works.
	 */
	public StepDefinitions(DummyClass tc) {
		
	}

    @Given("an example scenario")
    public void anExampleScenario() {
    }

    @When("all step definitions are implemented")
    public void allStepDefinitionsAreImplemented() {
    }

    @Then("the scenario passes")
    public void theScenarioPasses() {
    }

}
