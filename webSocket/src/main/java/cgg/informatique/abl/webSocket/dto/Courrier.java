package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.TypeMessage;
import cgg.informatique.abl.webSocket.entites.Compte;

public class Courrier extends Message {
    private static final TypeMessage COURRIER = TypeMessage.COURRIER;

    public Courrier(Compte de, String texte) {
        super(de, texte, COURRIER);
    }

    public Courrier(String texte) {
        super(texte, COURRIER);
    }
}
