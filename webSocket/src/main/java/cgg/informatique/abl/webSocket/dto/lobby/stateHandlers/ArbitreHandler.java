package cgg.informatique.abl.webSocket.dto.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.match.Match;
import cgg.informatique.abl.webSocket.dto.match.MatchState;

public class ArbitreHandler extends LobbyRoleHandler {
    public ArbitreHandler(Lobby context) {
        super(context);
    }

    @Override
    public LobbyPosition removeFromRole(LobbyUserData user) {
        getContext().setArbitre(null);
        Match match = getContext().getCurrentMatch();
        MatchState state = match == null ? MatchState.OVER : match.getMatchState();

        if (state != MatchState.OVER && state != MatchState.EXIT) {
            match.refAtFault();
        }

        return new LobbyPosition(LobbyRole.ARBITRE);
    }

    @Override
    public LobbyPosition addToRole(LobbyUserData user, Integer position) {
        getContext().setArbitre(user);
        return new LobbyPosition(LobbyRole.ARBITRE);
    }

    @Override
    public void checkAvailable(LobbyUserData user, Integer Position) {
        if (getContext().getArbitre() != null)
            throw new IllegalArgumentException("Il y a déjà un arbitre");
    }
}
