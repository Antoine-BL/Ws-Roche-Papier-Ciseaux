package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.commands.Role;

public enum LobbyRole {
    COMBATTANT,
    SPECTATEUR,
    ROUGE,
    BLANC,
    ARBITRE;

    public void changeRole(Lobby lobby, SanitaryCompte user) {
        lobby.devenirRole(lobby.getLobbyUserData(user), this);
    }
}
