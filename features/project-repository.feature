Feature: ProjectRepository behavior

  Scenario: Saved project can be found by id
    Given project repository test has empty repository
    When project repository test saves project id 26001 named "Planner"
    Then project repository test should return same project instance

  Scenario: Next id uses highest yearly sequence
    Given project repository test has current year sequences 3 and 7
    When project repository test generates next id
    Then project repository test next id sequence should be 8
