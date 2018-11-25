package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize()
public class Creer extends Commande{
    public Creer() {}

    public Creer(List<String> args) {
        super(args);
    }

    @Override
    public void execute(LobbyCommandContext context) {
        try {
            context.createLobby();
            Thread thread = new Thread(LobbyCommandContext.getLobby());
            thread.start();
            send("Lobby créé", context);
        } catch (IllegalStateException e) {
            send(e.getMessage(), context);
        }
    }
}