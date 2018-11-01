package cgg.informatique.abl.webSocket.dto.lobby;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;

public enum LobbyRole {
    COMBATTANT,
    SPECTATEUR,
    ROUGE,
    BLANC,
    ARBITRE;

    public void changeRole(Lobby lobby, SanitizedCompte user) {
        lobby.devenirRole(lobby.getLobbyUserData(user), new LobbyPosition(this));
    }

    public void changeRole(Lobby lobby, SanitizedCompte user, int position) {
        lobby.devenirRole(lobby.getLobbyUserData(user), new LobbyPosition(this, position));
    }
}
