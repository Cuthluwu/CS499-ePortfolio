# What Changed: Original Artifact, Week 3 Baseline, and Week 4 Enhancement

## Original CS 320 artifact (May 2026)

The original Contact, Task, and Appointment services stored records in separate
`HashMap` collections and supported exact-ID create, read, update, and delete
operations. That version demonstrated the course requirements, but it used the
default package, repeated validation and service patterns, exposed limited
state-protection guarantees, and offered no way to retrieve records by user-
meaningful criteria such as a partial last name, a keyword, or a date range.

## Week 3 software-design baseline

Milestone Two reorganized the same requirements into a Maven project with
`model`, `service`, `repository`, `validation`, and `exception` packages. It
centralized common validation, added specific duplicate/not-found exceptions,
used `Optional` for lookup contracts, protected stored state with defensive
copies, returned controlled snapshots, and changed updates to a copy-validate-
replace sequence so a failed validation could not leave a partial update. That
work created a stable design baseline; it is included separately in
`02_WEEK3_DESIGN_BASELINE` rather than being mislabeled as new Week 4 work.

## Week 4 algorithms and data-structures enhancement

Milestone Three keeps the Week 3 architecture and adds the category-specific
algorithm delta in four production locations:

- `Validation.searchTerm` validates and normalizes retrieval criteria.
- `ContactService.searchByLastName` performs case-insensitive substring search
  and orders results by last name, first name, and contact ID.
- `TaskService.searchByKeyword` searches both task name and description and
  orders matches by task name and task ID.
- `AppointmentService.findByDateRange` filters an inclusive date interval and
  orders results by appointment date and appointment ID.

The services preserve `HashMap` for expected constant-time exact-ID access.
Broader retrieval scans `n` stored records, copies the `m` matches, and sorts
those matches, producing O(n + m log m) time and O(m) additional result space.
Comparator tie-breakers are required because `HashMap` does not guarantee an
iteration order. Each method returns copied model objects inside an unmodifiable
list so the caller cannot mutate the internal collection through a search
result.

The final performance pass keeps the same complexity bounds while reducing
temporary allocation. Comparator chains are reusable class constants, the
services return Java 17's unmodifiable `Stream.toList()` result directly, and
appointment retrieval compares primitive epoch-millisecond values rather than
allocating repeated defensive `Date` copies during filtering and sorting.

Six algorithm-focused JUnit methods were added, increasing the included suite
from 97 methods in Week 3 to 103 in Week 4. The new tests cover mixed-case and
partial matching, matches across alternate fields, empty and invalid criteria,
comparator tie-breakers, inclusive boundaries, reversed ranges, immutable
collections, and snapshot independence. A dependency-free Java 17 verifier also
completed 19 algorithm checks with no reported compiler warnings.

## References

JUnit Team. (2024). *JUnit 5.10.2 user guide*.
https://docs.junit.org/5.10.2/user-guide/

Oracle. (n.d.-a). *Comparator (Java SE 17 & JDK 17)*.
https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Comparator.html

Oracle. (n.d.-b). *HashMap (Java SE 17 & JDK 17)*.
https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/HashMap.html

Oracle. (n.d.-c). *Stream.toList (Java SE 17 & JDK 17)*.
https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/Stream.html#toList()
