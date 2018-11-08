package cgg.informatique.abl.webSocket.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "GROUPES")
public class Groupe implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String groupe;

    public Groupe() {}

    public Groupe(String groupe) {
        this.groupe = groupe;
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        return groupe;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupe() {
        return groupe;
    }

    public void setGroupe(String groupe) {
        this.groupe = groupe;
    }
}
