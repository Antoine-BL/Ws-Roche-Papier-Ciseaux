package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.TypeMessage;
import cgg.informatique.abl.webSocket.entites.Compte;

public class Commande extends Message {
    private static final TypeMessage COMMANDE = TypeMessage.COMMANDE;

    public Commande(Compte de, String texte) {
        super(de, texte, COMMANDE);
    }

    public Commande(String texte) {
        super(texte, COMMANDE);
    }
}
