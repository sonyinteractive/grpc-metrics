package com.sony.cgei.grpc.metrics.handlers;

import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

public class ServerForwardingHandlerTest {

    @Mock
    private ServerCall delegateMock;

    @Mock
    private CallTracker trackerMock;

    private ServerForwardingHandler serverForwardingHandler;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        serverForwardingHandler = new ServerForwardingHandler(delegateMock, trackerMock);
    }

    @Test
    public void startUp() {
        verify(trackerMock).recordCallStarted();
    }

    @Test
    public void sendMessage() {
        serverForwardingHandler.sendMessage(new Object());
        verify(trackerMock).recordMessageSent();
    }

    @Test
    public void close() {
        Status status = Status.fromCode(Status.Code.OK);
        serverForwardingHandler.close(status, new Metadata());
        verify(trackerMock).recordCompleted(eq(status.getCode()));
        verify(trackerMock).recordLatency(anyDouble());
    }
}
