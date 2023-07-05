package com.sony.cgei.grpc.metrics.models;

import io.grpc.MethodDescriptor;

public class MethodDefinition {

    private String serviceName;
    private String methodName;
    private String type;

    public static MethodDefinition of(MethodDescriptor<?, ?> method) {
        String serviceName = MethodDescriptor.extractFullServiceName(method.getFullMethodName());

        // Full method names are of the form: "full.serviceName/MethodName". We extract the last part.
        String methodName = method.getFullMethodName().substring(serviceName.length() + 1);
        return new MethodDefinition(serviceName, methodName, method.getType());
    }

    private MethodDefinition(String serviceName, String methodName, MethodDescriptor.MethodType methodType) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.type = methodType.toString();
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getType() {
        return type;
    }
}
