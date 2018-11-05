package cgg.informatique.abl.webSocket.dto.match.stateHandlers;

import cgg.informatique.abl.webSocket.dto.match.Match;

public class DecideHandler extends MatchStateHandler {
    public DecideHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().refAtFault();
    }
}
