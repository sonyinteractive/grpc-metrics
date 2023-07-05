package com.sony.cgei.grpc.metrics.interceptors;

import com.sony.cgei.grpc.metrics.configurations.GrpcMetricsConfiguration;
import com.sony.cgei.grpc.metrics.handlers.ClientForwardingHandler;
import com.sony.cgei.grpc.metrics.models.ClientMetrics;
import com.sony.cgei.grpc.metrics.models.MethodDefinition;
import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.*;

public class ClientMonitoringInterceptor implements ClientInterceptor {

    private String serviceName;
    private ClientMetrics clientMetrics;

    /**
     * Interceptor for client side gRPC
     * @param serviceName name of the service that started the gRPC channel
     * @param configuration gRPC metrics configuration
     */
    public ClientMonitoringInterceptor(String serviceName, GrpcMetricsConfiguration configuration) {
        this.serviceName = serviceName;
        this.clientMetrics = new ClientMetrics(configuration);
    }

    /**
     *
     * @param methodDescriptor method definition
     * @param callOptions channel call options
     * @param channel channel that is controlling message traffic
     * @param <R>
     * @param <S>
     * @return client call after interceptor interference
     */
    @Override
    public <R, S> ClientCall<R, S> interceptCall(
            MethodDescriptor<R, S> methodDescriptor, CallOptions callOptions, Channel channel) {
        MethodDefinition method = MethodDefinition.of(methodDescriptor);
        CallTracker tracker = new CallTracker(clientMetrics, serviceName, method);

        return new ClientForwardingHandler<>(channel.newCall(methodDescriptor, callOptions), tracker);
    }
}
