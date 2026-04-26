Feature: ActivityStatus enum behavior

  Scenario: Known enum value resolves
    When activity status test resolves status "PLANNED"
    Then activity status test resolved value should be "PLANNED"

  Scenario: Enum size is stable
    Then activity status test count should be 3
