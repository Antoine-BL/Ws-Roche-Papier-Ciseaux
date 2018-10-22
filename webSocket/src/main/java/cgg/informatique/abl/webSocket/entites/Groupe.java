package cgg.informatique.abl.webSocket.entites;

import org.springframework.security.core.GrantedAuthority;

public enum Groupe implements GrantedAuthority {
    BLANC("Blanc"),
    JAUNE("Jaune"),
    ORANGE("Orange"),
    VERT("Vert"),
    BLEU("Bleu"),
    MARRON("Marro"),
    NOIR("Noir");

    private String authority;

    Groupe(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + authority;
    }
}
