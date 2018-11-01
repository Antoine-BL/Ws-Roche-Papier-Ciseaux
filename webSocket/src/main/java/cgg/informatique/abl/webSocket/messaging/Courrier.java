package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;

public class Courrier extends Message {
    private String texte;

    public Courrier(SanitizedCompte de, String texte) {
        super(de);
        this.texte = texte;
    }

    public Courrier(String texte) {
        super();
        this.texte = texte;
    }

    public Courrier() {}

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }
}
