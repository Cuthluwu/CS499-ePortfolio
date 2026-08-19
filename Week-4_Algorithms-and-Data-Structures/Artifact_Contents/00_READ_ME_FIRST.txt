MADISON PARKER - CS 499 MILESTONE THREE
Algorithms and Data Structures

This folder shows the CS 320 project at three points so the Week 4 algorithm changes are easy to compare.

01_ORIGINAL_CS320
    My original CS 320 Contact, Task, and Appointment project.

02_WEEK3_DESIGN_BASELINE
    The version after the Week 3 software-design changes and before the Week 4 search work.

03_WEEK4_ALGORITHMS_ENHANCED
    The Week 4 version. It adds contact last-name search, task keyword search, appointment date-range filtering, sorted results, and tests for those behaviors.

04_DOCUMENTATION
    Complexity notes, change summaries, references, and supporting course documentation.

05_EVIDENCE
    Saved verification output, scripts, and a Week 3-to-Week 4 diff used during final review.

The existing HashMap is still used for exact-ID operations. The new search methods scan the stored records and sort the matches, which is discussed in the milestone narrative and complexity notes.

To run the Maven tests, open 03_WEEK4_ALGORITHMS_ENHANCED and run:
    mvn clean test
