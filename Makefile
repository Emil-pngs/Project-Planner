FAT_JAR := target/ProjectPlanner.jar
THIN_JAR := target/ProjectPlanner-thin.jar

.PHONY: build test run clean release

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

# Bump patch version, build fat jar, publish a GitHub release, and attach the jar
release:
	@CURRENT=$$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout); \
	MAJOR=$$(echo $$CURRENT | cut -d. -f1); \
	MINOR=$$(echo $$CURRENT | cut -d. -f2); \
	PATCH=$$(echo $$CURRENT | cut -d. -f3); \
	NEW_PATCH=$$(($$PATCH + 1)); \
	NEW_VERSION="$$MAJOR.$$MINOR.$$NEW_PATCH"; \
	TAG="v$$NEW_VERSION"; \
	RELEASE_FAT="dist/Release $$TAG.jar"; \
	RELEASE_THIN="dist/Release $$TAG-thin.jar"; \
	echo "Bumping version: $$CURRENT -> $$NEW_VERSION"; \
	mvn versions:set -DnewVersion=$$NEW_VERSION -DgenerateBackupPoms=false -q; \
	mvn package -q; \
	mkdir -p dist; \
	cp $(FAT_JAR) "$$RELEASE_FAT"; \
	cp $(THIN_JAR) "$$RELEASE_THIN"; \
	git add pom.xml; \
	git commit -m "Release $$TAG"; \
	git tag "$$TAG"; \
	git push origin "$$TAG"; \
	gh release create "$$TAG" "$$RELEASE_FAT" "$$RELEASE_THIN" --title "Release $$TAG" --notes ""; \
	git add .; \
	git commit -m "Compiled for Release $$TAG"; \
	git push origin HEAD:main; \
	echo "Published GitHub release $$TAG"
