package com.sony.cgei.grpc.metrics.handlers;

import com.sony.cgei.grpc.metrics.listeners.ClientCallListener;
import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;

public class ClientForwardingHandler<R, S> extends ForwardingClientCall.SimpleForwardingClientCall<R, S> {

    private CallTracker tracker;

    public ClientForwardingHandler(ClientCall<R, S> delegate, CallTracker tracker) {
        super(delegate);
        this.tracker = tracker;
    }

    @Override
    public void start(ClientCall.Listener<S> delegate, Metadata metadata) {
        tracker.recordCallStarted();
        super.start(new ClientCallListener<>(delegate, tracker), metadata);
    }

    @Override
    public void sendMessage(R requestMessage) {
        tracker.recordMessageSent();
        super.sendMessage(requestMessage);
    }
}
