package cgg.informatique.abl.webSocket.game.lobby;

import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.entites.Compte;

public interface LobbyContext {
    void lobbyClosed();
    void persistMatch(Combat combat);
    Compte getUser(String username);
}
