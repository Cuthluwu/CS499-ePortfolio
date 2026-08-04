# Enhancement Map

## Artifact decision

Module One selected the CS 250 SNHU Travel planning materials for Category One. During the code review, the planning materials proved useful for stakeholder context but did not contain enough source code for a meaningful code walkthrough. The Category One enhancement therefore uses the CS 320 contact, task, and appointment service suite as the multifaceted code artifact. CS 250 remains evidence of Agile planning, requirements, and audience awareness. This refinement is stated in the code-review script instead of being hidden.

## Before and after

| Area | Original condition | Completed enhancement | Primary evidence |
|---|---|---|---|
| Project structure | Three separate folders with classes in the default package | One Maven project with model, service, validation, repository, and exception packages | `pom.xml`, `src/main/java` |
| Validation | Repeated numeric limits and null checks in each model | Named constants and one shared validation utility that also rejects blank text | `Validation.java`, model classes |
| Service contract | Similar services used separate method patterns and returned `null` for missing lookups | Shared `RecordRepository<T>` contract and `Optional<T>` lookup policy | `RecordRepository.java`, service classes |
| Failure behavior | General `IllegalArgumentException` messages | Distinct duplicate-record and record-not-found exceptions | `exception/` |
| State ownership | Services returned their stored mutable objects | Services store copies and return snapshots | copy constructors and service tests |
| Multi-field updates | A later validation failure could leave an earlier field changed | Updates occur on a copy and replace stored state only after all validation passes | `update` methods and atomicity tests |
| Appointment boundary | Validation depended directly on the moving system time | `Clock` can be injected for deterministic date-boundary tests | `Appointment.java`, `AppointmentTest.java` |
| Regression evidence | Original JUnit files were separated from a build definition | Original requirement tests were ported and retained; new design tests were added | 97 test methods in `src/test/java` |

## Highest-value review locations

- `src/main/java/edu/snhu/cs499/validation/Validation.java`
- `src/main/java/edu/snhu/cs499/repository/RecordRepository.java`
- `src/main/java/edu/snhu/cs499/service/ContactService.java`
- `src/main/java/edu/snhu/cs499/model/Appointment.java`
- `src/test/java/edu/snhu/cs499/service/ContactServiceTest.java`
- `evidence/SOFTWARE_DESIGN_VERIFICATION_RESULTS.txt`
