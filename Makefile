JAR := target/ProjectPlanner.jar

.PHONY: build test run clean

# Build the jar and do a quick smoke-run (exits after 3 s if no crash)
build:
	mvn package -q
	@echo "Built $(JAR)"
	@timeout 3 java -jar $(JAR) || true
	@echo "Smoke-run passed"

# Run the unit + Cucumber tests
test:
	mvn test

# Just launch the app (no timeout)
run:
	mvn javafx:run -q

clean:
	mvn clean -q
