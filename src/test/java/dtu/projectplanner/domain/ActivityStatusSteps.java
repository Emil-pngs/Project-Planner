package dtu.projectplanner.domain;

import dtu.projectplanner.domain.ActivityStatus;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActivityStatusSteps {

    private ActivityStatus resolvedStatus;

    @When("activity status test resolves status {string}")
    public void activityStatusTestResolvesStatus(String statusName) {
        resolvedStatus = ActivityStatus.valueOf(statusName);
    }

    @Then("activity status test resolved value should be {string}")
    public void activityStatusTestResolvedValueShouldBe(String expected) {
        assertEquals(expected, resolvedStatus.name());
    }

    @Then("activity status test count should be {int}")
    public void activityStatusTestCountShouldBe(Integer expected) {
        assertEquals(expected.intValue(), ActivityStatus.values().length);
    }
}
