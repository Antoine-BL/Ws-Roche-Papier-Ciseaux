package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.match.Match;

public class OverHandler extends MatchStateHandler {
    public OverHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().matchEnded();
    }

    @Override
    public void handleStateChanged() { }
}
