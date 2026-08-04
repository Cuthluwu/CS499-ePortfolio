# Test and Verification Plan

## JUnit suite

The cumulative enhanced Maven project contains 103 JUnit test methods across 12 test classes. The suite retains the original requirement checks, covers the software-design changes, and adds algorithm-specific cases.

Algorithm cases include:

- mixed-case substring and keyword input;
- matching through both a task name and description;
- empty and no-match results;
- null and blank search rejection;
- stable, deterministic comparator tie-breakers;
- inclusive appointment date boundaries;
- reversed and null date ranges;
- immutable result collections; and
- copied records that cannot mutate stored state.

Run from `enhanced/service-suite`:

```text
mvn clean test
```

## Standalone verification

`evidence/AlgorithmsVerification.java` compiles with the production source and runs 19 dependency-free checks. The recorded output confirms match behavior, deterministic order, edge cases, inclusive ranges, immutable lists, and record snapshots.

## Verification limits

The production source and standalone verifier were compiled with Java 17 and `-Xlint:all`, then all 19 checks were executed. The JUnit sources were syntax-parsed and compiler-checked against minimal API stubs in the review environment. A normal Maven run remains the repeatable method for executing all 103 JUnit tests with the real JUnit dependency.
