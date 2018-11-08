package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.dto.SanitizedUser;
import cgg.informatique.abl.webSocket.entites.Compte;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Message {
    private Compte de;

    public Message() {
    }

    public Message(Compte de) {
        this.de = de;
    }

    public Compte getDe() {
        return de;
    }

    public void setDe(Compte de) {
        this.de = de;
    }
}

