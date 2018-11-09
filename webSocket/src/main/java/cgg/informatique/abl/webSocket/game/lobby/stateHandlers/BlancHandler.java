package cgg.informatique.abl.webSocket.game.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchState;

public class BlancHandler extends LobbyRoleHandler {
    public BlancHandler(Lobby context) {
        super(context);
    }

    @Override
    public LobbyPosition removeFromRole(LobbyUserData user) {
        getContext().setBlanc(null);
        Match match = getContext().getCurrentMatch();
        MatchState state = match == null ? MatchState.OVER : match.getMatchState();

        if (state != MatchState.OVER && !(state == MatchState.EXIT && match.getBlanc().isSaluting())) {
            match.blancLeft();
        }

        return new LobbyPosition(LobbyRole.BLANC);
    }

    @Override
    public LobbyPosition addToRole(LobbyUserData user, Integer position) {
        getContext().setBlanc(user);
        return new LobbyPosition(LobbyRole.BLANC);
    }

    @Override
    public void checkAvailable(LobbyUserData user, Integer Position) {
        Match currentMatch = getContext().getCurrentMatch();

        if (currentMatch == null)
            throw new IllegalStateException("Aucun match en cours");

        if (!user.equals(currentMatch.getBlanc()))
            throw new IllegalArgumentException("Ce n'est pas à votre tour de combattre");
    }
}
