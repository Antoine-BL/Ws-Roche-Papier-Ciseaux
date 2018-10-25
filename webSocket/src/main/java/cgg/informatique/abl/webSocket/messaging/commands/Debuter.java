package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.messaging.Commande;
import cgg.informatique.abl.webSocket.messaging.Reponse;

import java.util.List;

public class Debuter extends Commande {
    private List<String> params;

    public Debuter(List<String> params) {
        this.params = params;
    }

    @Override
    public Reponse execute() {
        return new Reponse(1L, "Debut du match!");
    }
}
