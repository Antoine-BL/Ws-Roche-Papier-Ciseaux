package cgg.informatique.abl.webSocket.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "ROLES")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String role;

    public Role() {}

    public Role(String role) {
        this.role = role;
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        return role;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}
