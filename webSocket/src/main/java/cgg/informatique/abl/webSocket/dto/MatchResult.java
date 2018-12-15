package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Groupe;

public class MatchResult {
    private int pointsBlanc;
    private int pointsRouge;
    private int creditsArbitre;

    public MatchResult() {
    }

    public int getPointsBlanc() {
        return pointsBlanc;
    }

    public int getPointsRouge() {
        return pointsRouge;
    }

    public int getCreditsArbitre() {
        return creditsArbitre;
    }

    public void setPointsBlanc(int pointsBlanc) {
        this.pointsBlanc = pointsBlanc;
    }

    public void setPointsRouge(int pointsRouge) {
        this.pointsRouge = pointsRouge;
    }

    public void setCreditsArbitre(int creditsArbitre) {
        this.creditsArbitre = creditsArbitre;
    }
}
