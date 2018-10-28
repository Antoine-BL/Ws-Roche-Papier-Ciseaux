package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.SanitaryCompte;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.Message;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = CommandDeserializer.class)
public abstract class Commande extends Message {
    protected List<String> parametres;
    protected TypeCommande typeCommande;

    public Commande(){}

    public Commande(SanitaryCompte de, List<String> parametres) {
        super(de);
        this.parametres = parametres;
    }

    public Commande(List<String> parametres) {
        super();
        this.parametres = parametres;
    }

    public List<String> getParametres() {
        return parametres;
    }

    public void setParametres(List<String> parametres) {
        this.parametres = parametres;
    }

    public TypeCommande getTypeCommande() {
        return typeCommande;
    }

    public void setTypeCommande(TypeCommande typeCommande) {
        this.typeCommande = typeCommande;
    }

    public abstract Reponse execute(LobbyCommandContext context);
}
