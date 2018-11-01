package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import cgg.informatique.abl.webSocket.messaging.Reponse;

public class ReponseCommande extends Reponse {
    private DonneesReponseCommande donnees;

    public ReponseCommande(Long id, String texte) {
        super(id, texte);
    }

    public ReponseCommande(Compte de, String texte, DonneesReponseCommande donnees) {
        super(1L, de, texte);
        this.donnees = donnees;
    }

    public ReponseCommande(DonneesReponseCommande donnees) {
        this.donnees = donnees;
    }

    public DonneesReponseCommande getDonnees() {
        return donnees;
    }

    public void setDonnees(DonneesReponseCommande donnees) {
        this.donnees = donnees;
    }
}
