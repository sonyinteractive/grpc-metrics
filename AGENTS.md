# Agent Instructions

## Build And Test

- Before handing off code changes, make sure the project compiles and the unit tests pass.
- Use `mvn test` as the default verification command. This compiles main code, compiles test code, and runs the unit test suite.
- Add or update unit tests for behavior changes. Do not leave new logic untested unless there is a clear reason.

## Test Naming

- Unit test classes must be named after the class they test.
- Use the pattern `ClassNameTest`.
- Examples:
  - `CallTracker` -> `CallTrackerTest`
  - `ClientForwardingHandler` -> `ClientForwardingHandlerTest`

## Formatting And Style

- Keep formatting consistent with the surrounding Java code.
- Use clear names, readable line wrapping, and minimal comments that explain non-obvious behavior.
- Do not introduce unrelated style-only rewrites while making a focused change.
