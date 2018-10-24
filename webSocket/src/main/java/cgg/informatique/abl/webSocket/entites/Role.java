package cgg.informatique.abl.webSocket.entites;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    NOUVEAU("Nouveau"),
    ANCIEN("Ancien"),
    SENSEI("Sensei"),
    VENERABLE("Venerable");

    private String authority;

    Role(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + authority;
    }
}
