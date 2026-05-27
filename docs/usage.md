# Usage Guide

## Purpose

Use this library when a Java gRPC client or server needs Prometheus metrics for call counts, message counts, completion status, failures, and latency. The public integration points are the client and server interceptors in [`src/main/java/com/sony/cgei/grpc/metrics/interceptors`](../src/main/java/com/sony/cgei/grpc/metrics/interceptors).

## Dependency coordinates

The Maven coordinates in this repository are defined in [`pom.xml`](../pom.xml):

```xml
<dependency>
  <groupId>com.sony.cgei.grpc</groupId>
  <artifactId>grpc-metrics</artifactId>
  <version>0.0.3-SNAPSHOT</version>
</dependency>
```

Use the version and repository source appropriate for the consuming service. For local development, `mvn install` can publish the artifact to the local Maven repository.

## Server instrumentation

Register [`ServerMonitoringInterceptor`](../src/main/java/com/sony/cgei/grpc/metrics/interceptors/ServerMonitoringInterceptor.java) around a gRPC service definition:

```java
ServerMonitoringInterceptor monitoringInterceptor = new ServerMonitoringInterceptor(
    "example-service",
    GrpcMetricsConfiguration.defaultConfiguration());

ServerBuilder<?> serverBuilder = ServerBuilder.forPort(8080)
    .addService(ServerInterceptors.intercept(exampleApi.bindService(), monitoringInterceptor));
```

Server-side recording flow:

1. `ServerMonitoringInterceptor.interceptCall` derives a `MethodDefinition` from the call descriptor.
2. `ServerForwardingHandler` records the RPC start when it wraps the server call.
3. `ServerCallListener.onMessage` records each received request message.
4. `ServerForwardingHandler.sendMessage` records each response message sent.
5. `ServerForwardingHandler.close` records completion status, failure count when status is not `OK`, and call latency.

## Client instrumentation

Register [`ClientMonitoringInterceptor`](../src/main/java/com/sony/cgei/grpc/metrics/interceptors/ClientMonitoringInterceptor.java) on a managed channel:

```java
ClientMonitoringInterceptor monitoringInterceptor = new ClientMonitoringInterceptor(
    "example-service",
    GrpcMetricsConfiguration.defaultConfiguration());

ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
    .intercept(monitoringInterceptor)
    .usePlaintext()
    .build();
```

Client-side recording flow:

1. `ClientMonitoringInterceptor.interceptCall` derives a `MethodDefinition` from the method descriptor.
2. `ClientForwardingHandler.start` records the RPC start and wraps the client listener.
3. `ClientForwardingHandler.sendMessage` records each request message sent.
4. `ClientCallListener.onMessage` records each response message received.
5. `ClientCallListener.onClose` records completion status, failure count when status is not `OK`, and call latency.

## Configuration

[`GrpcMetricsConfiguration`](../src/main/java/com/sony/cgei/grpc/metrics/configurations/GrpcMetricsConfiguration.java) selects the Prometheus `CollectorRegistry` and latency histogram buckets.

### Default configuration

```java
GrpcMetricsConfiguration configuration = GrpcMetricsConfiguration.defaultConfiguration();
```

The default configuration uses `CollectorRegistry.defaultRegistry` and these latency buckets, in seconds:

```text
0.005, 0.01, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1.0, 2.5, 5.0, 10.0
```

### Custom collector registry

Use a custom registry when tests or host applications need isolated collector state:

```java
CollectorRegistry registry = new CollectorRegistry();
GrpcMetricsConfiguration configuration = GrpcMetricsConfiguration.defaultConfiguration()
    .withCollectorRegistry(registry);
```

### Custom latency buckets

```java
GrpcMetricsConfiguration configuration = GrpcMetricsConfiguration.defaultConfiguration()
    .withLatencyBuckets(new double[] {0.01, 0.05, 0.1, 0.5, 1.0, 5.0});
```

The configuration methods return new configuration instances; they do not mutate the existing instance.

## Exposing metrics

This repository registers Prometheus collectors but does not include an HTTP metrics endpoint or service framework adapter. The consuming service is responsible for exposing the selected `CollectorRegistry` through its existing Prometheus scrape endpoint. This is inferred from the repository source: the code builds Prometheus collectors and gRPC interceptors, but no HTTP exporter or web framework integration is present.

## Operational notes

- The `servicename` label comes from the string passed to the interceptor constructor.
- The `grpc_service`, `grpc_method`, and `grpc_type` labels come from the gRPC `MethodDescriptor`.
- The constructors for `ClientMetrics` and `ServerMetrics` register collectors in the configured registry. Avoid registering duplicate collectors with the same metric names in the same `CollectorRegistry` lifecycle.

## Related docs

- [Metrics reference](metrics-reference.md)
- [Architecture and code map](architecture.md)
- [Testing](testing.md)
