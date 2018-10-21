package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Compte;

import java.util.Date;

public class Reponse {
    private Long id;
    private Compte de;
    private String texte;
    private Long creation;

    public void setCreation(Long creation) {
        this.creation = creation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reponse() {
    }

    public Reponse(Long id, Compte de, String texte) {
        this.id = id;
        this.de = de;
        this.texte = texte;

        this.creation = System.currentTimeMillis();
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }


    public Long getCreation() {
        return creation;
    }

    public void setTime(Long creation) {
        this.creation = creation;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "de='" + de + '\'' +
                ", texte='" + texte + '\'' +
                ", creation=" + creation +
                ", avatar=" + de.getAvatar() +
                '}';
    }
}


