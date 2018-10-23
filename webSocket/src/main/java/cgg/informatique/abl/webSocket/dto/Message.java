package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.TypeMessage;
import cgg.informatique.abl.webSocket.entites.Compte;

public class Message {

    private Compte de;
    private String texte;
    private TypeMessage type;
    private Long   creation;

    public String getAvatar() {
        return de.getAvatar();
    }

    public Message() {
    }

    public Message(Compte de, String texte, TypeMessage type) {
        this.de = de;
        this.texte = texte;
        this.type = type;
        this.creation = System.currentTimeMillis();
    }

    public Message( String texte, TypeMessage type) {
        this.texte = texte;
        this.type = type;
    }

    public Long getCreation() {
        return creation;
    }

    public void setCreation(Long creation) {
        this.creation = creation;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "de='" + (de == null ? "anonyme" : de.getAlias()) + '\'' +
                ", texte='" + texte + '\'' +
                ", creation=" + creation +
                (de == null ? "" : ", avatar=" + de.getAvatar()) +
                '}';
    }

    public Compte getDe() {
        return de;
    }

    public void setDe(Compte de) {
        this.de = de;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String text) {
        this.texte = text;
    }
}

