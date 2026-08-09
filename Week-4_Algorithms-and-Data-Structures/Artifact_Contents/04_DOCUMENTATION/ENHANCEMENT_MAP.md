# Enhancement Map

## Category focus

The same CS 320 artifact supports Categories One and Two for different reasons. Category One improved architecture and service boundaries. Category Two adds operations that require explicit data-structure selection, linear searching, comparison-based sorting, immutable result construction, and complexity analysis.

## Before and after

| Area | Original condition | Completed algorithm enhancement | Primary evidence |
|---|---|---|---|
| Exact-ID access | `HashMap` CRUD by unique ID | Preserved average O(1) exact-ID operations | all service classes |
| Contact retrieval | Exact ID only | Case-insensitive last-name substring search | `ContactService.searchByLastName` |
| Contact ordering | `HashMap` iteration had no user-facing order | Last name, first name, then ID | `ContactService` comparator and tests |
| Task retrieval | Exact ID only | Case-insensitive keyword search across name and description | `TaskService.searchByKeyword` |
| Task ordering | No collection result | Task name, then ID | `TaskService` comparator and tests |
| Appointment retrieval | Exact ID only | Inclusive start/end date filtering | `AppointmentService.findByDateRange` |
| Appointment ordering | No collection result | Appointment date, then ID | `AppointmentService` comparator and tests |
| Result protection | No result collections | Unmodifiable lists containing record snapshots | `Stream.toList`, copy constructors, tests |
| Allocation control | Comparator chains and mutable `Date` copies could be created repeatedly during retrieval | Reusable comparator constants, direct unmodifiable stream results, and primitive timestamp comparison | service constants, `Appointment.getAppointmentTimeMillis`, tests |
| Edge cases | No search tests | Empty results, mixed case, blank/null input, reversed ranges, inclusive boundaries, ties, and attempted result mutation | 103 JUnit methods and 19 standalone checks |

## Highest-value review locations

- `src/main/java/edu/snhu/cs499/service/ContactService.java`
- `src/main/java/edu/snhu/cs499/service/TaskService.java`
- `src/main/java/edu/snhu/cs499/service/AppointmentService.java`
- `src/test/java/edu/snhu/cs499/service/ContactServiceTest.java`
- `src/test/java/edu/snhu/cs499/service/TaskServiceTest.java`
- `src/test/java/edu/snhu/cs499/service/AppointmentServiceTest.java`
- `04_EVIDENCE/ALGORITHMS_VERIFICATION_RESULTS.txt`
