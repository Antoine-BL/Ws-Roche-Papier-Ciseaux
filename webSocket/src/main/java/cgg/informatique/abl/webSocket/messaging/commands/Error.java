package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.messaging.Commande;
import cgg.informatique.abl.webSocket.messaging.Reponse;

import java.util.ArrayList;

public class Error extends Commande {
    private String className;

    public Error(String className) {
        super(new ArrayList<>());
        this.className = className;
    }

    @Override
    public Reponse execute() {
        return new Reponse(1L, "Erreur, commande: " + className + " est inconnue");
    }
}
