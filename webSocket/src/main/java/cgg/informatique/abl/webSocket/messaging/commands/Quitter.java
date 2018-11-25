package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public class Quitter extends Commande{
    public Quitter() { }

    public Quitter(List<String> parametres) {
        super(parametres);
        typeCommande = TypeCommande.QUITTER;
    }

    @Override
    public void execute(LobbyCommandContext context) {
        try {
            Lobby lobby = LobbyCommandContext.getLobby();

            LobbyUserData lub = lobby.getLobbyUserData(getDe());
            lobby.quitter(lub);

            send("Lobby quitté avec succès", context);
        } catch (Exception e) {
            send("Erreur en quittant le lobby", context);
        }
    }
}
