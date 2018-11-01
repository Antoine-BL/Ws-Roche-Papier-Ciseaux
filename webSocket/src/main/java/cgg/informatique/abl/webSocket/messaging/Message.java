package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;

public abstract class Message {

    private SanitizedCompte de;

    public Message() {
    }

    public Message(SanitizedCompte de) {
        this.de = de;
    }

    public SanitizedCompte getDe() {
        return de;
    }

    public void setDe(SanitizedCompte de) {
        this.de = de;
    }
}

