package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.entites.Avatar;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;
import cgg.informatique.abl.webSocket.messaging.Message;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = CommandDeserializer.class)
public abstract class Commande extends Message {
    private static final String OUTPUT_TOPIC = "/topic/battle/command";
    static final Compte COMPTE_SERVEUR = Compte.Builder()
            .avecCourriel("server@server.ca")
            .avecMotDePasse("")
            .avecAlias("CutieBot")
            .avecRole(new Role(""))
            .avecGroupe(new Groupe(""))
            .avecAvatar(new Avatar(2L))
            .build();
    protected List<String> parametres;
    protected TypeCommande typeCommande;

    public Commande(){}

    public Commande(SanitizedCompte de, List<String> parametres) {
        super(de);
        this.parametres = parametres;
    }

    public Commande(List<String> parametres) {
        super();
        this.parametres = parametres;
    }

    protected void send(String reponse, LobbyCommandContext ctxt) {
        Reponse message = new Reponse(1L, COMPTE_SERVEUR.sanitize(), reponse);
        ctxt.getMessaging().convertAndSend(OUTPUT_TOPIC, message);
    }

    protected void sendTo(String reponse, LobbyCommandContext ctxt, Compte user) {
        Reponse message = new Reponse(1L, COMPTE_SERVEUR.sanitize(), reponse);
        ctxt.getMessaging().convertAndSendToUser(user.getUsername(), OUTPUT_TOPIC, reponse);
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

    public abstract void execute(LobbyCommandContext context);
}
