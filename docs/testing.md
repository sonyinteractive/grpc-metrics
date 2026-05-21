# Testing

## Test stack

The project uses Maven, JUnit 4, Mockito, and Java 11. Dependency versions are defined in [`pom.xml`](../pom.xml).

## Verification commands

Run the full unit test suite before handing off changes:

```bash
mvn test
```

Run the documentation link checker after documentation edits:

```bash
python3 /Users/rcortezbellottideoli/.codex/skills/project-docs/scripts/check-docs-links.py .
```

## Test layout

| Test | Source under test | Behavior covered |
| --- | --- | --- |
| [`ClientForwardingHandlerTest`](../src/test/java/com/sony/cgei/grpc/metrics/handlers/ClientForwardingHandlerTest.java) | [`ClientForwardingHandler`](../src/main/java/com/sony/cgei/grpc/metrics/handlers/ClientForwardingHandler.java) | Records call start and sent request messages. |
| [`ServerForwardingHandlerTest`](../src/test/java/com/sony/cgei/grpc/metrics/handlers/ServerForwardingHandlerTest.java) | [`ServerForwardingHandler`](../src/main/java/com/sony/cgei/grpc/metrics/handlers/ServerForwardingHandler.java) | Records call start, sent response messages, completion status, and latency. |
| [`ClientCallListenerTest`](../src/test/java/com/sony/cgei/grpc/metrics/listeners/ClientCallListenerTest.java) | [`ClientCallListener`](../src/main/java/com/sony/cgei/grpc/metrics/listeners/ClientCallListener.java) | Delegation, received response messages, completion status, and latency. |
| [`ServerCallListenerTest`](../src/test/java/com/sony/cgei/grpc/metrics/listeners/ServerCallListenerTest.java) | [`ServerCallListener`](../src/main/java/com/sony/cgei/grpc/metrics/listeners/ServerCallListener.java) | Delegation and received request messages. |
| [`CallTrackerTest`](../src/test/java/com/sony/cgei/grpc/metrics/trackers/CallTrackerTest.java) | [`CallTracker`](../src/main/java/com/sony/cgei/grpc/metrics/trackers/CallTracker.java), `GeneralMetrics`, `MethodDefinition` | Label application, counters, failure handling, and latency histogram recording. |

## Naming rules

Unit test classes must be named after the class they test, using the `ClassNameTest` pattern. Examples:

- `CallTracker` -> `CallTrackerTest`
- `ClientForwardingHandler` -> `ClientForwardingHandlerTest`

## Adding tests

- Add or update unit tests for behavior changes.
- Prefer focused tests that verify the tracker method called by a handler or listener.
- For metric family or label changes, use an isolated `CollectorRegistry` to avoid global registry state and assert collected labels/samples.
- Keep test fixtures close to the behavior under test; this repository currently uses Mockito mocks rather than spinning up an end-to-end gRPC server.

## Coverage expectation

[`CONTRIBUTING.md`](../CONTRIBUTING.md) says code changes should include relevant unit tests and should not drop code coverage below 75%.
