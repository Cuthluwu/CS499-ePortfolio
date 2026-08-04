# Madison Parker — CS 499 Computer Science Capstone

This repository presents the planning record and three completed technical enhancements for my Southern New Hampshire University Computer Science capstone. The portfolio is organized to make every claim reviewable: each milestone separates the original artifact from the enhanced implementation, names the exact design decisions, includes supporting documentation, and records what was actually verified.

**Portfolio site:** [Open the ePortfolio landing page](https://cuthluwu.github.io/CS499-ePortfolio/)  
**Outcome crosswalk:** [Review the five program outcomes](OUTCOME_CROSSWALK.md)
**Portfolio timeline:** [Review the dated artifact progression](PORTFOLIO_TIMELINE.md)

## Portfolio at a Glance

| Course week | Portfolio date | Category and deliverable | Primary evidence |
| --- | --- | --- | --- |
| [Week 1](Week-1_Module-One-Planning/) | July 4, 2026 | Module One artifact selection and enhancement plan | Three-artifact plan, intended outcomes, and documented scope refinement |
| [Week 3](Week-3_Software-Design-and-Engineering/) | July 13, 2026 | Milestone Two: Software Design and Engineering | CS 320 service-suite architecture, validation, defensive copying, atomic updates, and 26 executed checks |
| [Week 4](Week-4_Algorithms-and-Data-Structures/) | July 20, 2026 | Milestone Three: Algorithms and Data Structures | Search, filtering, deterministic sorting, complexity analysis, and 19 executed checks |
| [Week 5](Week-5_Databases/) | August 1, 2026 | Milestone Four: Databases | CS 360 SQLite redesign, ownership-aware CRUD, migration, password verification, and 76 executed database/Java checks |
| [Week 6](Week-6_Algorithms-Publication-and-Technology/) | August 3, 2026 | Algorithms publication and disruptive-technology journal | Published algorithm evidence plus a researched, accessible Bluetooth smart-scale and behavior-feedback roadmap |

The dates above identify when each capstone-stage document belongs in the course progression. They are intentionally different from the May and June 2026 creation dates of the original CS 320 and CS 360 coursework. File packaging or GitHub publication may occur later without changing the historical artifact dates.

The current publication intentionally contains Weeks 1, 3, 4, 5, and 6. Week 2's recorded code review will be linked after the final video is encoded, downloaded, and independently checked for playback and readability. This release ends with the completed Module Six work.

## Recommended Review Path

1. Read the [Module One plan](Week-1_Module-One-Planning/) and the accompanying [scope refinement](Week-1_Module-One-Planning/PLAN_REFINEMENT.md).
2. Review the [Week 3 software-design narrative](Week-3_Software-Design-and-Engineering/Madison_Parker_CS499_Milestone2_Software_Design_Narrative.docx), then compare its original and enhanced source folders.
3. Continue to [Week 4](Week-4_Algorithms-and-Data-Structures/) to isolate the cumulative algorithm delta and its documented performance trade-offs.
4. Review [Week 5](Week-5_Databases/) as an independent Android/SQLite enhancement, beginning with its narrative and `Artifact_Contents/00_READ_ME_FIRST.txt`.
5. Use [OUTCOME_CROSSWALK.md](OUTCOME_CROSSWALK.md) to trace each program outcome to concrete implementation and evidence locations.
6. Review [Week 6](Week-6_Algorithms-Publication-and-Technology/) for the published algorithms work and the disruptive-technology journal grounded in the Weight Tracker.
7. Use [PORTFOLIO_TIMELINE.md](PORTFOLIO_TIMELINE.md) to distinguish original artifact creation, enhancement dates, and final publication activity.

## Artifact Continuity

Module One initially identified the CS 250 SNHU Travel planning materials for the software-design category. Those materials remain useful Agile and stakeholder-planning context, but the code review established that they did not contain enough source code for the required code-level analysis and enhancement. The CS 320 Contact, Task, and Appointment Service Suite therefore became the concrete Category One artifact. The same suite then supports Category Two through a separate cumulative enhancement with clearly isolated search, filtering, sorting, and complexity work. The CS 360 Android Weight Tracker remains the independent database artifact.

This refinement is stated openly so the portfolio does not silently substitute one artifact for another. It preserves the historical plan while keeping the final technical claims tied to inspectable code.

## Evidence Standard

Each enhancement week includes:

- an unchanged original baseline;
- a separate enhanced implementation;
- a Microsoft Word narrative;
- a downloadable technical artifact ZIP;
- a read-first guide and exact enhancement map;
- repeatable verification scripts and recorded results;
- integrity records for the original artifact; and
- explicit limitations that distinguish executed evidence from included test assets.

The Java service projects include 97 and 103 JUnit methods as reviewable test assets. Because those dependency-based suites were not executed in the publication environment, the portfolio does not report them as passing runs. Instead, the included dependency-free verifiers were compiled with Java 17 and executed: 26 checks passed for software design and 19 for algorithms. The database milestone separately passed 46 SQLite checks and 30 Java security/validation checks. The complete Android Studio project and instrumentation tests remain included, but an Android APK build is not claimed from the verification environment.

## Repository Structure

```text
CS499-ePortfolio/
├── index.html
├── assets/styles.css
├── OUTCOME_CROSSWALK.md
├── Week-1_Module-One-Planning/
├── Week-3_Software-Design-and-Engineering/
├── Week-4_Algorithms-and-Data-Structures/
├── Week-5_Databases/
├── Week-6_Algorithms-Publication-and-Technology/
└── PORTFOLIO_TIMELINE.md
```

Within each enhancement week, `Artifact_Contents` is extracted for browser-based review. Original and enhanced folders remain separate, and the submission ZIP is retained as a single-file download.

## Author

Madison Parker  
Bachelor of Science in Computer Science  
Southern New Hampshire University
