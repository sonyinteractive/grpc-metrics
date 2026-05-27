# Architecture and Code Map

## Purpose

This library adds metrics by wrapping gRPC calls with interceptors, forwarding handlers, and forwarding listeners. It does not implement service business logic; it observes gRPC lifecycle events and records Prometheus metrics.

## Module map

| Path | Responsibility |
| --- | --- |
| [`configurations/GrpcMetricsConfiguration.java`](../src/main/java/com/sony/cgei/grpc/metrics/configurations/GrpcMetricsConfiguration.java) | Holds the Prometheus `CollectorRegistry` and latency bucket settings. |
| [`interceptors/ClientMonitoringInterceptor.java`](../src/main/java/com/sony/cgei/grpc/metrics/interceptors/ClientMonitoringInterceptor.java) | Public client interceptor; creates per-call trackers and client forwarding handlers. |
| [`interceptors/ServerMonitoringInterceptor.java`](../src/main/java/com/sony/cgei/grpc/metrics/interceptors/ServerMonitoringInterceptor.java) | Public server interceptor; creates per-call trackers, server forwarding handlers, and server call listeners. |
| [`handlers/ClientForwardingHandler.java`](../src/main/java/com/sony/cgei/grpc/metrics/handlers/ClientForwardingHandler.java) | Wraps client calls to record started calls and sent request messages. |
| [`handlers/ServerForwardingHandler.java`](../src/main/java/com/sony/cgei/grpc/metrics/handlers/ServerForwardingHandler.java) | Wraps server calls to record started calls, sent response messages, completion, and latency. |
| [`listeners/ClientCallListener.java`](../src/main/java/com/sony/cgei/grpc/metrics/listeners/ClientCallListener.java) | Wraps client listeners to record received response messages, completion, and latency. |
| [`listeners/ServerCallListener.java`](../src/main/java/com/sony/cgei/grpc/metrics/listeners/ServerCallListener.java) | Wraps server listeners to record received request messages. |
| [`models/GeneralMetrics.java`](../src/main/java/com/sony/cgei/grpc/metrics/models/GeneralMetrics.java) | Registers shared counter and histogram families. |
| [`models/ClientMetrics.java`](../src/main/java/com/sony/cgei/grpc/metrics/models/ClientMetrics.java) | Selects the `client` subsystem for shared metric families. |
| [`models/ServerMetrics.java`](../src/main/java/com/sony/cgei/grpc/metrics/models/ServerMetrics.java) | Selects the `server` subsystem for shared metric families. |
| [`models/MethodDefinition.java`](../src/main/java/com/sony/cgei/grpc/metrics/models/MethodDefinition.java) | Extracts service, method, and type labels from a gRPC `MethodDescriptor`. |
| [`trackers/CallTracker.java`](../src/main/java/com/sony/cgei/grpc/metrics/trackers/CallTracker.java) | Applies labels and increments or observes metric values. |
| [`util/CollectorUtil.java`](../src/main/java/com/sony/cgei/grpc/metrics/util/CollectorUtil.java) | Builds Prometheus counters and histograms with consistent namespace and labels. |

## Server call flow

1. The host service installs `ServerMonitoringInterceptor` with a service name and `GrpcMetricsConfiguration`.
2. `interceptCall` reads the call's `MethodDescriptor` and creates a `MethodDefinition`.
3. The interceptor creates a `CallTracker` using `ServerMetrics`, the configured service name, and the method definition.
4. `ServerForwardingHandler` wraps the `ServerCall` and records `recordCallStarted` during construction.
5. `ServerCallListener` wraps the delegate listener returned by `callHandler.startCall`.
6. Request messages call `ServerCallListener.onMessage`, which records messages received.
7. Response messages call `ServerForwardingHandler.sendMessage`, which records messages sent.
8. Close calls `ServerForwardingHandler.close`, which records completion status, failed-call count when applicable, latency, and then delegates the close.

## Client call flow

1. The host service installs `ClientMonitoringInterceptor` on a `ManagedChannel`.
2. `interceptCall` reads the method descriptor and creates a `MethodDefinition`.
3. The interceptor creates a `CallTracker` using `ClientMetrics`, the configured service name, and the method definition.
4. `ClientForwardingHandler` wraps `channel.newCall`.
5. `ClientForwardingHandler.start` records `recordCallStarted` and wraps the delegate listener in `ClientCallListener`.
6. Request messages call `ClientForwardingHandler.sendMessage`, which records messages sent.
7. Response messages call `ClientCallListener.onMessage`, which records messages received.
8. Completion calls `ClientCallListener.onClose`, which records completion status, failed-call count when applicable, latency, and then delegates the close.

## Metric registration flow

1. `ClientMonitoringInterceptor` constructs `ClientMetrics`; `ServerMonitoringInterceptor` constructs `ServerMetrics`.
2. `ClientMetrics` and `ServerMetrics` extend `GeneralMetrics` with the `client` or `server` subsystem.
3. `GeneralMetrics` registers the six metric families against the configured `CollectorRegistry`.
4. `CollectorUtil` centralizes the `grpc` namespace and standard labels.

## Extension points

| Change | Start with | Add or update tests |
| --- | --- | --- |
| Add a new metric family | `GeneralMetrics`, `CollectorUtil`, `CallTracker` | `CallTrackerTest` and focused handler/listener tests that trigger the metric. |
| Change metric labels | `CollectorUtil`, `CallTracker`, `MethodDefinition` | `CallTrackerTest`; update [metrics reference](metrics-reference.md). |
| Change client event timing | `ClientForwardingHandler`, `ClientCallListener` | `ClientForwardingHandlerTest`, `ClientCallListenerTest`. |
| Change server event timing | `ServerForwardingHandler`, `ServerCallListener` | `ServerForwardingHandlerTest`, `ServerCallListenerTest`. |
| Change defaults or registry behavior | `GrpcMetricsConfiguration`, `GeneralMetrics` | Add a `GrpcMetricsConfigurationTest` and update usage docs. |

## Boundaries

- No persistence layer is present in this repository.
- No deployment manifest, runtime service, or HTTP metrics endpoint is present in this repository.
- Prometheus scrape endpoint wiring belongs to the consuming service.

## Related docs

- [Usage guide](usage.md)
- [Metrics reference](metrics-reference.md)
- [Testing](testing.md)
