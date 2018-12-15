package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.dto.MatchResult;
import org.springframework.stereotype.Component;

@Component
public class CombatBuilder implements Combat.RougeStep, Combat.BlancStep, Combat.ArbitreStep, Combat.ResultatStep {
    private Compte rouge;
    private Compte blanc;
    private Compte arbitre;
    private int pointsRouge;
    private int pointsBlanc;
    private int creditsArbitre;

    CombatBuilder(){}

    public Combat.BlancStep setRouge(Compte rouge) {
        this.rouge = rouge;
        return this;
    }

    public Combat.ArbitreStep setBlanc(Compte blanc) {
        this.blanc = blanc;
        return this;
    }

    public Combat.ResultatStep setArbitre(Compte arbitre) {
        this.arbitre = arbitre;
        return this;
    }

    @Override
    public CombatBuilder setResultat(MatchResult resultat) {
        this.pointsRouge = resultat.getPointsRouge();
        this.pointsBlanc = resultat.getPointsBlanc();
        this.creditsArbitre = resultat.getCreditsArbitre();

        return this;
    }

    public Combat build() {
        return new Combat(rouge, blanc, arbitre, pointsRouge, pointsBlanc, creditsArbitre, rouge.getGroupe(), blanc.getGroupe());
    }
}