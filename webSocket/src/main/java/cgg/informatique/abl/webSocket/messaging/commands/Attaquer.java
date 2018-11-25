package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.match.Attack;
import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.game.match.Match;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize()
public class Attaquer  extends Commande{
    private static final int ATTACK = 0;

    @Override
    public void execute(LobbyCommandContext context) {
        Lobby lobby = LobbyCommandContext.getLobby();
        Match match = lobby.getCurrentMatch();
        LobbyUserData lud = lobby.getLobbyUserData(getDe());

        try {
            Attack attack = Attack.valueOf(parametres.get(ATTACK).toUpperCase());

            match.getParticipant(lud).setAttack(attack);
        } catch (IllegalArgumentException e) {
            send(e.getMessage(), context);
        }
    }
}
