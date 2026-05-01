Feature: UI behavior

  Scenario: User enters valid credentials
    Given ui test has credential input "huba"
    When ui test validates credentials format
    Then ui test credentials should be true

  Scenario: User enters invalid credentials
    Given ui test has credential input "ab1"
    When ui test validates credentials format
    Then ui test credentials should be false

  Scenario: User toggles dark mode on
    Given ui test dark mode is off
    When ui test user presses dark mode button
    Then ui test dark mode should be on

  Scenario: User toggles dark mode off
    Given ui test dark mode is on
    When ui test user presses dark mode button
    Then ui test dark mode should be off

  Scenario: User creates a project from the UI action
    Given ui test has employee "mash"
    When ui test user "mash" presses create project button for "assignment 1"
    Then ui test created project leader should be "mash"

  Scenario: User cannot access a project they are not assigned to
    Given ui test has project led by "lead" and outsider "outs"
    When ui test user "outs" tries to access current project
    Then ui test operation should fail with message containing "Not allowed to view project"

  Scenario: Viewer can create activity in a project
    Given ui test has project led by "lead" with viewer "view"
    When ui test user "view" tries to create activity in current project
    Then ui test activity should be created

  Scenario: Outsider cannot create activity in a project
    Given ui test has project led by "lead" and outsider "outs"
    When ui test user "outs" tries to create activity in current project
    Then ui test operation should fail with message containing "Not allowed to edit project"

  Scenario: Creating activity in a missing project fails
    Given ui test has employee "lead"
    When ui test user "lead" tries to create activity in project id 999999
    Then ui test operation should fail with message containing "not found"

  Scenario: Project leader can create activity
    Given ui test has project led by "lead"
    When ui test user "lead" tries to create activity in current project
    Then ui test activity should be created

  Scenario: Viewer cannot edit activity in a project
    Given ui test has project led by "lead" with viewer "view" and one activity
    When ui test user "view" tries to edit current activity name to "Updated"
    Then ui test operation should fail with message containing "Not allowed to edit project"

  Scenario: Outsider cannot edit activity in a project
    Given ui test has project led by "lead" and outsider "outs" with one activity
    When ui test user "outs" tries to edit current activity name to "Updated"
    Then ui test operation should fail with message containing "Not allowed to edit project"

  Scenario: Editing activity in a missing project fails
    Given ui test has employee "lead"
    When ui test user "lead" tries to edit activity in project id 999999
    Then ui test operation should fail with message containing "not found"

  Scenario: Viewer cannot edit project viewers
    Given ui test has project led by "lead" with viewer "view"
    When ui test user "view" tries to edit viewers in current project
    Then ui test operation should fail with message containing "Not allowed to edit project"

  Scenario: Outsider cannot edit project viewers
    Given ui test has project led by "lead" and outsider "outs"
    When ui test user "outs" tries to edit viewers in current project
    Then ui test operation should fail with message containing "Not allowed to edit project"

  Scenario: Editing viewers in a missing project fails
    Given ui test has employee "lead"
    When ui test user "lead" tries to edit viewers in project id 999999
    Then ui test operation should fail with message containing "not found"

  Scenario: Project leader can edit project viewers
    Given ui test has project led by "lead" and outsider "outs"
    When ui test user "lead" tries to edit viewers in current project
    Then ui test viewers should contain "outs"

  Scenario: User creates project without assigning another leader
    Given ui test has employee "alic"
    When ui test user "alic" presses create project button for "Proj A"
    Then ui test created project leader should be "alic"

  Scenario: User creates project and assigns another project leader
    Given ui test has employee "alic"
    And ui test has employee "bob"
    When ui test user "alic" presses create project button for "Proj B" and assigns "bob" as project leader
    Then ui test created project leader should be "bob"

  Scenario: User cancels create project dialog
    Given ui test project list size is tracked
    When ui test user opens create project dialog and presses cancel
    Then ui test project list size should be unchanged

  Scenario: Logged in user logs out
    Given ui test user is logged in
    When ui test user logs out
    Then ui test screen should be "homescreen"

  Scenario: User selects a project
    Given ui test has project led by "lead"
    When ui test user selects current project
    Then ui test current project should be highlighted

  Scenario: User selects an activity and sees team members
    Given ui test has project led by "lead" with viewer "view" and one activity assigned to "view"
    When ui test user selects current activity as "view"
    Then ui test current activity should be highlighted
    And ui test team view should include "view"

  Scenario: Project leader cannot remove self as viewer
    Given ui test has project led by "lead"
    When ui test user "lead" tries to remove self from project viewers
    Then ui test viewers should contain "lead"


