package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface LobbyCommandContext {
    Lobby getLobby();
    void createLobby();
    SimpMessagingTemplate getMessaging();
}
