package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dao.CombatDao;
import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.entites.Compte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api")
public class CompteController {
    private CompteDao compteDao;
    private CombatDao combatDao;

    public CompteController(
            @Autowired CompteDao compteDao,
            @Autowired CombatDao combatDao) {
        this.compteDao = compteDao;
        this.combatDao = combatDao;
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
    public ResponseEntity addCompte(@RequestBody cgg.informatique.abl.webSocket.entites.Compte compte) {
        boolean compteExiste = compteDao.existsById(compte.getId());
        boolean emailExiste = compteDao.existsByCourriel(compte.getUsername());

        if (compteExiste || emailExiste) {
            return ResponseEntity.badRequest().build();
        }

        cgg.informatique.abl.webSocket.entites.Compte compteAjoute = compteDao.save(compte);

        return ResponseEntity.created(GenerateCreatedURI(compteAjoute)).build();
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
        return getCreditsPour((Compte)auth.getPrincipal());
    }

    @GetMapping("/monCompte/points")
    public ResponseEntity<Integer> getMesPoints(@Autowired Authentication auth) {
        return getPointsPour((Compte)auth.getPrincipal());
    }

    @GetMapping("/comptes/{id}/credits")
    public ResponseEntity<Integer> getCredits(@PathVariable Long id) {
        Optional<Compte> compte = compteDao.findById(id);

        if (!compte.isPresent()) return ResponseEntity.notFound().build();

        return getCreditsPour(compte.get());
    }

    @GetMapping("/comptes/{id}/points")
    public ResponseEntity<Integer> getPoints(@PathVariable Long id) {
        Optional<Compte> compte = compteDao.findById(id);

        if (!compte.isPresent()) return ResponseEntity.notFound().build();

        return getPointsPour(compte.get());
    }

    private ResponseEntity<Integer> getCreditsPour(Compte compte) {
        int credits = 0;

        for (Combat combat : compte.getCombatsArbitre()) {
            credits += combat.getResultat().calculerRecompensePour(compte, combat);
        }

        return ResponseEntity.ok(credits);
    }

    private ResponseEntity<Integer> getPointsPour(Compte compte) {
        int points = 0;

        List<Combat> combatsParticipe = new ArrayList<>();
        combatsParticipe.addAll(compte.getCombatsBlanc());
        combatsParticipe.addAll(compte.getCombatsRouge());

        for (Combat combat : combatsParticipe) {
            points += combat.getResultat().calculerRecompensePour(compte, combat);
        }

        return ResponseEntity.ok(points);
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
