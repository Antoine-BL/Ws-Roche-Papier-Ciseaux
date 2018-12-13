package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.dto.SanitizedUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;

@Entity
@Table(name="EXAMENS")
public class Examen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name = "EVALUATEUR_ID")
    private SanitizedCompte professeur;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name = "EVALUE_ID")
    private SanitizedCompte eleve;

    @Column(name = "A_REUSSI")
    private boolean reussi;
    @Column(name = "DATE")
    private long temps;

    @ManyToOne(targetEntity = Groupe.class)
    @JoinColumn(name="CEINTURE_ID")
    private Groupe ceinture;

    public Examen() {}

    public Examen(Compte professeur, Compte eleve) {
        this.professeur = professeur;
        this.eleve = eleve;
    }

    public Groupe getCeinture() {
        return ceinture;
    }

    public void setCeinture(Groupe ceinture) {
        this.ceinture = ceinture;
    }

    public SanitizedUser getProfesseur() {
        return professeur;
    }

    public void setProfesseur(Compte professeur) {
        this.professeur = professeur;
    }

    public SanitizedUser getEleve() {
        return eleve;
    }

    public void setEleve(Compte eleve) {
        this.eleve = eleve;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isReussi() {
        return reussi;
    }

    public void setReussi(boolean reussi) {
        this.reussi = reussi;
    }

    public long getTemps() {
        return temps;
    }

    public void setTemps(long temps) {
        this.temps = temps;
    }
}
