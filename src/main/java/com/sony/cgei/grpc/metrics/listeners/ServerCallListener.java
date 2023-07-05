package com.sony.cgei.grpc.metrics.listeners;

import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.ForwardingServerCallListener;
import io.grpc.ServerCall;

public class ServerCallListener<R> extends ForwardingServerCallListener<R> {

    private ServerCall.Listener<R> delegate;
    private CallTracker tracker;

    public ServerCallListener(ServerCall.Listener<R> delegate, CallTracker tracker) {
        this.delegate = delegate;
        this.tracker = tracker;
    }

    @Override
    protected ServerCall.Listener<R> delegate() {
        return delegate;
    }

    @Override
    public void onMessage(R request) {
        tracker.recordMessageReceived();
        super.onMessage(request);
    }

}
