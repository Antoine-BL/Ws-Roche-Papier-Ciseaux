package cgg.informatique.abl.webSocket.game.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchState;

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
        getContext().setRouge(user);
        return new LobbyPosition(LobbyRole.ROUGE);
    }

    @Override
    public void checkAvailable(LobbyUserData user, Integer Position) {
        Match currentMatch = getContext().getCurrentMatch();

        if (currentMatch == null)
            throw new IllegalStateException("Aucun match en cours");

        if (!user.equals(currentMatch.getRouge()))
            throw new IllegalArgumentException("Ce n'est pas à votre tour de combattre");
    }
}
