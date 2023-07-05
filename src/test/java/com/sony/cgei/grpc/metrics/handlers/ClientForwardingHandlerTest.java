package com.sony.cgei.grpc.metrics.handlers;

import com.sony.cgei.grpc.metrics.trackers.CallTracker;
import io.grpc.ClientCall;
import io.grpc.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ClientForwardingHandlerTest {

    @Mock
    private ClientCall delegateMock;

    @Mock
    private CallTracker trackerMock;

    private ClientForwardingHandler clientForwardingHandler;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        clientForwardingHandler = new ClientForwardingHandler(delegateMock, trackerMock);
    }

    @Test
    public void start() {
        ClientCall.Listener callListenerMock = mock(ClientCall.Listener.class);
        clientForwardingHandler.start(callListenerMock, new Metadata());
        verify(trackerMock).recordCallStarted();
    }

    @Test
    public void sendMessage() {
        clientForwardingHandler.sendMessage(new Object());
        verify(trackerMock).recordMessageSent();
    }
}
