package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.match.MatchState;
import cgg.informatique.abl.webSocket.dto.match.MatchUserData;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;

public class DonneesReponseCommande {
    private Object[] parametres;
    private String typeCommande;
    private SanitizedCompte de;

    public DonneesReponseCommande(TypeCommande typeCommande) {
        this.parametres = parametres;
        this.typeCommande = typeCommande.toString();
    }

    public DonneesReponseCommande(TypeCommande typeCommande, LobbyUserData de, Object ...parametres) {
        this.parametres = parametres;
        this.typeCommande = typeCommande.toString();
        this.de = de;
    }

    public DonneesReponseCommande(TypeCommande typeCommande, MatchUserData de, Object ...parametres) {
        this.parametres = parametres;
        this.typeCommande = typeCommande.toString();
        this.de = de.getUser();
    }

    public DonneesReponseCommande(TypeCommande typeCommande, Object ...parametres) {
        this.parametres = parametres;
        this.typeCommande = typeCommande.toString();
    }

    public DonneesReponseCommande(MatchState typeCommande) {
        this.parametres = parametres;
        this.typeCommande = typeCommande.toString();
        this.de = de;
    }

    public void setParametres(Object[] parametres) {
        this.parametres = parametres;
    }

    public Object[] getParametres() {
        return parametres;
    }

    public String getTypeCommande() {
        return typeCommande;
    }

    public void String(String typeCommande) {
        this.typeCommande = typeCommande;
    }

    public SanitizedCompte getDe() {
        return de;
    }

    public void setDe(SanitizedCompte de) {
        this.de = de;
    }
}
