# Metrics Reference

## Naming convention

Metrics use the Prometheus namespace `grpc`, subsystem `server` or `client`, and a metric-specific name. For example, the server started counter is registered as `grpc_server_started_count`; Prometheus counter exposition also emits samples with the `_total` suffix.

The naming and label definitions are built in [`CollectorUtil`](../src/main/java/com/sony/cgei/grpc/metrics/util/CollectorUtil.java) and [`GeneralMetrics`](../src/main/java/com/sony/cgei/grpc/metrics/models/GeneralMetrics.java).

## Common labels

| Label | Source | Description |
| --- | --- | --- |
| `servicename` | Interceptor constructor argument | Name of the service that owns the client channel or server. |
| `grpc_service` | `MethodDescriptor.extractFullServiceName` | Fully qualified gRPC service name from the method descriptor. |
| `grpc_method` | Full method name suffix after `<service>/` | gRPC method name. |
| `grpc_type` | `MethodDescriptor.MethodType` | gRPC method type such as `UNARY`, `SERVER_STREAMING`, `CLIENT_STREAMING`, or `BIDI_STREAMING`. |

The completed-call counter also includes `status`, the string form of `io.grpc.Status.Code`.

## Metric families

| Family | Subsystems | Type | Labels | Recorded when |
| --- | --- | --- | --- | --- |
| `grpc_<subsystem>_started_count` | `server`, `client` | Counter | Common labels | Server call wrapper is created; client call starts. |
| `grpc_<subsystem>_completed_count` | `server`, `client` | Counter | Common labels + `status` | Server call closes; client call closes. |
| `grpc_<subsystem>_failed_count` | `server`, `client` | Counter | Common labels | Completion status is not `OK`. |
| `grpc_<subsystem>_message_received_count` | `server`, `client` | Counter | Common labels | Wrapped listener receives a message. |
| `grpc_<subsystem>_message_sent_count` | `server`, `client` | Counter | Common labels | Wrapped call sends a message. |
| `grpc_<subsystem>_completed_latency_seconds` | `server`, `client` | Histogram | Common labels | Server call closes; client call closes. |

## Default latency buckets

[`GrpcMetricsConfiguration.defaultConfiguration`](../src/main/java/com/sony/cgei/grpc/metrics/configurations/GrpcMetricsConfiguration.java) uses these histogram buckets, in seconds:

| Bucket upper bound |
| --- |
| `0.005` |
| `0.01` |
| `0.05` |
| `0.075` |
| `0.1` |
| `0.25` |
| `0.5` |
| `0.75` |
| `1.0` |
| `2.5` |
| `5.0` |
| `10.0` |

Prometheus also includes the implicit `+Inf` bucket, plus `_count` and `_sum` samples for histograms.

## Example exposition

### Started calls

```text
# HELP grpc_server_started_count Total number of RPCs started
# TYPE grpc_server_started_count counter
grpc_server_started_count_total{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="ListItems",grpc_type="SERVER_STREAMING"} 2.0
```

### Completed calls

```text
# HELP grpc_server_completed_count Total number of RPCs completed
# TYPE grpc_server_completed_count counter
grpc_server_completed_count_total{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="ListItems",grpc_type="SERVER_STREAMING",status="OK"} 2.0
```

### Failed calls

```text
# HELP grpc_server_failed_count Total number of RPCs that failed
# TYPE grpc_server_failed_count counter
grpc_server_failed_count_total{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="ListItems",grpc_type="SERVER_STREAMING"} 1.0
```

### Latency histogram

```text
# HELP grpc_server_completed_latency_seconds Histogram of response latency in seconds of gRPC calls completed
# TYPE grpc_server_completed_latency_seconds histogram
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="ListItems",grpc_type="SERVER_STREAMING",le="0.1"} 1.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="ListItems",grpc_type="SERVER_STREAMING",le="+Inf"} 2.0
grpc_server_completed_latency_seconds_count{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="ListItems",grpc_type="SERVER_STREAMING"} 2.0
grpc_server_completed_latency_seconds_sum{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="ListItems",grpc_type="SERVER_STREAMING"} 0.22
```

## Timing details

- Server latency is measured from `ServerForwardingHandler` construction until `close`.
- Client latency is measured from `ClientCallListener` construction during call start until `onClose`.
- Timing uses `Instant.now().toEpochMilli()` and stores latency in seconds.

## Related docs

- [Usage guide](usage.md)
- [Architecture and code map](architecture.md)
