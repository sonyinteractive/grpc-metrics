package com.sony.cgei.grpc.metrics.listeners;

import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.ClientCall;
import io.grpc.Metadata;
import io.grpc.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class ClientCallListenerTest {

    @Mock
    private ClientCall.Listener delegateMock;

    @Mock
    private CallTracker trackerMock;

    private ClientCallListener clientCallListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        clientCallListener = new ClientCallListener(delegateMock, trackerMock);
    }

    @Test
    public void delegate() {
        assertEquals(delegateMock, clientCallListener.delegate());
    }

    @Test
    public void onMessage() {
        clientCallListener.onMessage(new Object());
        verify(trackerMock).recordMessageReceived();
    }

    @Test
    public void onClose() {
        Status status = Status.fromCode(Status.Code.OK);
        clientCallListener.onClose(status, new Metadata());
        verify(trackerMock).recordCompleted(eq(status.getCode()));
        verify(trackerMock).recordLatency(anyDouble());
    }
}
