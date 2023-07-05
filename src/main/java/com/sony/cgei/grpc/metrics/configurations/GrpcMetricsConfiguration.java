package com.sony.cgei.grpc.metrics.configurations;

import io.prometheus.client.CollectorRegistry;

public class GrpcMetricsConfiguration {

    private static double[] DEFAULT_HISTOGRAM_BUCKETS =
            new double[] {0.005D, 0.01D, 0.05D, 0.075D, 0.1D, 0.25D, 0.5D, 0.75D, 1.0D, 2.5D, 5.0D, 10.0D};

    private final CollectorRegistry registry;
    private final double[] latencyBuckets;

    private GrpcMetricsConfiguration(CollectorRegistry registry, double[] latencyBuckets) {
        this.registry = registry;
        this.latencyBuckets = latencyBuckets;
    }

    public static GrpcMetricsConfiguration defaultConfiguration() {
        return new GrpcMetricsConfiguration(CollectorRegistry.defaultRegistry, DEFAULT_HISTOGRAM_BUCKETS);
    }

    public GrpcMetricsConfiguration withCollectorRegistry(CollectorRegistry registry) {
        return new GrpcMetricsConfiguration(registry, this.latencyBuckets);
    }

    public GrpcMetricsConfiguration withLatencyBuckets(double[] latencyBuckets) {
        return new GrpcMetricsConfiguration(this.registry, latencyBuckets);
    }

    public CollectorRegistry getRegistry() {
        return registry;
    }

    public double[] getLatencyBuckets() {
        return latencyBuckets.clone();
    }
}
