Feature: TimeEntry behavior

  Scenario: Positive hours are returned
    Given time entry test has entry with hours 7
    Then time entry test getHours should return 7

  Scenario: Negative hours are preserved
    Given time entry test has entry with hours -2
    Then time entry test getHours should return -2
