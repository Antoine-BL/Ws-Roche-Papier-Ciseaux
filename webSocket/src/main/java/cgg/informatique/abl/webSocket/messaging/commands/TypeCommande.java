package cgg.informatique.abl.webSocket.messaging.commands;

public enum TypeCommande {
    QUITTER(Quitter.class),
    LOBBYMESSAGE(LobbyMessage.class),
    CREER(Creer.class),
    JOINDRE(Joindre.class),
    ROLE(Role.class);

    public Class<? extends Commande> getMappedSubtype() {
        return mappedSubtype;
    }

    private Class<? extends Commande> mappedSubtype;

    TypeCommande(Class<? extends Commande> mappedSubtype) {
        this.mappedSubtype = mappedSubtype;
    }
}