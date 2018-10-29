package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.Lobby;

public interface LobbyCommandContext {
    Lobby getLobby();
    void createLobby();
}
