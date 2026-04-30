# gRPC Metrics

## Overview

This library facilitates scraping for gRPC service metrics. It currently supports the following metrics for both client and server use cases:

* gRPC calls started - count
* gRPC calls completed - count
* Messages received - count
* Messages sent - count
* Completed gRPC calls latency - histogram

Metrics naming follows the standard grpc_USECASE_METRIC, where USECASE is either server or client and metric is the actual metric (examples below).
All metrics share the following labels: 
* servicename
* grpc_service
* grpc_method
* grpc_type

Some metrics like count of calls completed have other labels like response `status`.

## Metrics Examples

### Calls started
```
# HELP grpc_server_started_count Total number of RPCs started
# TYPE grpc_server_started_count counter
grpc_server_started_count_total{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",} 2.0
```

### Calls completed
```
# HELP grpc_server_completed_count Total number of RPCs completed
# TYPE grpc_server_completed_count counter
grpc_server_completed_count_total{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",status="OK",} 2.0
```

### Calls failed
```
# HELP grpc_server_failed_count Total number of RPCs that failed
# TYPE grpc_server_failed_count counter
grpc_server_failed_count_total{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",} 1.0
```

### Messages received
```
# HELP grpc_server_message_received_count Total number of messages received
# TYPE grpc_server_message_received_count counter
grpc_server_message_received_count_total{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",} 2.0
```

### Messages sent
```
# HELP grpc_server_message_sent_count Total number of messages sent
# TYPE grpc_server_message_sent_count counter
grpc_server_message_sent_count_total{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",} 246.0
```

### Latency

```
# HELP grpc_server_completed_latency_seconds  Histogram of response latency in seconds of gRPC calls completed
# TYPE grpc_server_completed_latency_seconds histogram
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="0.001",} 0.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="0.005",} 0.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="0.01",} 0.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="0.05",} 0.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="0.075",} 1.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="0.1",} 1.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="0.25",} 2.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="0.5",} 2.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="1.0",} 2.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="2.0",} 2.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="5.0",} 2.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="10.0",} 2.0
grpc_server_completed_latency_seconds_bucket{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",le="+Inf",} 2.0
grpc_server_completed_latency_seconds_count{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",} 2.0
grpc_server_completed_latency_seconds_sum{servicename="example-service",grpc_service="example.v1.ExampleService",grpc_method="listItems",grpc_type="SERVER_STREAMING",} 0.22
```

## Usage

### Server interceptor

```
ServerMonitoringInterceptor monitoringInterceptor = new ServerMonitoringInterceptor(YOUR_SERVICE_NAME,
    GrpcMetricsConfiguration.defaultConfiguration());
ServerBuilder serverBuilder = ServerBuilder.forPort(GRPC_PORT)
    .addService(ServerInterceptors.intercept(YOURAPIIMPL.bindService(), monitoringInterceptor));
```

### Client interceptor

```
ClientMonitoringInterceptor monitoringInterceptor = new ClientMonitoringInterceptor(YOUR_SERVICE_NAME,
    GrpcMetricsConfiguration.defaultConfiguration());
ManagedChannel channel = ManagedChannelBuilder.forAddress(YOUR_HOST, YOUR_PORT)
    .intercept(monitoringInterceptor)
    .usePlaintext().build();
```
