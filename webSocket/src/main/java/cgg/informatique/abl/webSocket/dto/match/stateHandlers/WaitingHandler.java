package cgg.informatique.abl.webSocket.dto.match.stateHandlers;

import cgg.informatique.abl.webSocket.dto.match.Match;

public class WaitingHandler extends MatchStateHandler {
    public WaitingHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().disqualify();
    }
}
