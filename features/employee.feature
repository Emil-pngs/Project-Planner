Feature: Employee domain behavior

  Scenario: Employee assignment is tracked
    Given employee test has employee "Sara" with initials "sara"
    When employee test assigns that employee to an activity
    Then employee test assignment should be true

  Scenario: Assigned employee is not available for same activity
    Given employee test has assigned employee "Jens" with initials "jens"
    When employee test checks availability for same activity
    Then employee test availability should be false
