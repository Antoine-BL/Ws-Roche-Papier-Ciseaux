package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.match.Match;

public class DecideHandler extends MatchStateHandler {
    public DecideHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().refAtFault();
    }

    @Override
    public void handleStateChanged() { }
}
