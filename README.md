# gRPC Metrics

Java 11 interceptor library that records Prometheus metrics for gRPC client and server calls. The library wraps gRPC calls with client/server interceptors, derives labels from the gRPC `MethodDescriptor`, and registers counters and latency histograms with the Prometheus Java client.

## Start here

- **Purpose:** add Prometheus gRPC metrics to Java services without coupling to a specific service framework.
- **Runtime:** Java 11, gRPC Java API, Prometheus `simpleclient`.
- **Main entry points:** [`ClientMonitoringInterceptor`](src/main/java/com/sony/cgei/grpc/metrics/interceptors/ClientMonitoringInterceptor.java), [`ServerMonitoringInterceptor`](src/main/java/com/sony/cgei/grpc/metrics/interceptors/ServerMonitoringInterceptor.java), and [`GrpcMetricsConfiguration`](src/main/java/com/sony/cgei/grpc/metrics/configurations/GrpcMetricsConfiguration.java).
- **Build and test:** `mvn test`.

## Documentation index

| Topic | Description |
| --- | --- |
| [Usage guide](docs/usage.md) | How to add server/client interceptors and customize metric configuration. |
| [Metrics reference](docs/metrics-reference.md) | Metric names, labels, default latency buckets, and event timing. |
| [Architecture and code map](docs/architecture.md) | Interceptor, handler, listener, tracker, and collector responsibilities. |
| [Testing](docs/testing.md) | Test layout, naming rules, and verification commands. |
| [AI context guide](docs/ai-context.md) | Lightweight routing map for agent-assisted maintenance. |
| [Contributing](CONTRIBUTING.md) | Contribution, review, testing, and release expectations. |

## Quick usage

### Server interceptor

```java
ServerMonitoringInterceptor monitoringInterceptor = new ServerMonitoringInterceptor(
    YOUR_SERVICE_NAME,
    GrpcMetricsConfiguration.defaultConfiguration());

ServerBuilder<?> serverBuilder = ServerBuilder.forPort(GRPC_PORT)
    .addService(ServerInterceptors.intercept(YOUR_API_IMPL.bindService(), monitoringInterceptor));
```

### Client interceptor

```java
ClientMonitoringInterceptor monitoringInterceptor = new ClientMonitoringInterceptor(
    YOUR_SERVICE_NAME,
    GrpcMetricsConfiguration.defaultConfiguration());

ManagedChannel channel = ManagedChannelBuilder.forAddress(YOUR_HOST, YOUR_PORT)
    .intercept(monitoringInterceptor)
    .usePlaintext()
    .build();
```

See the [usage guide](docs/usage.md) for custom `CollectorRegistry` and latency bucket examples.

## Metrics summary

Metrics use the Prometheus namespace `grpc` and the subsystem `server` or `client`:

| Metric family | Type | Description | Extra labels |
| --- | --- | --- | --- |
| `grpc_server_started_count` / `grpc_client_started_count` | Counter | RPCs started. | None |
| `grpc_server_completed_count` / `grpc_client_completed_count` | Counter | RPCs completed. | `status` |
| `grpc_server_failed_count` / `grpc_client_failed_count` | Counter | RPCs completed with non-`OK` status. | None |
| `grpc_server_message_received_count` / `grpc_client_message_received_count` | Counter | Messages received. | None |
| `grpc_server_message_sent_count` / `grpc_client_message_sent_count` | Counter | Messages sent. | None |
| `grpc_server_completed_latency_seconds` / `grpc_client_completed_latency_seconds` | Histogram | Completed call latency in seconds. | None |

All metric families include `servicename`, `grpc_service`, `grpc_method`, and `grpc_type` labels. In Prometheus text exposition, counter samples are emitted with the usual `_total` suffix. See the [metrics reference](docs/metrics-reference.md) for details.

## Repository map

| Path | Purpose |
| --- | --- |
| [`pom.xml`](pom.xml) | Maven coordinates, Java version, and dependencies. |
| [`src/main/java/com/sony/cgei/grpc/metrics/configurations`](src/main/java/com/sony/cgei/grpc/metrics/configurations) | Metrics configuration and Prometheus registry selection. |
| [`src/main/java/com/sony/cgei/grpc/metrics/interceptors`](src/main/java/com/sony/cgei/grpc/metrics/interceptors) | Public gRPC client/server interceptors. |
| [`src/main/java/com/sony/cgei/grpc/metrics/handlers`](src/main/java/com/sony/cgei/grpc/metrics/handlers) | Forwarding client/server call wrappers that record start, send, close, and latency events. |
| [`src/main/java/com/sony/cgei/grpc/metrics/listeners`](src/main/java/com/sony/cgei/grpc/metrics/listeners) | Forwarding listeners that record inbound messages and client close events. |
| [`src/main/java/com/sony/cgei/grpc/metrics/models`](src/main/java/com/sony/cgei/grpc/metrics/models) | Metric family registration and gRPC method label extraction. |
| [`src/main/java/com/sony/cgei/grpc/metrics/trackers`](src/main/java/com/sony/cgei/grpc/metrics/trackers) | Label application and metric updates. |
| [`src/main/java/com/sony/cgei/grpc/metrics/util`](src/main/java/com/sony/cgei/grpc/metrics/util) | Prometheus collector builder helpers. |
| [`src/test/java`](src/test/java) | JUnit/Mockito unit tests. |
| [`docs`](docs) | Maintainer and user documentation. |

## Verify changes

```bash
mvn test
python3 /Users/rcortezbellottideoli/.codex/skills/project-docs/scripts/check-docs-links.py .
```
