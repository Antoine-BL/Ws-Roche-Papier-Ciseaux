package cgg.informatique.abl.webSocket.dto.lobby;

import cgg.informatique.abl.webSocket.dto.SanitaryCompte;

public enum LobbyRole {
    COMBATTANT,
    SPECTATEUR,
    ROUGE,
    BLANC,
    ARBITRE;

    public void changeRole(Lobby lobby, SanitaryCompte user) {
        lobby.devenirRole(lobby.getLobbyUserData(user), new LobbyPosition(this));
    }

    public void changeRole(Lobby lobby, SanitaryCompte user, int position) {
        lobby.devenirRole(lobby.getLobbyUserData(user), new LobbyPosition(this, position));
    }
}
