package cgg.informatique.abl.webSocket.messaging;

public class Heartbeat extends Message {
    private boolean heartbeat = true;

    public boolean isHeartbeat() {
        return heartbeat;
    }
}
