package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.match.Match;

public class PauseHandler extends MatchStateHandler {
    public PauseHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().viderTatami();
        getContext().enregistrerCombat();
        getContext().endMatch();
    }
}
