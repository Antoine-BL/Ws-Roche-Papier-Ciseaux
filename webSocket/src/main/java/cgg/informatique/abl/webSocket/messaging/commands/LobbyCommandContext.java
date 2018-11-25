package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface LobbyCommandContext {
    static Lobby getLobby() {
        return FightController.getLobby();
    }
    void createLobby();
    SimpMessagingTemplate getMessaging();
}
