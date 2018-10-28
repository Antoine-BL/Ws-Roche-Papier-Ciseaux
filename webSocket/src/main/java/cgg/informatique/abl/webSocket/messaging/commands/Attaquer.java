package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.Attack;
import cgg.informatique.abl.webSocket.dto.Lobby;
import cgg.informatique.abl.webSocket.dto.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.Match;
import cgg.informatique.abl.webSocket.messaging.Reponse;

public class Attaquer  extends Commande{
    private static final int ATTACK = 0;

    @Override
    public Reponse execute(LobbyCommandContext context) {
        Lobby lobby = context.getLobby();
        Match match = lobby.getCurrentMatch();
        LobbyUserData lud = lobby.getLobbyUserData(getDe());

        Attack attack = Attack.valueOf(parametres.get(ATTACK));

        match.getParticipant(lud).setAttack(attack);

        return new Reponse(1L, lud.getUser().getAlias() + " a choisi une attaque!");
    }
}
