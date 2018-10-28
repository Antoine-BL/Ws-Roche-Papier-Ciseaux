package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;

@JsonDeserialize
public class Error extends Commande {
    private String className;

    public Error() {

    }

    public Error(String className) {
        super(new ArrayList<>());
        this.className = className;
    }

    @Override
    public Reponse execute(LobbyCommandContext context) {
        return new Reponse(1L, "Erreur, commande: " + className + " est inconnue");
    }
}
