# AI Context Guide

## How agents should use this repo

- Start with [`README.md`](../README.md), [`AGENTS.md`](../AGENTS.md), and this guide.
- Use the routing map below to choose the first docs, source files, tests, and config files to inspect for the task.
- Read referenced source files and tests directly before proposing or making changes.
- Keep documentation links relative and update this guide when ownership, entry points, validation commands, or important docs move.

## Repository profile

- **Project type:** Java library for gRPC/Prometheus metrics instrumentation.
- **Build tool:** Maven.
- **Primary package:** `com.sony.cgei.grpc.metrics`.
- **Primary validation:** `mvn test`.
- **Docs validation:** `python3 /Users/rcortezbellottideoli/.codex/skills/project-docs/scripts/check-docs-links.py .`.

## Task routing map

| Task area | Read first | Code entry points | Tests | Config/docs |
| --- | --- | --- | --- | --- |
| Add or change server instrumentation | [`docs/architecture.md`](architecture.md), [`docs/metrics-reference.md`](metrics-reference.md) | [`ServerMonitoringInterceptor`](../src/main/java/com/sony/cgei/grpc/metrics/interceptors/ServerMonitoringInterceptor.java), [`ServerForwardingHandler`](../src/main/java/com/sony/cgei/grpc/metrics/handlers/ServerForwardingHandler.java), [`ServerCallListener`](../src/main/java/com/sony/cgei/grpc/metrics/listeners/ServerCallListener.java) | [`ServerForwardingHandlerTest`](../src/test/java/com/sony/cgei/grpc/metrics/handlers/ServerForwardingHandlerTest.java), [`ServerCallListenerTest`](../src/test/java/com/sony/cgei/grpc/metrics/listeners/ServerCallListenerTest.java) | [`docs/usage.md`](usage.md) |
| Add or change client instrumentation | [`docs/architecture.md`](architecture.md), [`docs/metrics-reference.md`](metrics-reference.md) | [`ClientMonitoringInterceptor`](../src/main/java/com/sony/cgei/grpc/metrics/interceptors/ClientMonitoringInterceptor.java), [`ClientForwardingHandler`](../src/main/java/com/sony/cgei/grpc/metrics/handlers/ClientForwardingHandler.java), [`ClientCallListener`](../src/main/java/com/sony/cgei/grpc/metrics/listeners/ClientCallListener.java) | [`ClientForwardingHandlerTest`](../src/test/java/com/sony/cgei/grpc/metrics/handlers/ClientForwardingHandlerTest.java), [`ClientCallListenerTest`](../src/test/java/com/sony/cgei/grpc/metrics/listeners/ClientCallListenerTest.java) | [`docs/usage.md`](usage.md) |
| Add or rename metric families | [`docs/metrics-reference.md`](metrics-reference.md), [`docs/architecture.md`](architecture.md) | [`GeneralMetrics`](../src/main/java/com/sony/cgei/grpc/metrics/models/GeneralMetrics.java), [`CollectorUtil`](../src/main/java/com/sony/cgei/grpc/metrics/util/CollectorUtil.java), [`CallTracker`](../src/main/java/com/sony/cgei/grpc/metrics/trackers/CallTracker.java) | [`CallTrackerTest`](../src/test/java/com/sony/cgei/grpc/metrics/trackers/CallTrackerTest.java) | Update [`README.md`](../README.md) and [`docs/metrics-reference.md`](metrics-reference.md) |
| Change labels or method extraction | [`docs/metrics-reference.md`](metrics-reference.md) | [`MethodDefinition`](../src/main/java/com/sony/cgei/grpc/metrics/models/MethodDefinition.java), [`CollectorUtil`](../src/main/java/com/sony/cgei/grpc/metrics/util/CollectorUtil.java), [`CallTracker`](../src/main/java/com/sony/cgei/grpc/metrics/trackers/CallTracker.java) | [`CallTrackerTest`](../src/test/java/com/sony/cgei/grpc/metrics/trackers/CallTrackerTest.java) | Update metric examples in docs |
| Change configuration defaults | [`docs/usage.md`](usage.md), [`docs/metrics-reference.md`](metrics-reference.md) | [`GrpcMetricsConfiguration`](../src/main/java/com/sony/cgei/grpc/metrics/configurations/GrpcMetricsConfiguration.java), [`GeneralMetrics`](../src/main/java/com/sony/cgei/grpc/metrics/models/GeneralMetrics.java) | Add or update `GrpcMetricsConfigurationTest`; check `CallTrackerTest` isolated registry setup | [`pom.xml`](../pom.xml) if dependency versions change |
| Update docs only | [`README.md`](../README.md), this guide | Relevant source paths linked from the doc being changed | Not usually required unless docs expose behavior that should be verified | Run docs link checker |
| Release or contribution process | [`CONTRIBUTING.md`](../CONTRIBUTING.md), [`README.md`](../README.md) | [`pom.xml`](../pom.xml) | `mvn test` | [`release.properties`](../release.properties) is a generated Maven release file and is ignored by `.gitignore` |

## Validation matrix

| Change type | Minimum validation |
| --- | --- |
| Java code behavior | `mvn test` |
| Metric names, labels, or buckets | `mvn test`; update [metrics reference](metrics-reference.md) |
| Interceptor usage examples | `mvn test` when examples reflect code behavior; docs link checker |
| Documentation only | Docs link checker |
| Build or dependency changes | `mvn test` |

## Context boundaries

This guide is an index, not a substitute for reading source. It intentionally omits exhaustive symbol lists, generated manifests, and full repository dumps. If a task touches code, tests, configuration, metric contracts, or operational behavior, inspect the linked files directly and follow [`AGENTS.md`](../AGENTS.md) before editing.
