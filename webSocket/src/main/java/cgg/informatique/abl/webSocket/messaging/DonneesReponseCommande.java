package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;

public class DonneesReponseCommande {
    private Object[] parametres;
    private TypeCommande typeCommande;
    private Object de;

    public DonneesReponseCommande(TypeCommande typeCommande, Object ...parametres) {
        this.parametres = parametres;
        this.typeCommande = typeCommande;
    }

    public DonneesReponseCommande(TypeCommande typeCommande, LobbyUserData de, Object ...parametres) {
        this.parametres = parametres;
        this.typeCommande = typeCommande;
        this.de = de;
    }

    public void setParametres(Object[] parametres) {
        this.parametres = parametres;
    }

    public Object[] getParametres() {
        return parametres;
    }

    public TypeCommande getTypeCommande() {
        return typeCommande;
    }

    public void setTypeCommande(TypeCommande typeCommande) {
        this.typeCommande = typeCommande;
    }

    public Object getDe() {
        return de;
    }

    public void setDe(Object de) {
        this.de = de;
    }
}
