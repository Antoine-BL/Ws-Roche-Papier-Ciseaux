package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.dto.SanitaryCompte;

public abstract class Message {

    private SanitaryCompte de;

    public Message() {
    }

    public Message(SanitaryCompte de) {
        this.de = de;
    }

    public SanitaryCompte getDe() {
        return de;
    }

    public void setDe(SanitaryCompte de) {
        this.de = de;
    }
}

