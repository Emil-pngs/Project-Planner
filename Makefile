FAT_JAR := target/ProjectPlanner.jar
THIN_JAR := target/ProjectPlanner-thin.jar
THEME_SCSS := src/main/resources/dtu/projectplanner/ui/theme.scss
THEME_CSS := src/main/resources/dtu/projectplanner/ui/theme.css

.PHONY: styles build test run clean release

styles:
	@if command -v sass >/dev/null 2>&1; then \
		sass "$(THEME_SCSS)" "$(THEME_CSS)" --no-source-map; \
	elif command -v npx >/dev/null 2>&1; then \
		npx --yes sass "$(THEME_SCSS)" "$(THEME_CSS)" --no-source-map; \
	else \
		echo "Error: Sass compiler not found. Install sass or npm/npx."; \
		exit 1; \
	fi
	@echo "Compiled $(THEME_SCSS) -> $(THEME_CSS)"

build:
	mvn package -q
	@echo "Built $(FAT_JAR) (fat jar)"
	@echo "Built $(THIN_JAR) (thin jar)"
	@timeout 3 java -jar $(FAT_JAR) || true
	@echo "Built successfully."

test:
	mvn test

run:
	mvn javafx:run -q

clean:
	mvn clean -q

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
	if command -v sass >/dev/null 2>&1; then \
		sass "$(THEME_SCSS)" "$(THEME_CSS)" --no-source-map; \
	elif command -v npx >/dev/null 2>&1; then \
		npx --yes sass "$(THEME_SCSS)" "$(THEME_CSS)" --no-source-map; \
	else \
		echo "Error: Sass compiler not found. Install sass or npm/npx."; \
		exit 1; \
	fi; \
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
