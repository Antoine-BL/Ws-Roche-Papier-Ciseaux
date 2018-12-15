package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.dto.MatchResult;
import javax.persistence.*;

@Entity
@Table(name = "COMBATS")
public class Combat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name="ROUGE_ID")
    private Compte rouge;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name="BLANC_ID")
    private Compte blanc;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name="ARBITRE_ID")
    private Compte arbitre;

    @Column(name = "POINTS_ROUGE")
    private int pointsRouge;
    @Column(name = "POINTS_BLANC")
    private int pointsBlanc;
    @Column(name = "CREDITS_ARBITRE")
    private int creditsArbitre;
    @Column(name = "DATE")
    private long temps;

    @ManyToOne(targetEntity = Groupe.class)
    @JoinColumn(name = "CEINTURE_ROUGE_ID")
    private Groupe ceintureRouge;

    @ManyToOne(targetEntity = Groupe.class)
    @JoinColumn(name = "CEINTURE_BLANC_ID")
    private Groupe ceintureBlanc;

    public Combat() {}

    public Combat(Compte rouge, Compte blanc, Compte arbitre, int pointsRouges, int pointsBlancs, int creditsArbitres, Groupe ceintureBlanc, Groupe ceintureRouge) {
        this.rouge = rouge;
        this.blanc = blanc;
        this.arbitre = arbitre;
        this.pointsRouge = pointsRouges;
        this.pointsBlanc = pointsBlancs;
        this.creditsArbitre = creditsArbitres;
        this.ceintureBlanc = ceintureBlanc;
        this.ceintureRouge = ceintureRouge;
        this.temps = System.currentTimeMillis();
    }

    public static RougeStep Builder() {
        return new CombatBuilder();
    }

    public interface RougeStep { BlancStep setRouge(Compte rouge); }
    public interface BlancStep { ArbitreStep setBlanc(Compte blanc); }
    public interface ArbitreStep { ResultatStep setArbitre(Compte arbitre); }
    public interface ResultatStep { CombatBuilder setResultat(MatchResult resultat); }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compte getRouge() {
        return rouge;
    }

    public void setRouge(Compte rouge) {
        this.rouge = rouge;
    }

    public Compte getBlanc() {
        return blanc;
    }

    public void setBlanc(Compte blanc) {
        this.blanc = blanc;
    }

    public Compte getArbitre() {
        return arbitre;
    }

    public void setArbitre(Compte arbitre) {
        this.arbitre = arbitre;
    }

    public Groupe getCeintureRouge() {
        return ceintureRouge;
    }

    public void setCeintureRouge(Groupe ceintureRouge) {
        this.ceintureRouge = ceintureRouge;
    }

    public Groupe getCeintureBlanc() {
        return ceintureBlanc;
    }

    public void setCeintureBlanc(Groupe ceintureBlanc) {
        this.ceintureBlanc = ceintureBlanc;
    }

    public int getPointsRouge() {
        return pointsRouge;
    }

    public void setPointsRouge(int pointsRouge) {
        this.pointsRouge = pointsRouge;
    }

    public int getPointsBlanc() {
        return pointsBlanc;
    }

    public void setPointsBlanc(int pointsBlanc) {
        this.pointsBlanc = pointsBlanc;
    }

    public int getCreditsArbitre() {
        return creditsArbitre;
    }

    public void setCreditsArbitre(int creditsArbitre) {
        this.creditsArbitre = creditsArbitre;
    }

    public long getTemps() {
        return temps;
    }

    public void setTemps(long temps) {
        this.temps = temps;
    }

    public interface CeintureStep {
        ResultatStep setCeintures(Groupe rouge, Groupe blanc);
    }
}
