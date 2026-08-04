# Test and Verification Plan

## JUnit suite

The enhanced Maven project contains 97 JUnit test methods across 12 test classes. Six classes preserve the original model and service requirements. Six additional classes test the design enhancement, including blank-input rejection, normalization, defensive copying, deterministic time, specific failure types, consistent lookup behavior, and atomic multi-field updates.

Run from `enhanced/service-suite`:

```text
mvn clean test
```

## Standalone verification

`evidence/SoftwareDesignVerification.java` provides a dependency-free check of the highest-risk behaviors. It compiles with the production source and performs 26 checks. The recorded run confirms:

- required text normalization and blank rejection;
- copies on service input and output;
- duplicate and missing-record policies;
- validated updates and deletion;
- preservation of stored state after failed multi-field updates;
- task field limits;
- defensive `Date` handling and deterministic past-date validation; and
- blank service-identifier rejection.

## Verification limits

The production source and standalone verifier were compiled with Java 17 and `-Xlint:all`, then the 26 checks were executed. The JUnit sources were also syntax-parsed and compiler-checked against minimal API stubs in the review environment. A normal Maven run remains the repeatable method for executing all 97 JUnit tests with the real JUnit dependency.
