package com.sony.cgei.grpc.metrics.handlers;

import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;

import java.time.Instant;

public class ServerForwardingHandler<R, S> extends ForwardingServerCall.SimpleForwardingServerCall<R, S> {

    private CallTracker tracker;
    private long startTime;
    private static final long MILLIS_PER_SECOND = 1000L;

    public ServerForwardingHandler(ServerCall<R, S> delegate, CallTracker tracker) {
        super(delegate);
        this.tracker = tracker;
        this.startTime = Instant.now().toEpochMilli();
        tracker.recordCallStarted();
    }

    @Override
    public void sendMessage(S message) {
        tracker.recordMessageSent();
        super.sendMessage(message);
    }

    @Override
    public void close(Status status, Metadata responseHeaders) {
        double latency = (Instant.now().toEpochMilli() - startTime) / (double) MILLIS_PER_SECOND;
        tracker.recordCompleted(status.getCode());
        tracker.recordLatency(latency);
        super.close(status, responseHeaders);
    }
}
