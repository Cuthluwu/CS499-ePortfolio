# Week 4 — Algorithms and Data Structures

Milestone Three builds cumulatively on the Week 3 service design while isolating the Category Two delta. The existing `HashMap` remains appropriate for exact-ID commands; three new retrieval algorithms traverse values, filter by category-specific criteria, copy matches, establish deterministic order, and return unmodifiable results.

## Start Here

1. [Enhancement narrative](Madison_Parker_CS499_Milestone3_Algorithms_Narrative.docx)
2. [`files/00_READ_ME_FIRST.txt`](files/00_READ_ME_FIRST.txt)
3. [`files/docs/ALGORITHM_COMPLEXITY_AND_TRADEOFFS.md`](files/docs/ALGORITHM_COMPLEXITY_AND_TRADEOFFS.md)
4. [`files/evidence/ALGORITHMS_VERIFICATION_RESULTS.txt`](files/evidence/ALGORITHMS_VERIFICATION_RESULTS.txt)
5. [Downloadable technical artifact](Madison_Parker_CS499_Milestone3_Algorithms_Artifact.zip)

## Algorithm Delta

| Source method | Retrieval behavior | Deterministic order |
| --- | --- | --- |
| `ContactService.searchByLastName` | Case-insensitive substring search over last names | Last name, first name, contact ID |
| `TaskService.searchByKeyword` | Case-insensitive search across task name or description | Task name, task ID |
| `AppointmentService.findByDateRange` | Inclusive filtering across copied start and end dates | Appointment date, appointment ID |

`Validation.searchTerm` rejects null and blank criteria and normalizes user input. Each algorithm creates record snapshots before sorting, then returns `List.copyOf` so the caller cannot structurally modify the result or mutate the service's stored record through a returned element.

## Complexity and Design Trade-Off

For a map with capacity *c*, *n* stored records, and *m* matches, each retrieval performs a value traversal, copies the matches, and sorts only those matches. The precise bound is **O(c + n + m log m)** time and **O(m)** additional result space. With normal capacity proportional to record count, this is conventionally summarized as **O(n + m log m)**.

The enhancement deliberately avoids secondary indexes at the current scale. A last-name structure would not make arbitrary substring matching constant time; a task-term index would require tokenization and synchronization rules; a date index would add maintenance for duplicate timestamps and updates. The documentation identifies those options as future work when measured collection size and access frequency justify their additional invariants.

## Verification Boundary

The cumulative source contains 103 JUnit methods, including six retrieval-focused additions. The dependency-free Java 17 verifier was compiled with `-Xlint:all` and executed; all **19 of 19** checks passed across mixed case, partial matches, tie-break ordering, inclusive boundaries, null/reversed input, empty results, immutable lists, and record snapshots.

Run the recorded verifier with:

```sh
sh files/evidence/run_algorithms_verification.sh
```
