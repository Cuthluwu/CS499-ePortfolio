# Rubric and Course-Outcome Crosswalk

| Indicator | Evidence |
|---|---|
| Uses well-founded skills and techniques to implement a design solution | Maven layout, explicit packages, shared validation, repository contract, specific exceptions, defensive copies, and deterministic time handling |
| Programs a solution to software logic problems | Consistent lookup behavior, duplicate protection, validated CRUD operations, and atomic multi-field updates |
| Addresses potential design flaws related to security | Blank input rejection, controlled service mutation, immutable identifiers, defensive `Date` copies, no credentials or external secrets, and tests for invalid state changes |
| Clearly articulates ideas and accomplishments | Code-review script, narrative, enhancement map, design rationale, repeatable verification, and direct before-and-after evidence |

## Outcome coverage

- Outcome 1: CS 250 remains the Agile and stakeholder context, while the enhancement documentation makes requirements and decisions reviewable by another developer.
- Outcome 2: The code review, narrative, package guide, and evidence map present the work for both academic and technical audiences.
- Outcome 4: The primary coverage comes from the Java 17 design, Maven project structure, repository contract, validation, testing, and maintainable service boundaries.
- Outcome 5: The enhancement applies defensive programming and prevents invalid or unauthorized state changes within the service boundary. It does not claim to implement application authentication or network security.
