package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.match.Attack;
import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.match.Match;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize()
public class Attaquer  extends Commande{
    private static final int ATTACK = 0;

    @Override
    public void execute(LobbyCommandContext context) {
        Lobby lobby = context.getLobby();
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
