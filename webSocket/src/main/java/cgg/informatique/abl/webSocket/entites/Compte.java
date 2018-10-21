package cgg.informatique.abl.webSocket.entites;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Entity
public class Compte {
    @Id
    private Long id;
    private String courriel;
    private String motPasse;
    private String alias;
    private String avatar;
    @Enumerated
    private Role role = Role.NOUVEAU;
    @Enumerated
    private Groupe groupe = Groupe.BLANC;

    protected Compte() { }

    private Compte(
            final Long id,
            final String courriel,
            final String motPasse,
            final String alias,
            final String avatar,
            final Role role,
            final Groupe groupe
    ) {
        ArrayList<String> errors = new ArrayList<>();

        this.id = id;
        this.courriel = courriel;
        this.motPasse = motPasse;
        this.alias = alias;
        this.avatar = avatar;
        if (role != null)
            this.role = role;
        if (groupe != null)
            this.groupe = groupe;

        Validate();
    }

    private void Validate() {
        if (courriel == null) {
            throw new IllegalArgumentException("Courriel ne peut pas être null");
        }

        if (motPasse == null) {
            throw new IllegalArgumentException("Mot de passe ne peut pas être null");
        }
    }

    public static CourrielBuilder Builder(Long id) {
        return new Builder(id);
    }

    public Long getId() {
        return id;
    }

    public String getCourriel() {
        return courriel;
    }

    public String getMotPasse() {
        return motPasse;
    }

    public String getAlias() {
        return alias;
    }

    public String getAvatar() {
        return avatar;
    }

    public Role getRole() {
        return role;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public static class Builder implements CourrielBuilder, MotPasseBuilder{
        private Long id;
        private String courriel;
        private String motPasse;
        private String alias;
        private String avatar;
        private Role role;
        private Groupe groupe;

        private Builder(Long id) {
            this.id = id;
        }

        public MotPasseBuilder avecCourriel(@NotNull String courriel) {
            this.courriel = courriel;
            return this;
        }

        public Builder avecMotDePasse(@NotNull String motPasse) {
            this.motPasse = motPasse;
            return this;
        }

        public Builder avecAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public Builder avecRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder avecGroupe(Groupe groupe) {
            this.groupe = groupe;
            return this;
        }

        public void avecAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Compte build() {
            return new Compte(id, courriel, motPasse, alias, avatar, role, groupe);
        }
    }

    public interface CourrielBuilder{
        MotPasseBuilder avecCourriel(String courriel);
    }

    public interface MotPasseBuilder {
        Builder avecMotDePasse(String motPasse);
    }
}


