Feature: ProjectPlanningService behavior

  Scenario: Project creation assigns requester as leader
    Given project planning service test has employee "anna"
    When project planning service test creates project "Campus" by "anna"
    Then project planning service test leader initials should be "anna"

  Scenario: Time registration fails when worker is unassigned
    Given project planning service test has leader "anna" and worker "mads" with one activity
    When project planning service test checks registration error
    Then project planning service test should report assignment error
