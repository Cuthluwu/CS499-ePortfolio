# Capstone Progress and Decision Log

## Planned enhancement

Module One proposed preserving `HashMap` storage, adding broader search and filtering, returning sorted results, testing edge cases, and documenting complexity. The code-review script repeated that plan and identified specific operations for contacts, tasks, and appointments.

## Completed enhancement

The cumulative Category Two snapshot retains the Category One package and service improvements. It adds last-name search, task keyword search, and appointment date-range filtering. Each method returns a deterministic immutable snapshot and has tests for valid behavior, no matches, invalid criteria, ordering ties, and mutation attempts.

## Scope decision

No secondary search index was added. For the small in-memory artifact, maintaining three additional index structures would introduce synchronization risk and hide the simpler trade-off the milestone is meant to explain. The documentation states where an indexed design would become appropriate.

## Outcome plan update

The enhancement meets the planned primary coverage for Outcomes Three and Four. Outcome Five is supported through defensive result handling and validation, but the narrative does not treat those measures as a substitute for system-level security.
