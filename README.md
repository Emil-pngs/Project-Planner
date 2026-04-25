# Project Planner - Exam Project in SWE1 (Group 8)


Project planning application for Softwarehuset A/S. It currently supports two roles:

- **Project Leader**: create and manage projects, assign activities, set project leaders, and track budgeted hours.
- **Employee**: view assigned activities and register worked hours.

## Requirements

- Java 17 or higher
- (Optionally) JDK 25.0.2 or higher
- (Optionally) GNU make 4.4.1 or higher
- (Optionally) Apache Maven 3.9.11 or higher

> Make and/or Maven are only needed if building from source. The prebuilt jar only requires Java.

## How to run the Application

**Using make:**

```make
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
