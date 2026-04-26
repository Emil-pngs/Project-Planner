Feature: UI behavior

  Scenario: User enters invalid credentials
    Given ui test has credential input "ab1"
    When ui test validates credentials format
    Then ui test credentials should be false

  Scenario: User enters valid credentials
    Given ui test has credential input "huba"
    When ui test validates credentials format
    Then ui test credentials should be true

  Scenario: Non-leader user cannot use create activity button
    Given ui test project leader is "lead" and current user is "work"
    When ui test checks whether create activity button is enabled
    Then ui test create activity button enabled should be false

  Scenario: Project leader can use create activity button
    Given ui test project leader is "lead" and current user is "lead"
    When ui test checks whether create activity button is enabled
    Then ui test create activity button enabled should be true
