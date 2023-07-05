package com.sony.cgei.grpc.metrics.models;

import com.sony.cgei.grpc.metrics.configurations.GrpcMetricsConfiguration;

public class ServerMetrics extends GeneralMetrics {

    private static final String SUBSYSTEM = "server";

    public ServerMetrics(GrpcMetricsConfiguration configuration) {
        super(configuration, SUBSYSTEM);
    }
}
