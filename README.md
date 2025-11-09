# Parabank Registration - Selenium + Cucumber (Maven)

This project contains a sample Selenium + Cucumber test suite for the Parabank registration flow.

Structure
- pom.xml - maven project file with dependencies
- src/main/java - page objects
- src/test/java - step definitions, runner and utilities
- src/test/resources/features - cucumber feature files

How to run
1. Ensure Java 11+ and Maven are installed.
2. From project root run:

```powershell
mvn test
```

ChromeDriver will be managed automatically by WebDriverManager.
