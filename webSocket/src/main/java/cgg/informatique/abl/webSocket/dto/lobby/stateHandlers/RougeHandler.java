package cgg.informatique.abl.webSocket.dto.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.match.Match;

public class RougeHandler extends LobbyRoleHandler {
    public RougeHandler(Lobby context) {
        super(context);
    }

    @Override
    public LobbyPosition removeFromRole(LobbyUserData user) {
        getContext().setRouge(null);
        return new LobbyPosition(LobbyRole.ROUGE);
    }

    @Override
    public LobbyPosition addToRole(LobbyUserData user, Integer position) {
        return null;
    }

    @Override
    public void checkAvailable(LobbyUserData user, Integer Position) {
        Match currentMatch = getContext().getCurrentMatch();

        if (currentMatch == null)
            throw new IllegalStateException("Aucun match en cours");

        if (!user.getCourriel().equals(currentMatch.getRouge().getCourriel()))
            throw new IllegalArgumentException("Ce n'est pas à votre tour de combattre");
    }
}
