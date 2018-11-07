package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.dto.SanitizedUser;

public class Reponse {
    private Long id;
    private SanitizedUser de;
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

    public Reponse(Long id, String texte) {
        this.id = id;
        this.texte = texte;

        this.creation = System.currentTimeMillis();
    }

    public Reponse(Long id, SanitizedUser de, String texte) {
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

    public SanitizedUser getDe() {
        return de;
    }

    public void setDe(SanitizedUser de) {
        this.de = de;
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
                ", avatar=" + de.getAvatarId() +
                '}';
    }
}


