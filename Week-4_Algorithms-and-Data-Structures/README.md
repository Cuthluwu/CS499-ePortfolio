# Week 4 — Algorithms and Data Structures

This milestone starts from the CS 320 service project I improved in Week 3 and adds ways to find records when the exact ID is not known.

## Files

- [Algorithms narrative](Madison_Parker_CS499_Milestone3_Algorithms_Narrative.docx)
- [`Artifact_Contents/01_ORIGINAL_CS320/`](Artifact_Contents/01_ORIGINAL_CS320/) — original CS 320 project
- [`Artifact_Contents/02_WEEK3_DESIGN_BASELINE/`](Artifact_Contents/02_WEEK3_DESIGN_BASELINE/) — Week 3 version before the algorithm changes
- [`Artifact_Contents/03_WEEK4_ALGORITHMS_ENHANCED/`](Artifact_Contents/03_WEEK4_ALGORITHMS_ENHANCED/) — Week 4 version
- [Downloadable artifact ZIP](Madison_Parker_CS499_Milestone3_Algorithms_Artifact.zip)

## What I added

- `ContactService.searchByLastName` searches contact last names without requiring an exact ID.
- `TaskService.searchByKeyword` checks task names and descriptions for a keyword.
- `AppointmentService.findByDateRange` returns appointments inside an inclusive date range.
- The results are sorted so the same data is returned in a predictable order.

I kept the existing `HashMap` because it still makes sense for exact-ID lookups. The new search methods have a different job: they scan the stored records, keep the matches, and sort those matches before returning them.

If there are `n` stored records and `m` matches, the search-and-sort work is summarized as **O(n + m log m)** time with **O(m)** extra result space. For this small in-memory project, I did not add extra indexes because that would add more code to maintain without solving a problem the application currently has.

## Testing

The tests cover cases such as mixed capitalization, partial matches, no matches, invalid search input, date-range boundaries, and result ordering. The test source and saved verification results are kept inside the artifact folder.