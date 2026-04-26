Feature: Activity domain behavior

  Scenario: Duplicate employee assignment is prevented
    Given activity test has activity with budget 12
    When activity test assigns same employee twice
    Then activity test employee assignment count should be 1

  Scenario: Registered time reduces remaining hours
    Given activity test has tracked activity budget 10
    When activity test registers hours 3 and 2
    Then activity test remaining hours should be 5
