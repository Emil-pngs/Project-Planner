Feature: DemoDataSeeder behavior

  Scenario: First population creates demo projects
    Given demo data seeder test has empty repositories
    When demo data seeder test populates once
    Then demo data seeder test first run should mention projects "+3 projects"

  Scenario: Second population does not add activities again
    Given demo data seeder test has empty repositories
    When demo data seeder test populates twice
    Then demo data seeder test second run should mention activities "+0 activities"
