package cgg.informatique.abl.webSocket.dto.lobby;

public class LobbyPosition {
    private Integer position;
    private LobbyRole role;

    public LobbyPosition(LobbyRole role) {
        this.role = role;
    }

    public LobbyPosition(LobbyRole role, int position) {
        this.position = position;
        this.role = role;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public LobbyRole getRole() {
        return role;
    }
}
