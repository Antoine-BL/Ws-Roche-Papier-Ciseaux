package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchState;
import cgg.informatique.abl.webSocket.game.match.Signal;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize()
public class Signaler extends Commande{
    private static int SIGNAL = 0;
    private static int CIBLE = 1;

    @Override
    public void execute(LobbyCommandContext context) {
        Lobby lobby = context.getLobby();
        Match match = lobby.getCurrentMatch();

        Signal signalDonne = Signal.valueOf(parametres.get(SIGNAL).toUpperCase());

        switch (signalDonne) {
            case REI:
                match.setMatchState(MatchState.READY);
                break;
            case HAJIME:
                match.setMatchState(MatchState.START);
                break;
            case IPPON:
                LobbyRole cible;
                try {
                    cible = LobbyRole.valueOf(parametres.get(CIBLE).toUpperCase());
                } catch (Exception e) {
                    cible = LobbyRole.ARBITRE;
                }

                if (match.getRouge().getLobbyUser().getRole().getId() == 3) {
                    match.victory(match.getRouge(), match.getBlanc());
                    break;
                } else if (match.getBlanc().getLobbyUser().getRole().getId() == 3) {
                    match.victory(match.getBlanc(), match.getRouge());
                    break;
                }

                if (match.arbitreARaison(cible)) {
                    if (cible == LobbyRole.ROUGE) {
                        match.victory(match.getRouge(), match.getBlanc());
                    } else if (cible == LobbyRole.BLANC){
                        match.victory(match.getBlanc(), match.getRouge());
                    } else if (cible == LobbyRole.ARBITRE){
                        match.tie();
                    } else {
                        throw new IllegalArgumentException("Doit être rouge ou blanc");
                    }
                } else {
                    match.refAtFault();
                }

                break;
            case EGAL:
                if (match.arbitreARaison()) {
                    match.tie();
                } else {
                    match.refAtFault();
                }
                break;
            case RESTER:
                if (match.getMatchState() == MatchState.EXIT) {
                    match.setArbitreReste(true);
                }
                break;
        }
    }
}
