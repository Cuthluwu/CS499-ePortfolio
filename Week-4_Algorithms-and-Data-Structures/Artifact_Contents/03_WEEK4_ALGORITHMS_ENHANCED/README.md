# CS 320 Service Suite

This cumulative enhanced version reorganizes Madison Parker's original CS 320 contact, task, and appointment services into one Maven project and adds category-specific search, filtering, and deterministic sorting algorithms. The original business rules remain intact while the code now uses packages, shared validation, a repository contract, specific service exceptions, consistent lookup behavior, defensive copies, and expanded JUnit tests.

## Algorithm enhancements

- Contact search performs a case-insensitive last-name scan and sorts matches by last name, first name, and ID.
- Task search scans both the name and description and sorts matches by name and ID.
- Appointment filtering uses an inclusive date range and sorts matches by date and ID.
- Each result is an immutable snapshot, so callers cannot modify the service's collection through the returned list.
- Exact-ID operations retain average O(1) `HashMap` behavior. Searches are O(n), and sorting `m` matches is O(m log m).

## Requirements

- Java 17 or newer
- Maven 3.9 or newer

## Run the tests

```text
mvn clean test
```

The `src/main/java` directory contains production code. The `src/test/java` directory contains model and service tests. No database, network connection, or external account is required.
