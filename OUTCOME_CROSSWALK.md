# CS 499 Program Outcome Crosswalk

This crosswalk connects the five Computer Science program outcomes to implemented behavior, design reasoning, and reviewable evidence. The complete reader-facing synthesis is available at [outcomes.html](outcomes.html).

| Program outcome | Concrete evidence | Primary locations |
| --- | --- | --- |
| **1. Collaborative environments and decision support** | CS 250 Agile context, user stories, acceptance criteria, transparent scope refinement, unchanged baselines, conventional project structure, repository contracts, decision logs, read-first guides, enhancement maps, and reproducible checks create a shared basis for review. Individual completion is stated honestly rather than described as a production team project. | [Self-assessment](self-assessment.html); Week 1 plan/refinement; Week 3 and Week 5 decision/evidence maps |
| **2. Professional oral, written, and visual communication** | The website presents substantive before/after evidence without requiring downloads. Narratives explain limitation, exact change, rationale, trade-off, verification, feedback response, and remaining limitation. CS 465 and DAT 205 examples show audience and stakeholder adaptation outside the portfolio artifacts. The code-review script provides a complete 35-minute oral plan; the recording remains pending. | [Self-assessment](self-assessment.html); [code review](code-review.html); three category pages and DOCX narratives; Week 6 journal |
| **3. Algorithms and data structures** | The CS 320 suite preserves expected `O(1)` exact-ID access, adds three multi-record retrieval algorithms, establishes complete comparator chains, protects results with model snapshots and unmodifiable lists, and evaluates time, space, best/all-match cases, and indexing alternatives. The database index and query-plan result provide supporting evidence. | [Algorithms page](algorithms.html); Week 4 complexity document and 19-check verifier; Week 5 query/index analysis |
| **4. Well-founded techniques, skills, and tools** | Java 17, Maven organization, shared validation, explicit exceptions, defensive copying, atomic updates, `Optional`, `Clock`, Android structure, SQLite, repositories, constraints, migration, PBKDF2, query planning, verification scripts, diffs, and GitHub Pages deliver defined application and reviewer value. | [Software design](software-design.html); [databases](databases.html); 26 software checks, 46 database checks, and 36 Java checks |
| **5. Security mindset** | Service boundaries reject invalid input, prevent alias-based mutation, and preserve state after failures. The database replaces plaintext credentials, scopes CRUD by authenticated owner, parameterizes values, bounds verifier metadata, randomizes blank legacy credentials, enforces foreign keys and constraints, audits migration ambiguity, disables backup, and removes unused SMS permission. Residual risks are documented. | Week 3 validation/copy/update evidence; Week 5 security analysis, source, manifest, migration, and verifiers; Week 6 security/acceptance roadmap |

## Verification Summary

| Enhancement | Executed evidence | Separately scoped assets |
| --- | --- | --- |
| Software Design and Engineering | Java 17 lint compilation; 26/26 focused behavior checks | 97 JUnit methods and Maven execution path |
| Algorithms and Data Structures | Java 17 lint compilation; 19/19 focused algorithm checks | 103 cumulative JUnit methods and Maven execution path |
| Databases | 46/46 SQLite checks; Java 17 lint compilation; 36/36 security/validation checks | Complete Gradle project and Android instrumentation tests |

The focused verifiers total 127 passing checks. Compilation, standalone checks, dependency-based tests, and a full Android build prove different things and are reported separately.
