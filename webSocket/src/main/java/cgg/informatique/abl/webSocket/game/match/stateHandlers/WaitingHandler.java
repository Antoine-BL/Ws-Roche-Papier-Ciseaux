package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchUserData;

public class WaitingHandler extends MatchStateHandler {
    public WaitingHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().disqualify();
    }

    @Override
    public void handleStateChanged() {

    }
}
