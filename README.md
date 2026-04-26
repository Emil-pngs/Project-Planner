# Project Planner - Exam Project in SWE1 (Group 8)

A project planning and time registration application developed for Softwarehuset A/S as part of the course **02161 Software Engineering 1** (Spring 2026).

## Overview

The system helps project leaders and employees manage software development projects, track activity progress, and register worked hours. It supports two roles:

- **Project Leader**: create and manage projects, add and schedule activities, assign employees, set project leaders, and track budgeted vs. registered hours.
- **Employee**: view assigned activities, register worked hours on activities, and check remaining work.

### Key Functionality

- Create projects with auto-assigned numbers in the format `YYXXX` (e.g., `26001`, `26002`)
- Add activities to projects with a name, budgeted hours, and a week-level start/end range (e.g., week 10–12, 2026)
- Assign one or more employees to an activity
- Register time entries on activities
- View available employees for an activity (those not already assigned)
- Track activity status: `PLANNED`, `IN_PROGRESS`, or `COMPLETED`
- Generate a project report showing budgeted hours, registered hours, and remaining work
- Fixed activities for vacation, sick leave, courses, etc.

### Domain Model

| Class | Responsibility |
|---|---|
| `Project` | Holds activities, project leader, and aggregates hours |
| `Activity` | Tracks assigned employees, time entries, status, and week schedule |
| `Employee` | Identified by initials (≤4 chars); knows their assigned activities |
| `TimeEntry` | Records date, hours, employee, and activity for a single registration |
| `ActivityStatus` | Enum: `PLANNED`, `IN_PROGRESS`, `COMPLETED` |

## Requirements

- Java 17 or higher
- (Optionally) JDK 25.0.2 or higher
- (Optionally) GNU make 4.4.1 or higher
- (Optionally) Apache Maven 3.9.11 or higher
- (Optionally) Dart Sass CLI 1.77.8 or higher

> Make, Maven, and Sass are optional tools used when building or styling from source. The prebuilt jar only requires Java.

## How to run the Application

**Using make:**

```
make run
```

**Using Maven directly:**

```
mvn javafx:run
```

**Run the prebuilt jar:**

```
java -jar target/ProjectPlanner.jar
```

**Build the jar yourself, then run it:**

```
make build
java -jar target/ProjectPlanner.jar
```

## How to run Cucumber Tests

Run all tests (JUnit 5 + Cucumber):

```
mvn test
```

Or with Make:

```
make test
```

Test reports are written to `target/surefire-reports/`.
