MADISON PARKER - CS 499 MILESTONE THREE
Enhancement Two: Algorithms and Data Structures

This ZIP is the Week 4 technical-artifact submission. It is intentionally
self-contained and opens directly to the evidence needed for review.

01_ORIGINAL_CS320
    Exact original CS 320 Contact, Task, and Appointment source/test files from
    May 2026. This version uses separate default-package services and supports
    exact-ID CRUD but not multi-record search, filtering, or deterministic sort.

02_WEEK3_DESIGN_BASELINE
    The completed Milestone Two software-design version immediately before the
    Week 4 algorithm enhancement. It preserves the original requirements while
    adding Maven organization, packages, shared validation, repository and
    exception boundaries, defensive copies, atomic updates, Optional lookup,
    and expanded tests.

03_WEEK4_ALGORITHMS_ENHANCED
    The cumulative Milestone Three Maven project. It retains the Week 3 design
    foundation and adds contact last-name search, task keyword search, inclusive
    appointment date-range filtering, deterministic comparator chains, immutable
    snapshot results, and algorithm-focused tests.

04_DOCUMENTATION
    Enhancement map, complexity/trade-off analysis, verification plan, rubric
    crosswalk, decision log, references, and a direct three-stage comparison.

05_EVIDENCE
    Standalone Java verifier, recorded 19-check result, original integrity
    hashes, the Week 3-to-Week 4 unified diff, and a repeatable verification
    script.

RECOMMENDED REVIEW ORDER
1. 04_DOCUMENTATION/WHAT_CHANGED_ORIGINAL_WEEK3_WEEK4.md
2. 04_DOCUMENTATION/ENHANCEMENT_MAP.md
3. 04_DOCUMENTATION/ALGORITHM_COMPLEXITY_AND_TRADEOFFS.md
4. 05_EVIDENCE/ALGORITHMS_VERIFICATION_RESULTS.txt
5. 05_EVIDENCE/WEEK3_TO_WEEK4.diff

RUN THE STANDALONE VERIFIER
From the ZIP root on macOS/Linux/Git Bash:
    sh 05_EVIDENCE/run_algorithms_verification.sh

RUN THE JUNIT PROJECT
From 03_WEEK4_ALGORITHMS_ENHANCED:
    mvn clean test

Evidence boundary: the included dependency-free verifier was compiled with
Java 17 and executed for 19 passing checks. The 103 JUnit methods are included
as reviewable test assets and can be executed with Maven in a normal Java/Maven
environment.
