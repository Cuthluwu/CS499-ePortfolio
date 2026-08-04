# Week 3 — Software Design and Engineering

Milestone Two converts the original CS 320 Contact, Task, and Appointment services into one organized, testable Java 17 service suite. The enhancement focuses on architecture and behavior at service boundaries rather than adding unrelated frameworks or presenting folder cleanup as the main improvement.

## Start Here

1. [Enhancement narrative](Madison_Parker_CS499_Milestone2_Software_Design_Narrative.docx)
2. [`files/00_READ_ME_FIRST.txt`](files/00_READ_ME_FIRST.txt)
3. [`files/docs/ENHANCEMENT_MAP.md`](files/docs/ENHANCEMENT_MAP.md)
4. [`files/evidence/SOFTWARE_DESIGN_VERIFICATION_RESULTS.txt`](files/evidence/SOFTWARE_DESIGN_VERIFICATION_RESULTS.txt)
5. [Downloadable technical artifact](Madison_Parker_CS499_Milestone2_Software_Design_Artifact.zip)

## Before and After

| Design area | Original condition | Completed enhancement |
| --- | --- | --- |
| Structure | Three default-package folders with repeated logic | Maven layout with model, service, repository, validation, and exception packages |
| Validation | Repeated field checks and inconsistent normalization | Shared `Validation` rules plus model-owned named limits |
| Missing and duplicate records | `null` or general `IllegalArgumentException` depending on method | `Optional` for lookups and specific command exceptions |
| State ownership | Mutable caller objects stored and returned directly | Copies on input and snapshot copies on output |
| Multi-field updates | A later validation failure could leave a partial change | Copy-validate-replace commits only after the full update succeeds |
| Appointment time | Validation depended on moving system time | `Clock` injection supports deterministic boundary tests |

## Exact Implementation Focus

- `Validation.requiredText`, `Validation.phone`, and `Validation.presentOrFutureDate` centralize service rules.
- `RecordRepository<T>` documents the common add, lookup, delete, and size contract without forcing unrelated update behavior into inheritance.
- `ContactService.updateName`, `TaskService.update`, and `AppointmentService.update` implement the atomic copy-validate-replace sequence.
- Copy constructors and defensive `Date` handling prevent callers from mutating stored service state through retained references.
- `DuplicateRecordException` and `RecordNotFoundException` make failure contracts explicit.

## Verification Boundary

The enhanced Maven project contains 97 JUnit methods as reviewable test assets. Those dependency-based tests are not mislabeled as an executed run in this environment. The included Java 17 verifier was compiled with `-Xlint:all` and executed against the production source; all **26 of 26** behavior checks passed, including defensive-copy isolation and unchanged stored state after rejected multi-field updates.

Run the recorded verifier with:

```sh
sh files/evidence/run_software_design_verification.sh
```

The narrative also states the artifact's limits: it is an in-memory, single-process teaching component and does not claim persistence, concurrency control, authentication, or production authorization.
