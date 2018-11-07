package cgg.informatique.abl.webSocket.dto.lobby;

import cgg.informatique.abl.webSocket.entites.Combat;

public interface LobbyContext {
    void lobbyClosed();
    void persistMatch(Combat combat);
}
