package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.configurations.UserDetailsImpl;
import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.dto.Lobby;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public class Joindre extends Commande{
    public Joindre() {

    }

    public Joindre(Compte de, List<String> parametres) {
        super(de, parametres);
    }

    @Override
    public Reponse execute(FightController context) {
        try {
            Lobby lobby = context.getLobby();
            lobby.connect(this.getDe());

            return new Reponse(1L, "Vous avez rejoint le lobby!");
        } catch (Exception e) {
            return new Reponse(1L, "Échec de la connexion au lobby...");
        }
    }
}
