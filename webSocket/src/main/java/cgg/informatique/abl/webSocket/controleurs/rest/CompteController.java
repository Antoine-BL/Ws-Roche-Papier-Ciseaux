package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.entites.Compte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api")
public class CompteController {
    private CompteDao compteDao;

    public CompteController(@Autowired CompteDao compteDao) {
        this.compteDao = compteDao;
    }

    @GetMapping("/comptes")
    public List<SanitizedCompte> getAllCompte() { return compteDao.findAll().stream().map(cgg.informatique.abl.webSocket.entites.Compte::sanitize).collect(Collectors.toList()); }

    @GetMapping("/comptes/{id}")
    public ResponseEntity<SanitizedCompte> getCompte(@PathVariable Long id) {
        Optional<cgg.informatique.abl.webSocket.entites.Compte> compte = compteDao.findById(id);


        if (compte.isPresent()) {
            SanitizedCompte compteSan = compte.get().sanitize();
            return ResponseEntity.ok(compteSan);
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
