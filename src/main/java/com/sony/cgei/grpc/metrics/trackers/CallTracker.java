package com.sony.cgei.grpc.metrics.trackers;

import com.sony.cgei.grpc.metrics.models.GeneralMetrics;
import com.sony.cgei.grpc.metrics.models.MethodDefinition;
import io.grpc.Status;
import io.prometheus.client.SimpleCollector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CallTracker {

    private GeneralMetrics generalMetrics;
    private MethodDefinition method;
    private String serviceName;

    public CallTracker(GeneralMetrics generalMetrics, String serviceName, MethodDefinition method) {
        this.generalMetrics = generalMetrics;
        this.serviceName = serviceName;
        this.method = method;
    }

    /**
     * Records metrics related to a gRPC call start
     */
    public void recordCallStarted() {
        setLabels(generalMetrics.getStartedCounter()).inc();
    }

    /**
     * Records metrics related to a gRPC call completion
     * @param code response status code
     */
    public void recordCompleted(Status.Code code) {
        setLabels(generalMetrics.getCompletedCounter(), code.toString()).inc();

        if (!code.equals(Status.Code.OK)) {
            setLabels(generalMetrics.getFailedCounter()).inc();
        }
    }

    /**
     * Record metrics related to a single message that was sent
     */
    public void recordMessageSent() {
        setLabels(generalMetrics.getMessagesSentCounter()).inc();
    }

    /**
     * Record metrics related to a single message received
     */
    public void recordMessageReceived() {
        setLabels(generalMetrics.getMessagesReceivedCounter()).inc();
    }

    /**
     * Records latency for a gRPC completed call
     * @param latency latency value
     */
    public void recordLatency(double latency) {
        setLabels(generalMetrics.getCompletedLatencyHistogram()).observe(latency);
    }

    /**
     * Prepares labeled values for a collector
     * @param collector
     * @param additionalLabels
     * @param <T>
     * @return prepared metric
     */
    private <T> T setLabels(SimpleCollector<T> collector, String... additionalLabels) {
        List<String> allLabels = new ArrayList<>();
        Collections.addAll(allLabels, serviceName, method.getServiceName(), method.getMethodName(), method.getType());
        Collections.addAll(allLabels, additionalLabels);

        return collector.labels(allLabels.toArray(new String[0]));
    }
}
