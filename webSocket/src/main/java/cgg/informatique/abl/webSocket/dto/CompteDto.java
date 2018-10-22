package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.dto.validation.PasswordMatches;
import cgg.informatique.abl.webSocket.dto.validation.ValidEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@PasswordMatches
public class CompteDto {
    @NotNull
    @NotEmpty
    private String alias;

    @NotNull
    @NotEmpty
    @ValidEmail
    private String courriel;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    private String avatar;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCourriel() {
        return courriel;
    }

    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
