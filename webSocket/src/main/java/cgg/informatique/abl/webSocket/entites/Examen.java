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
    @JoinColumn(name = "ID_Professeur")
    @JsonBackReference(value="exam-prof")
    private SanitizedCompte professeur;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name = "ID_Eleve")
    @JsonBackReference(value="exam-eleve")
    private SanitizedCompte eleve;

    private boolean reussi;
    private long temps;

    public Examen() {}

    public Examen(Compte professeur, Compte eleve) {
        this.professeur = professeur;
        this.eleve = eleve;
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
