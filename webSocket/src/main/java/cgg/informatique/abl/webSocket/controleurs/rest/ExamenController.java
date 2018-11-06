package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.dao.ExamenDao;
import cgg.informatique.abl.webSocket.dao.GroupeDao;
import cgg.informatique.abl.webSocket.dao.RoleDao;
import cgg.informatique.abl.webSocket.dto.ExamDto;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Examen;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.xml.ws.Response;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

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
        Compte professeur = compteDao.findById(exam.getProfesseur()).orElseThrow(IllegalStateException::new);
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
    public ResponseEntity<Map<String, List<Compte>>> trouverEligiblesExamRole() {
        Map<String, List<Compte>> eligiblesParNiveau = new HashMap<>();

        List<Compte> eligiblesAncien = new ArrayList<>();
        List<Compte> eligiblesSensei = new ArrayList<>();
        List<Compte> senseis = new ArrayList<>();

       compteDao.findAll()
                .stream()
                .filter(c -> c.getRole().equals("Nouveau"))
                .filter(c -> c.getCombatsArbitre().size() >= 30)
                .filter(c -> CompteController.getCreditsPour(c) >= 10)
                .forEach(eligiblesAncien::add);

       compteDao.findAll()
               .stream()
               .filter(c -> c.getRole().equals("Ancien"))
               .filter(c -> c.getGroupe().equals("Noir"))
               .forEach(eligiblesSensei::add);

        compteDao.findAll()
                .stream()
                .filter(c -> c.getRole().equals("Sensei"))
                .forEach(senseis::add);

        eligiblesParNiveau.put("Ancien", eligiblesAncien);
        eligiblesParNiveau.put("Sensei", eligiblesSensei);
        eligiblesParNiveau.put("Demotion", senseis);

        return ResponseEntity.ok(eligiblesParNiveau);
    }

    @GetMapping(value = "/eligibles/groupe")
    public ResponseEntity<Map<String, List<Compte>>> trouverEligiblesExamGroupe() {
        Map<String, List<Compte>> eligiblesParGroupe = new HashMap<>();

        List<Compte> comptesEligibles = compteDao.findAll()
                .stream()
                .filter(c -> CompteController.getPointsPour(c) >= 100)
                .filter(c -> CompteController.getCreditsPour(c) >= 10)
                .collect(Collectors.toList());

        comptesEligibles
                .forEach(c -> {
                    if (!eligiblesParGroupe.containsKey(c.getGroupe())) {
                        List<Compte> comptes = new ArrayList<>();
                        comptes.add(c);

                        eligiblesParGroupe.put(c.getGroupe(), comptes);
                    } else {
                        eligiblesParGroupe.get(c.getGroupe()).add(c);
                    }
                });

        return ResponseEntity.ok(eligiblesParGroupe);
    }
}
