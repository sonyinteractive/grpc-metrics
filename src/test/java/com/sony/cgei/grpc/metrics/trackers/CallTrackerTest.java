package com.sony.cgei.grpc.metrics.trackers;

import com.sony.cgei.grpc.metrics.configurations.GrpcMetricsConfiguration;
import com.sony.cgei.grpc.metrics.models.GeneralMetrics;
import com.sony.cgei.grpc.metrics.models.MethodDefinition;
import com.sony.cgei.grpc.metrics.models.ServerMetrics;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.SimpleCollector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CallTrackerTest {

    private static final String SERVICE_NAME_LABEL = "servicename";
    private static final String GRPC_SERVICE_LABEL = "grpc_service";
    private static final String GRPC_METHOD_LABEL = "grpc_method";
    private static final String GRPC_TYPE_LABEL = "grpc_type";
    private static final String STATUS_LABEL = "status";
    private static final String SERVICE_NAME = "test";
    private static final MethodDescriptor.MethodType METHOD_TYPE = MethodDescriptor.MethodType.SERVER_STREAMING;
    private static final String GRPC_SERVICE = "com.service";
    private static final String GRPC_METHOD = "method";
    private static final String GRPC_FULL_SERVICE_NAME = String.format("%s/%s", GRPC_SERVICE, GRPC_METHOD);
    private static final Status.Code CODE = Status.Code.INTERNAL;

    private GeneralMetrics metrics;
    private CallTracker callTracker;
    private CollectorRegistry collectorRegistry;

    @Before
    public void setup() {
        MethodDescriptor method = MethodDescriptor.<String, Integer>newBuilder()
                .setType(METHOD_TYPE)
                .setFullMethodName(GRPC_FULL_SERVICE_NAME)
                .setRequestMarshaller(mock(MethodDescriptor.Marshaller.class))
                .setResponseMarshaller(mock(MethodDescriptor.Marshaller.class))
                .build();
        MethodDefinition methodDefinition = MethodDefinition.of(method);
        collectorRegistry = new CollectorRegistry();
        GrpcMetricsConfiguration metricsConfiguration = GrpcMetricsConfiguration
                .defaultConfiguration()
                .withCollectorRegistry(collectorRegistry)
                .withLatencyBuckets(new double[] {.005, .01, .05, .1, .5, 1, 5, 10});
        metrics = new ServerMetrics(metricsConfiguration);
        callTracker = new CallTracker(metrics, SERVICE_NAME, methodDefinition);
    }

    @After
    public void terminate() {
        collectorRegistry.clear();
    }

    @Test
    public void recordCallStarted() {
        callTracker.recordCallStarted();
        validateMetrics(metrics.getStartedCounter(), false);
    }

    @Test
    public void recordCompleted() {
        callTracker.recordCompleted(CODE);
        validateMetrics(metrics.getCompletedCounter(), true);
        validateMetrics(metrics.getFailedCounter(), false);
    }

    @Test
    public void recordMessageReceived() {
        callTracker.recordMessageReceived();
        validateMetrics(metrics.getMessagesReceivedCounter(), false);
    }

    @Test
    public void recordMessageSent() {
        callTracker.recordMessageSent();
        validateMetrics(metrics.getMessagesSentCounter(), false);
    }

    @Test
    public void recordLatency() {
        double latency = 0.2;
        callTracker.recordLatency(latency);
        validateMetrics(metrics.getCompletedLatencyHistogram(), false);
    }

    private <T> void validateMetrics(SimpleCollector<T> collector, boolean verifyCode) {
        List<Collector.MetricFamilySamples> samples = collector.collect();
        assertEquals(1, samples.size());

        for (Collector.MetricFamilySamples.Sample sample : samples.get(0).samples) {
            assertTrue(sample.labelNames.contains(SERVICE_NAME_LABEL));
            assertTrue(sample.labelValues.contains(SERVICE_NAME));
            assertTrue(sample.labelNames.contains(GRPC_SERVICE_LABEL));
            assertTrue(sample.labelValues.contains(GRPC_SERVICE));
            assertTrue(sample.labelNames.contains(GRPC_METHOD_LABEL));
            assertTrue(sample.labelValues.contains(GRPC_METHOD));
            assertTrue(sample.labelNames.contains(GRPC_TYPE_LABEL));
            assertTrue(sample.labelValues.contains(METHOD_TYPE.toString()));

            if (verifyCode) {
                assertTrue(sample.labelNames.contains(STATUS_LABEL));
                assertTrue(sample.labelValues.contains(CODE.toString()));
            }
        }
    }
}
