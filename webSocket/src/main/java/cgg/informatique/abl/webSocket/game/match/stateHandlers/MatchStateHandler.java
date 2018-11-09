package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.match.Match;

public abstract class MatchStateHandler {
    private Match context;

    public MatchStateHandler(Match context) {
        this.context = context;
    }

    public abstract void handleTimeout();
    public abstract void handleStateChanged();

    public Match getContext() {
        return context;
    }

    public void setContext(Match context) {
        this.context = context;
    }
}
