# CS 320 Service Suite

This enhanced version reorganizes Madison Parker's original CS 320 contact, task, and appointment services into one Maven project. The original business rules remain intact while the code now uses packages, shared validation, a repository contract, specific service exceptions, consistent lookup behavior, defensive copies, and expanded JUnit tests.

## Requirements

- Java 17 or newer
- Maven 3.9 or newer

## Run the tests

```text
mvn clean test
```

The `src/main/java` directory contains production code. The `src/test/java` directory contains model and service tests. No database, network connection, or external account is required.
