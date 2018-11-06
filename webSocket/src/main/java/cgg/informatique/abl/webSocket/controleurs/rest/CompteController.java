package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dao.*;
import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.entites.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api")
public class CompteController {
    private CompteDao compteDao;
    private CombatDao combatDao;
    private ExamenDao examenDao;
    private GroupeDao groupeDao;
    private RoleDao roleDao;

    public CompteController(
            @Autowired CompteDao compteDao,
            @Autowired CombatDao combatDao,
            @Autowired ExamenDao examenDao,
            @Autowired GroupeDao groupeDao,
            @Autowired RoleDao roleDao) {
        this.compteDao = compteDao;
        this.combatDao = combatDao;
        this.examenDao = examenDao;
        this.groupeDao = groupeDao;
        this.roleDao = roleDao;
    }

    @GetMapping("/comptes")
    public List<SanitizedCompte> getAllCompte() { return compteDao.findAll().stream().map(cgg.informatique.abl.webSocket.entites.Compte::sanitize).collect(Collectors.toList()); }

    @GetMapping("/comptes/{id}")
    public ResponseEntity<Compte> getCompte(@PathVariable Long id) {
        Optional<cgg.informatique.abl.webSocket.entites.Compte> compte = compteDao.findById(id);


        if (compte.isPresent()) {
            SanitizedCompte compteSan = compte.get().sanitize();
            return ResponseEntity.ok(compte.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/comptes")
    public ResponseEntity addCompte(@RequestBody Compte compte) {
        boolean compteExiste = compteDao.existsById(compte.getId());
        boolean emailExiste = compteDao.existsByCourriel(compte.getUsername());

        if (compteExiste || emailExiste) {
            return ResponseEntity.badRequest().build();
        }

        cgg.informatique.abl.webSocket.entites.Compte compteAjoute = compteDao.save(compte);

        return ResponseEntity.created(GenerateCreatedURI(compteAjoute)).build();
    }

    @PostMapping("/compte/ceinture/{id}")
    public ResponseEntity promotionCeinture(@PathVariable Long id) {
        Compte compte = compteDao.findById(id).orElseThrow(IllegalStateException::new);
        int nouveauGroupe = compte.getGroupeObj().getId() + 1;
        Optional<Groupe> groupe = groupeDao.findById(nouveauGroupe);

        if (!groupe.isPresent()) return ResponseEntity.badRequest().build();

        compte.setGroupeObj(groupe.get());

        compteDao.save(compte);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/compte/role/{id}")
    public ResponseEntity promotionRole(@PathVariable Long id) {
        Compte compte = compteDao.findById(id).orElseThrow(IllegalStateException::new);
        int nouveauRole = compte.getRoleObj().getId() + 1;
        Optional<Role> role = roleDao.findById(nouveauRole);

        if (!role.isPresent()) return ResponseEntity.badRequest().build();

        compte.setRoleObj(role.get());

        compteDao.save(compte);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/compte")
    public ResponseEntity modifierCompte(@RequestBody Compte compte){
        if (!compteDao.exists(Example.of(compte))) {
            return ResponseEntity.badRequest().build();
        }

        compteDao.save(compte);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comptes")
    public ResponseEntity deleteCompte(Long id){
        if (!compteDao.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        compteDao.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/monCompte")
    public ResponseEntity<SanitizedCompte> getCurrentAccount(@Autowired Authentication auth) {
        if (auth != null) {
            Compte compte = (Compte)auth.getPrincipal();
            SanitizedCompte compteSan = compte.sanitize();
            return ResponseEntity.ok(compteSan);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/monCompte/credits")
    public ResponseEntity<Integer> getMesCredits(@Autowired Authentication auth) {
        return ResponseEntity.ok(getCreditsPour((Compte)auth.getPrincipal()));
    }

    @GetMapping("/monCompte/points")
    public ResponseEntity<Integer> getMesPoints(@Autowired Authentication auth) {
        return ResponseEntity.ok(getPointsPour((Compte)auth.getPrincipal()));
    }

    @GetMapping("/comptes/{id}/credits")
    public ResponseEntity<Integer> getCredits(@PathVariable Long id) {
        Optional<Compte> compte = compteDao.findById(id);

        if (!compte.isPresent()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(getCreditsPour(compte.get()));
    }

    @GetMapping("/comptes/{id}/points")
    public ResponseEntity<Integer> getPoints(@PathVariable Long id) {
        Optional<Compte> compte = compteDao.findById(id);

        if (!compte.isPresent()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(getPointsPour(compte.get()));
    }

    public static Integer getCreditsPour(Compte compte) {
        int credits = 0;
        long tempsMax = (long) compte
                .getExamensEleve()
                .stream()
                .map(Examen::getTemps)
                .max(Comparator.comparing(Long::valueOf))
                .orElseGet(() -> -1L);

        for (Combat combat : compte.getCombatsArbitre()) {
            credits += combat.getCreditsArbitre();
        }

        for (Examen examen : compte.getExamensEleve()) {
            credits -= 10;
        }

        return credits;
    }

    public static Integer getPointsPour(Compte compte) {
        int points = 0;
        long tempsMax = (long) compte
                .getExamensEleve()
                .stream()
                .map(Examen::getTemps)
                .max(Comparator.comparing(Long::valueOf))
                .orElseGet(() -> -1L);

        for (Combat combat : compte.getCombatsRouge()) {
            if (combat.getTemps() > tempsMax) {
                points += combat.getPointsRouge();
            }
        }

        for (Combat combat : compte.getCombatsBlanc()) {
            if (combat.getTemps() > tempsMax) {
                points += combat.getPointsBlanc();
            }
        }

        return points;
    }

    private URI GenerateCreatedURI(cgg.informatique.abl.webSocket.entites.Compte compte) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(compte.getId())
                .toUri();
    }

    public CompteDao getDao() {
        return compteDao;
    }
}
