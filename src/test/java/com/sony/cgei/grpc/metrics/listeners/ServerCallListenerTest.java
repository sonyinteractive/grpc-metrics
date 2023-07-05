package com.sony.cgei.grpc.metrics.listeners;

import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.ServerCall;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class ServerCallListenerTest {

    @Mock
    private CallTracker trackerMock;

    @Mock
    private ServerCall.Listener delegateMock;

    private ServerCallListener listener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        listener = new ServerCallListener(delegateMock, trackerMock);
    }

    @Test
    public void delegate() {
        assertEquals(delegateMock, listener.delegate());
    }

    @Test
    public void onMessage() {
        listener.onMessage(new Object());
        verify(trackerMock).recordMessageReceived();
    }
}
