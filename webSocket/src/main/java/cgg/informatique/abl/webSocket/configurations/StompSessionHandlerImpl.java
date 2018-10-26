package cgg.informatique.abl.webSocket.configurations;

import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;

public class StompSessionHandlerImpl implements StompSessionHandler {
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {

    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return null;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        headers.add("simpUser", "Server");
    }
}
