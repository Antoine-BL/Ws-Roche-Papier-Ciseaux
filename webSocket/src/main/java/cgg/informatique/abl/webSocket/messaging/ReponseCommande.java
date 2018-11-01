package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.messaging.commands.Commande;

import static cgg.informatique.abl.webSocket.messaging.commands.Commande.COMPTE_SERVEUR;

public class ReponseCommande extends Reponse{
    private DonneesReponse donnees;

    public ReponseCommande(Long id, String texte) {
        super(id, texte);
    }

    public ReponseCommande(String texte, DonneesReponse donnees) {
        super(1L, COMPTE_SERVEUR, texte);
        this.donnees = donnees;
    }

    public ReponseCommande(DonneesReponse donnees) {
        this.donnees = donnees;
    }

    public DonneesReponse getDonnees() {
        return donnees;
    }

    public void setDonnees(DonneesReponse donnees) {
        this.donnees = donnees;
    }
}
