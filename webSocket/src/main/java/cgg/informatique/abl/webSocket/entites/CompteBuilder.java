package cgg.informatique.abl.webSocket.entites;

import javax.validation.constraints.NotNull;

public class CompteBuilder implements Compte.CourrielBuilder, Compte.MotPasseBuilder {
    private String courriel;
    private String motPasse;
    private String alias;
    private Avatar avatar;
    private Role role;
    private Groupe groupe;

    CompteBuilder() { }

    public Compte.MotPasseBuilder avecCourriel(@NotNull String courriel) {
        this.courriel = courriel;
        return this;
    }

    public CompteBuilder avecMotDePasse(@NotNull String motPasse) {
        this.motPasse = motPasse;
        return this;
    }

    public CompteBuilder avecAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public CompteBuilder avecRole(Role role) {
        this.role = role;
        return this;
    }

    public CompteBuilder avecGroupe(Groupe groupe) {
        this.groupe = groupe;
        return this;
    }

    public CompteBuilder avecAvatar(Avatar avatar) {
        this.avatar = avatar;
        return this;
    }

    public CompteBuilder avecAvatar(String avatar) {
        this.avatar = new Avatar(avatar);
        return this;
    }

    public Compte build() {
        return new Compte(courriel, motPasse, alias, avatar, role, groupe);
    }
}