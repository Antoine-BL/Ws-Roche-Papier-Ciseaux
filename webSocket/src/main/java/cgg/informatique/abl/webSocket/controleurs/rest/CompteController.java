package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.configurations.http.UserDetailsImpl;
import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.dto.SanitaryCompte;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Role;
import com.sun.jndi.toolkit.url.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;

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
    public List<SanitaryCompte> getAllCompte() { return compteDao.findAll().stream().map(Compte::sanitize).collect(Collectors.toList()); }

    @GetMapping("/comptes/{id}")
    public ResponseEntity<SanitaryCompte> getCompte(@PathVariable Long id) {
        Optional<Compte> compte = compteDao.findById(id);


        if (compte.isPresent()) {
            SanitaryCompte compteSan = compte.get().sanitize();
            return ResponseEntity.ok(compteSan);
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

        Compte compteAjoute = compteDao.save(compte);

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
    public ResponseEntity<SanitaryCompte> getCurrentAccount(@Autowired Authentication auth) {
        if (auth != null) {
            UserDetailsImpl udi = (UserDetailsImpl)auth.getPrincipal();
            SanitaryCompte compteSan = udi.getCompte().sanitize();
            return ResponseEntity.ok(compteSan);
        }
        return ResponseEntity.noContent().build();
    }

    private URI GenerateCreatedURI(Compte compte) {
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
