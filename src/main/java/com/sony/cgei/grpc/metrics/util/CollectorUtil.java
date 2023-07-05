package com.sony.cgei.grpc.metrics.util;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectorUtil {

    private static final String NAMESPACE = "grpc";
    private static final String SERVICE_NAME = "servicename";
    private static final String GRPC_SERVICE = "grpc_service";
    private static final String GRPC_METHOD = "grpc_method";
    private static final String GRPC_TYPE = "grpc_type";

    private CollectorUtil() {}

    public static Counter.Builder buildCounter(String subsystem, String name, String help) {
        return buildCounter(subsystem, name, help, Collections.emptyList());
    }

    public static Counter.Builder buildCounter(String subsystem, String name, String help, List<String> additionalLabels) {
        return Counter.build()
                .namespace(NAMESPACE)
                .subsystem(subsystem)
                .name(name)
                .labelNames(createLabels(additionalLabels))
                .help(help);
    }

    public static Histogram.Builder buildHistogram(String subsystem, String name, double[] buckets, String help) {
        return Histogram.build()
                .namespace(NAMESPACE)
                .subsystem(subsystem)
                .name(name)
                .labelNames(createLabels())
                .buckets(buckets)
                .help(help);
    }

    private static String[] createLabels() {
        return createLabels(Collections.emptyList());
    }

    private static String[] createLabels(List<String> additionalLabels) {
        List<String> allLabels = new ArrayList<>();
        Collections.addAll(allLabels, SERVICE_NAME, GRPC_SERVICE, GRPC_METHOD, GRPC_TYPE);
        allLabels.addAll(additionalLabels);

        return allLabels.toArray(new String[0]);
    }
}
