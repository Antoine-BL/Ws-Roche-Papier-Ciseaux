package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.TypeMessage;
import cgg.informatique.abl.webSocket.entites.Compte;

public abstract class Message {

    private Compte de;

    public String getAvatar() {
        return de.getAvatar();
    }

    public Message() {
    }

    public Message(Compte de) {
        this.de = de;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "de='" + (de == null ? "anonyme" : de.getAlias()) + '\'' +
                (de == null ? "" : ", avatar=" + de.getAvatar()) +
                '}';
    }

    public Compte getDe() {
        return de;
    }

    public void setDe(Compte de) {
        this.de = de;
    }
}

