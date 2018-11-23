package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dao.*;
import cgg.informatique.abl.webSocket.dto.AuthRequest;
import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.dto.SanitizedUser;
import cgg.informatique.abl.webSocket.entites.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController()
@RequestMapping("/api")
public class CompteController {
    private CompteDao compteDao;
    private CombatDao combatDao;
    private ExamenDao examenDao;
    private GroupeDao groupeDao;
    private RoleDao roleDao;
    private AuthenticationManager authManager;

    public CompteController(
            @Autowired CompteDao compteDao,
            @Autowired CombatDao combatDao,
            @Autowired ExamenDao examenDao,
            @Autowired GroupeDao groupeDao,
            @Autowired AuthenticationManager authManager,
            @Autowired RoleDao roleDao) {
        this.compteDao = compteDao;
        this.combatDao = combatDao;
        this.examenDao = examenDao;
        this.groupeDao = groupeDao;
        this.roleDao = roleDao;
        this.authManager = authManager;
    }

    @PostMapping("/authenticate/{username}/{password}")
    public ResponseEntity apiAuth(@PathVariable String username, @PathVariable String password,
                          @Autowired  HttpServletRequest req) {
        Compte userAccount = compteDao.findById(username).orElseThrow(IllegalArgumentException::new);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userAccount, password);
        Authentication auth = authManager.authenticate(authToken);
        SecurityContext secContext = SecurityContextHolder.getContext();
        secContext.setAuthentication(auth);

        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, secContext);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/comptes")
    public List<SanitizedCompte> getAllCompte() { return compteDao.findAll().stream().map(cgg.informatique.abl.webSocket.entites.Compte::sanitize).collect(Collectors.toList()); }

    @GetMapping("/comptes/{id}")
    public ResponseEntity<SanitizedCompte> getCompte(@PathVariable String id) {
        Optional<cgg.informatique.abl.webSocket.entites.Compte> compte = compteDao.findById(id);


        if (compte.isPresent()) {
            SanitizedUser compteSan = compte.get().sanitize();
            return ResponseEntity.ok(compte.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/comptes")
    public ResponseEntity addCompte(@RequestBody Compte compte) {
        boolean emailExiste = compteDao.existsByCourriel(compte.getUsername());

        if (emailExiste) {
            return ResponseEntity.badRequest().build();
        }

        cgg.informatique.abl.webSocket.entites.Compte compteAjoute = compteDao.save(compte);

        return ResponseEntity.created(GenerateCreatedURI(compteAjoute)).build();
    }

    @PostMapping("/compte/ceinture/{id}")
    public ResponseEntity promotionCeinture(@PathVariable String courriel) {
        Compte compte = compteDao.findByCourriel(courriel).orElseThrow(IllegalStateException::new);
        int nouveauGroupe = compte.getGroupe().getId() + 1;
        Optional<Groupe> groupe = groupeDao.findById(nouveauGroupe);

        if (!groupe.isPresent()) return ResponseEntity.badRequest().build();

        compte.setGroupe(groupe.get());

        compteDao.save(compte);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/compte/pardonner/{id}")
    public ResponseEntity pardonner(@PathVariable String id) {
        Compte compte = compteDao.findByCourriel(id).orElseThrow(IllegalStateException::new);

        List<Examen> examens = examenDao.findByEleve(compte);

        Examen plusRecent = examens.stream()
                .max(Comparator.comparingLong(Examen::getTemps))
                .orElseThrow(IllegalStateException::new);

        plusRecent.setReussi(true);
        examenDao.save(plusRecent);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/compte/role/{id}")
    public ResponseEntity promotionRole(@PathVariable String id) {
        Compte compte = compteDao.findByCourriel(id).orElseThrow(IllegalStateException::new);
        int nouveauRole = compte.getRole().getId() + 1;
        Optional<Role> role = roleDao.findById(nouveauRole);

        if (!role.isPresent()) return ResponseEntity.badRequest().build();

        compte.setRole(role.get());

        compteDao.save(compte);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/compte/demotion/{id}")
    public ResponseEntity demotionRole(@PathVariable String id) {
        Compte compte = compteDao.findByCourriel(id).orElseThrow(IllegalStateException::new);
        int nouveauRole = compte.getRole().getId() - 1;
        Optional<Role> role = roleDao.findById(nouveauRole);

        if (!role.isPresent()) return ResponseEntity.badRequest().build();

        compte.setRole(role.get());

        compteDao.save(compte);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/comptes")
    public ResponseEntity modifierCompte(@RequestBody Compte compte){
        if (!compteDao.exists(Example.of(compte))) {
            return ResponseEntity.badRequest().build();
        }

        compteDao.save(compte);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comptes/{id}")
    public ResponseEntity deleteCompte(@PathVariable String id){
        if (!compteDao.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        compteDao.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/monCompte")
    public ResponseEntity<SanitizedUser> getCurrentAccount(@Autowired Authentication auth) {
        if (auth != null) {
            Compte compte = (Compte)auth.getPrincipal();
            SanitizedUser compteSan = compte.sanitize();
            return ResponseEntity.ok(compteSan);
        }
        return ResponseEntity.noContent().build();
    }

    public static Integer getCreditsPour(Compte compte) {
        int credits = 0;
        if (compte.getExamensEleve() == null) return 0;
        if (compte.getCombatsArbitre() == null) return 0;

        long tempsMax = compte
                .getExamensEleve()
                .stream()
                .map(Examen::getTemps)
                .max(Comparator.comparing(Long::valueOf))
                .orElse(-1L);

        for (Combat combat : compte.getCombatsArbitre()) {
            credits += combat.getCreditsArbitre();
        }

        for (Examen examen : compte.getExamensEleve()) {
            credits -= examen.isReussi() ? 10 : 5;
        }

        if (compte.getRole().getId() > 0) {
            credits -= 10;

            if (credits < 0){
                credits = 0;
            }
        }

        return credits;
    }

    public static Integer getPointsPour(Compte compte) {
        int points = 0;
        if (compte.getExamensEleve() == null) return 0;
        if (compte.getCombatsRouge() == null) return 0;
        if (compte.getCombatsBlanc() == null) return 0;

        long tempsMax = compte
                .getExamensEleve()
                .stream()
                .filter(Examen::isReussi)
                .map(Examen::getTemps)
                .max(Comparator.comparing(Long::valueOf))
                .orElse(-1L);

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
                .buildAndExpand(compte.getCourriel())
                .toUri();
    }

    public CompteDao getDao() {
        return compteDao;
    }
}
