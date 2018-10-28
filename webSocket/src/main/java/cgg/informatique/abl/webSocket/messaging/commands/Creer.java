package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize()
public class Creer extends Commande{
    public Creer() {

    }

    public Creer(List<String> args) {
        super(args);
    }

    @Override
    public Reponse execute(LobbyCommandContext context) {
        Thread thread = new Thread(context.getLobby());
        thread.start();

        return new Reponse(1L, "Lobby en création...");
    }
}