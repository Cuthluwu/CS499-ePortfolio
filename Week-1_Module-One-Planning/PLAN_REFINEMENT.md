# Module One Plan Refinement

## Why the Category One artifact changed

The Module One plan selected the CS 250 SNHU Travel sprint-review and planning materials for Software Design and Engineering. That was a reasonable starting point because the artifact documented user stories, acceptance criteria, stakeholder communication, and iterative planning. During the code review, however, the category requirements also demanded a source-level walkthrough and a completed technical enhancement. The planning artifact did not contain enough implementation code to support that analysis without creating a new application and presenting it as an enhancement of existing source.

The CS 320 Contact, Task, and Appointment Service Suite therefore became the concrete Category One code artifact. This was a refinement of scope, not a silent replacement:

- **CS 250 remains the planning context.** Its Agile artifacts support discussion of audience, stakeholder value, requirements, and collaboration.
- **CS 320 supplies the Category One implementation evidence.** The original services expose inspectable decisions about validation, collection ownership, failure behavior, mutation, update atomicity, time dependencies, and testing.
- **CS 320 also supports Category Two through a separate cumulative delta.** The algorithms milestone adds search, filtering, deterministic sorting, immutable results, and complexity analysis after the design foundation is established.
- **CS 360 remains Category Three.** The Android Weight Tracker provides an independent SQLite artifact for schema, migration, security, CRUD, query, and evidence work.

## Traceability across the portfolio

| Initial plan | Final implementation decision | Where the decision is documented |
| --- | --- | --- |
| Use CS 250 planning materials for Category One | Preserve CS 250 as planning context; use CS 320 as the source-code enhancement | Week 3 narrative, “Refinement of the Module One Plan” |
| Add service-layer search, filtering, sorting, and analysis to CS 320 | Completed as a cumulative Category Two enhancement after the design refactor | Week 4 narrative and algorithm complexity documentation |
| Redesign the CS 360 persistence layer | Completed with a version-two schema, ownership-scoped repositories, migration, credential verification, reporting, and test evidence | Week 5 narrative, database documentation, and instructor evidence |

The original Module One document is preserved as the historical planning record. This refinement file provides the missing decision trail so an instructor or employer can follow how review findings changed the implementation plan.
