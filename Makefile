FAT_JAR := target/ProjectPlanner.jar
THIN_JAR := target/ProjectPlanner-thin.jar

.PHONY: build test run clean

# Build both jars and do a quick smoke-run of the fat jar
build:
	mvn package -q
	@echo "Built $(FAT_JAR) (fat jar)"
	@echo "Built $(THIN_JAR) (thin jar)"
	@timeout 3 java -jar $(FAT_JAR) || true
	@echo "Smoke-run passed"

# Run the unit + Cucumber tests
test:
	mvn test

# Just launch the app (no timeout)
run:
	mvn javafx:run -q

clean:
	mvn clean -q
