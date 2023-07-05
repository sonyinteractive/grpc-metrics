package com.sony.cgei.grpc.metrics.models;

import com.sony.cgei.grpc.metrics.configurations.GrpcMetricsConfiguration;

public class ClientMetrics extends GeneralMetrics {

    private static final String SUBSYSTEM = "client";

    public ClientMetrics(GrpcMetricsConfiguration configuration) {
        super(configuration, SUBSYSTEM);
    }
}
