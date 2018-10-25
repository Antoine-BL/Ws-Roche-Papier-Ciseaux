package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.dto.Match;
import cgg.informatique.abl.webSocket.entites.Compte;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = CommandDeserializer.class)
public abstract class Commande extends Message {
    private List<String> parametres;
    private TypeCommande typeCommande;
    private Match match;

    public Commande(){}

    public Commande(Compte de, List<String> parametres) {
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

    public abstract Reponse execute();
}
