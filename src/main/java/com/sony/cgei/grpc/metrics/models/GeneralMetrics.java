package com.sony.cgei.grpc.metrics.models;

import com.sony.cgei.grpc.metrics.configurations.GrpcMetricsConfiguration;
import com.sony.cgei.grpc.metrics.util.CollectorUtil;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

import java.util.List;

public class GeneralMetrics {

    private Counter startedCounter;
    private Counter completedCounter;
    private Counter failedCounter;
    private Counter messagesReceivedCounter;
    private Counter messagesSentCounter;
    private Histogram completedLatencyHistogram;

    /**
     * General metrics for both client and server side cases
     * @param configuration gRPC metrics configuration
     * @param subsystem determines if it is a client side or server side metric
     */
    public GeneralMetrics(GrpcMetricsConfiguration configuration, String subsystem) {
        startedCounter = CollectorUtil.buildCounter(subsystem, "started_count",
                "Total number of RPCs started")
                .register(configuration.getRegistry());
        completedCounter = CollectorUtil.buildCounter(subsystem, "completed_count",
                "Total number of RPCs completed", List.of("status"))
                .register(configuration.getRegistry());
        failedCounter = CollectorUtil.buildCounter(subsystem, "failed_count",
                "Total number of RPCs that failed")
                .register(configuration.getRegistry());
        messagesReceivedCounter = CollectorUtil.buildCounter(subsystem, "message_received_count",
                "Total number of messages received")
                .register(configuration.getRegistry());
        messagesSentCounter = CollectorUtil.buildCounter(subsystem, "message_sent_count",
                "Total number of messages sent")
                .register(configuration.getRegistry());
        completedLatencyHistogram = CollectorUtil.buildHistogram(subsystem, "completed_latency_seconds",
                configuration.getLatencyBuckets(), " Histogram of response latency in seconds of gRPC calls completed")
                .register(configuration.getRegistry());
    }

    public Counter getStartedCounter() {
        return startedCounter;
    }

    public Counter getCompletedCounter() {
        return completedCounter;
    }

    public Counter getMessagesReceivedCounter() {
        return messagesReceivedCounter;
    }

    public Counter getMessagesSentCounter() {
        return messagesSentCounter;
    }

    public Histogram getCompletedLatencyHistogram() {
        return completedLatencyHistogram;
    }

    public Counter getFailedCounter() {
        return failedCounter;
    }
}
