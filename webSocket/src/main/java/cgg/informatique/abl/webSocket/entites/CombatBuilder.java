package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.dao.CombatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CombatBuilder implements Combat.RougeStep, Combat.BlancStep, Combat.ArbitreStep, Combat.ResultatStep, Combat.CeintureStep {
    private Compte rouge;
    private Compte blanc;
    private Compte arbitre;
    private int pointsRouge;
    private int pointsBlanc;
    private int creditsArbitre;
    private Groupe ceintureRouge;
    private Groupe ceintureBlanc;

    CombatBuilder(){}

    public Combat.BlancStep setRouge(Compte rouge) {
        this.rouge = rouge;
        return this;
    }

    public Combat.ArbitreStep setBlanc(Compte blanc) {
        this.blanc = blanc;
        return this;
    }

    public Combat.CeintureStep setArbitre(Compte arbitre) {
        this.arbitre = arbitre;
        return this;
    }

    @Override
    public CombatBuilder setResultat(int pointsRouge, int pointsBlanc, int creditArbitre) {
        this.pointsRouge = pointsRouge;
        this.pointsBlanc = pointsBlanc;
        this.creditsArbitre = creditArbitre;

        return this;
    }

    public Combat.ResultatStep setCeintures(Groupe ceintureBlanc, Groupe ceintureRouge) {
        this.ceintureBlanc = ceintureBlanc;
        this.ceintureRouge = ceintureRouge;

        return this;
    }

    public Combat build() {
        return new Combat(rouge, blanc, arbitre, pointsRouge, pointsBlanc, creditsArbitre, ceintureRouge, ceintureBlanc);
    }
}