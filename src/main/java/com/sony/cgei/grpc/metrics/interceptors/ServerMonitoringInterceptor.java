package com.sony.cgei.grpc.metrics.interceptors;

import com.sony.cgei.grpc.metrics.handlers.ServerForwardingHandler;
import com.sony.cgei.grpc.metrics.configurations.GrpcMetricsConfiguration;
import com.sony.cgei.grpc.metrics.listeners.ServerCallListener;
import com.sony.cgei.grpc.metrics.models.MethodDefinition;
import com.sony.cgei.grpc.metrics.models.ServerMetrics;
import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.*;

public class ServerMonitoringInterceptor implements ServerInterceptor {

    private String serviceName;
    private ServerMetrics serverMetrics;

    /**
     * Interceptor for server side gRPC
     * @param serviceName name of the service that started the gRPC server
     * @param configuration gRPC metrics configuration
     */
    public ServerMonitoringInterceptor(String serviceName, GrpcMetricsConfiguration configuration) {
        this.serviceName = serviceName;
        this.serverMetrics = new ServerMetrics(configuration);
    }

    /**
     * Call interceptor for server side gRPC
     * @param call Server call
     * @param metadata Call metadata
     * @param callHandler Handler for gRPC server side call
     * @param <R>
     * @param <S>
     * @return listener for metric handling
     */
    @Override
    public <R, S> ServerCall.Listener<R> interceptCall(ServerCall<R, S> call, Metadata metadata, ServerCallHandler<R, S> callHandler) {
        MethodDescriptor<R, S> methodDescriptor = call.getMethodDescriptor();
        MethodDefinition methodDefinition = MethodDefinition.of(methodDescriptor);
        CallTracker tracker = new CallTracker(serverMetrics, serviceName, methodDefinition);
        ServerCall<R, S> monitoringCall = new ServerForwardingHandler<>(call, tracker);

        return new ServerCallListener<>(callHandler.startCall(monitoringCall, metadata), tracker);
    }
}
