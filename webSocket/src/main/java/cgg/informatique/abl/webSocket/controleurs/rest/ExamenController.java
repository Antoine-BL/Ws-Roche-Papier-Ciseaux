package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.dao.ExamenDao;
import cgg.informatique.abl.webSocket.dao.GroupeDao;
import cgg.informatique.abl.webSocket.dao.RoleDao;
import cgg.informatique.abl.webSocket.dto.ExamDto;
import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Examen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/examens")
public class ExamenController {
    private ExamenDao examenDao;
    private RoleDao roleDao;
    private GroupeDao groupeDao;
    private CompteDao compteDao;

    public ExamenController(@Autowired ExamenDao examenDao,
                            @Autowired RoleDao roleDao,
                            @Autowired GroupeDao groupeDao,
                            @Autowired CompteDao compteDao) {
        this.examenDao = examenDao;
        this.roleDao = roleDao;
        this.groupeDao = groupeDao;
        this.compteDao = compteDao;
    }

    @GetMapping(value = "")
    public List<Examen> tousLesExamems() { return examenDao.findAll();}

    @GetMapping(value = "/{id}")
    public ResponseEntity<Examen> examenParId(@PathVariable Long id) {
        Optional<Examen> examen = examenDao.findById(id);

        if (!examen.isPresent()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(examen.get());
    }

    @PostMapping(value = "")
    public ResponseEntity creerExamen(@RequestBody ExamDto exam) {
        Compte professeur = compteDao.findByCourriel(exam.getProfesseur()).orElseThrow(IllegalStateException::new);
        Compte eleve = compteDao.findById(exam.getEleve()).orElseThrow(IllegalStateException::new);

        Examen examen = new Examen(professeur, eleve);
        examen.setReussi(exam.isReussi());
        examen.setTemps(exam.getTemps());

        examen = examenDao.save(examen);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(exam.getId())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping(value = "/eligibles/role")
    public ResponseEntity<Map<String, List<SanitizedCompte>>> trouverEligiblesExamRole() {
        Map<String, List<SanitizedCompte>> eligiblesParNiveau = new HashMap<>();

        List<SanitizedCompte> eligiblesAncien = new ArrayList<>();
        List<SanitizedCompte> eligiblesSensei = new ArrayList<>();
        List<SanitizedCompte> senseis = new ArrayList<>();
        List<SanitizedCompte> honteux = new ArrayList<>();

        getTous()
                .filter(ExamenController::estEligibleAncien)
                .map(Compte::sanitize)
                .forEach(eligiblesAncien::add);

        getTous()
               .filter(ExamenController::estEligibleSensei)
                .map(Compte::sanitize)
                .forEach(eligiblesSensei::add);

        getTous()
                .filter(c -> c.getRole().getRole().equals("Sensei"))
                .map(Compte::sanitize)
                .forEach(senseis::add);

        compteDao.findAll()
                .stream()
                .filter(Compte::isDeshonore)
                .forEach(honteux::add);


        eligiblesParNiveau.put("Ancien", eligiblesAncien);
        eligiblesParNiveau.put("Sensei", eligiblesSensei);
        eligiblesParNiveau.put("Demotion", senseis);
        eligiblesParNiveau.put("Deshonorables", honteux);

        return ResponseEntity.ok(eligiblesParNiveau);
    }

    private static boolean estEligibleAncien(Compte c) {
        return c.getRole().getRole().equals("Nouveau")
                && c.getCombatsArbitre().size() >= 30
                && c.getCredits() >= 10;
    }

    private static boolean estEligibleSensei(Compte c) {
        return  c.getRole().getRole().equals("Ancien")
                && c.getGroupe().getGroupe().equals("Noir");
    }

    @GetMapping(value = "/eligibles/groupe")
    public ResponseEntity<Map<String, List<SanitizedCompte>>> trouverEligiblesExamGroupe() {
        Map<String, List<SanitizedCompte>> eligiblesParGroupe = new HashMap<>();

        List<Compte> comptesEligibles = getTous()
                .filter(c -> c.getGroupe().getId() < 6)
                .filter(c -> c.getPoints() >= 100)
                .filter(c -> c.getCredits() >= 10)
                .collect(Collectors.toList());

        comptesEligibles
                .forEach(c -> {
                    if (!eligiblesParGroupe.containsKey(c.getGroupe().getGroupe())) {
                        List<SanitizedCompte> comptes = new ArrayList<>();
                        comptes.add(c);

                        eligiblesParGroupe.put(c.getGroupe().getGroupe(), comptes);
                    } else {
                        eligiblesParGroupe.get(c.getGroupe().getGroupe()).add(c);
                    }
                });

        return ResponseEntity.ok(eligiblesParGroupe);
    }

    private Stream<Compte> getTous() {
        return compteDao.findAll().stream();
    }
}
