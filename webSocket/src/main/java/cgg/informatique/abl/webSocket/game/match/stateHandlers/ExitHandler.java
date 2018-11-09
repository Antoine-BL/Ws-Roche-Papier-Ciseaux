package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchState;

public class ExitHandler extends MatchStateHandler {
    public ExitHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().setMatchState(MatchState.OVER);
    }

    @Override
    public void handleStateChanged() { }
}