package cgg.informatique.abl.webSocket.dto.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.match.Match;
import cgg.informatique.abl.webSocket.dto.match.MatchState;

public class RougeHandler extends LobbyRoleHandler {
    public RougeHandler(Lobby context) {
        super(context);
    }

    @Override
    public LobbyPosition removeFromRole(LobbyUserData user) {
        getContext().setRouge(null);
        Match match = getContext().getCurrentMatch();
        MatchState state = match == null ? MatchState.OVER : match.getMatchState();

        if (state != MatchState.OVER && state != MatchState.EXIT) {
            match.rougeLeft();
        }

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
