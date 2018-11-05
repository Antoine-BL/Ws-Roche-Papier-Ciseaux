package cgg.informatique.abl.webSocket.dto.match.stateHandlers;

import cgg.informatique.abl.webSocket.dto.match.Match;

public class ReadyHandler extends MatchStateHandler {
    public ReadyHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().determineFault();
    }
}
