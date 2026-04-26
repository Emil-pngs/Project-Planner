Feature: EmployeeRepository behavior

  Scenario: Saved employee can be found
    Given employee repository test has empty repository
    When employee repository test saves employee "Anna" with initials "anna"
    Then employee repository test find result should be same instance

  Scenario: Duplicate same instance is ignored
    Given employee repository test has empty repository
    When employee repository test saves same instance twice
    Then employee repository test size should be 1
