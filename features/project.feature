Feature: Project domain behavior

  Scenario: Setting leader grants view access
    Given project test has project id 260001
    When project test sets leader
    Then project test leader should have view access

  Scenario: Remaining work reflects registered time
    Given project test has project with one activity budget 10
    When project test calculates remaining work
    Then project test remaining work should be 6
