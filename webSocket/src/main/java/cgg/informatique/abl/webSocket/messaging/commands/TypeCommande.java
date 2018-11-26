package cgg.informatique.abl.webSocket.messaging.commands;

public enum TypeCommande {
    QUITTER(Quitter.class),
    CREER(Creer.class),
    JOINDRE(Joindre.class),
    ROLE(ChangerRole.class),
    COMBATTRE(Combattre.class),
    ATTAQUER(Attaquer.class),
    SALUER(Saluer.class),
    SIGNALER(Signaler.class),
    POSITION(Position.class),
    COMBAT(CombatHandler.class),
    MATCH_STATE,
    DECONNECTER,
    CONNECTER,
    ERREUR;

    public Class<? extends Commande> getMappedSubtype() {
        return mappedSubtype;
    }

    private Class<? extends Commande> mappedSubtype;

    TypeCommande(){}

    TypeCommande(Class<? extends Commande> mappedSubtype) {
        this.mappedSubtype = mappedSubtype;
    }
}