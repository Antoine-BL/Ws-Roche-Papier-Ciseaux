package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.TypeMessage;
import cgg.informatique.abl.webSocket.configurations.UserDetailsImpl;
import cgg.informatique.abl.webSocket.entites.Compte;
import org.springframework.security.core.userdetails.UserDetails;

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

