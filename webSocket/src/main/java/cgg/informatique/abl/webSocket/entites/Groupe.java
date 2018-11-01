package cgg.informatique.abl.webSocket.entites;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "GROUPES")
public class Groupe implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupe;

    public Groupe() {}

    public Groupe(String groupe) {
        this.groupe = groupe;
    }

    @Override
    public String getAuthority() {
        return groupe;
    }
}
