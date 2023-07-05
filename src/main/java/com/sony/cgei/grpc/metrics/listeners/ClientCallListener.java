package com.sony.cgei.grpc.metrics.listeners;

import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.ClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Status;

import java.time.Instant;

public class ClientCallListener<S> extends ForwardingClientCallListener<S> {

    private ClientCall.Listener<S> delegate;
    private CallTracker tracker;
    private long startTime;
    private static final long MILLIS_PER_SECOND = 1000L;

    public ClientCallListener(ClientCall.Listener<S> delegate, CallTracker tracker) {
        this.delegate = delegate;
        this.tracker = tracker;
        this.startTime = Instant.now().toEpochMilli();
    }

    @Override
    protected ClientCall.Listener<S> delegate() {
        return delegate;
    }

    @Override
    public void onClose(Status status, Metadata metadata) {
        double latencySec = (Instant.now().toEpochMilli() - startTime) / (double) MILLIS_PER_SECOND;
        tracker.recordCompleted(status.getCode());
        tracker.recordLatency(latencySec);
        super.onClose(status, metadata);
    }

    @Override
    public void onMessage(S responseMessage) {
        tracker.recordMessageReceived();
        super.onMessage(responseMessage);
    }
}
