package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dao.*;
import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.dto.SanitizedUser;
import cgg.informatique.abl.webSocket.entites.*;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.match.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private PasswordEncoder passwordEncoder;

    public static Compte VENERABLE;
    public static Compte SENSEI1;

    public static GetDefaultAccountsTask accountsTask;

    public CompteController(
            @Autowired CompteDao compteDao,
            @Autowired CombatDao combatDao,
            @Autowired ExamenDao examenDao,
            @Autowired GroupeDao groupeDao,
            @Autowired AuthenticationManager authManager,
            @Autowired RoleDao roleDao,
            @Autowired PasswordEncoder passwordEncoder) {
        this.compteDao = compteDao;
        this.combatDao = combatDao;
        this.examenDao = examenDao;
        this.groupeDao = groupeDao;
        this.roleDao = roleDao;
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;

        VENERABLE = compteDao.findById("s1@dojo").get();
        SENSEI1 = compteDao.findById("v1@dojo").get();

        accountsTask = new GetDefaultAccountsTask();
        new Thread(accountsTask).run();
    }

    @PostMapping("/authenticate/{username}/{password}")
    public ResponseEntity apiAuth(@PathVariable String username, @PathVariable String password,
                          @Autowired  HttpServletRequest req) {
        try {
            Compte userAccount = compteDao.findById(username).orElseThrow(IllegalArgumentException::new);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userAccount, password);
            Authentication auth = authManager.authenticate(authToken);
            SecurityContext secContext = SecurityContextHolder.getContext();
            secContext.setAuthentication(auth);

            HttpSession session = req.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, secContext);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/comptes")
    public List<SanitizedCompte> getAllCompte() {
        List<SanitizedCompte> comptes = compteDao.findAll().stream().map(cgg.informatique.abl.webSocket.entites.Compte::sanitize).collect(Collectors.toList());
        return comptes;
    }

    /**
     * @return la liste de tout les combats de l'utilisateur
     */
    @GetMapping("/myCombats")
    public List<Combat> getAllMyCombat(@Autowired Authentication auth) {
        List<Combat> myCombats = new ArrayList<>();
        if (auth != null) {
            String courriel = ((Compte)auth.getPrincipal()).getCourriel();
            Compte compte = compteDao.findById(courriel)
                    .orElseThrow(IllegalArgumentException::new);

            myCombats = Stream.concat(combatDao.findAllByRouge(compte).stream(), combatDao.findAllByBlanc(compte).stream())
                    .collect(Collectors.toList());

            myCombats.sort( (c1, c2) -> c1.getTemps() < c2.getTemps() ? 1 : -1);
        }

        return myCombats;
    }
    /**
     * @return la liste de tout les Examens de l'utilisateur
     */
    @GetMapping("/myExamens")
    public List<Examen> getAllMyExamen(@Autowired Authentication auth) {
        List<Examen> myExamens = new ArrayList<>();
        if (auth != null) {
            String courriel = ((Compte)auth.getPrincipal()).getCourriel();
            Compte compte = compteDao.findById(courriel)
                    .orElseThrow(IllegalArgumentException::new);

            myExamens =examenDao.findByEleve(compte);

            myExamens.sort( (e1, e2) -> e1.getTemps() < e2.getTemps() ? 1 : -1);
        }

        return myExamens;
    }

    @GetMapping("/comptes/defaults")
    public ResponseEntity<List<SanitizedCompte>> getComptesDefaut() {
        synchronized (accountsTask) {
            try {
                if (accountsTask.getDefaultAccounts() == null) {
                    accountsTask.wait();
                } else {
                    return ResponseEntity.ok(accountsTask.getDefaultAccounts());
                }
            } catch (InterruptedException e) {
                return ResponseEntity.ok(accountsTask.getDefaultAccounts());
            }
        }

        return ResponseEntity.badRequest().build();
    }

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
            String courriel = ((Compte)auth.getPrincipal()).getCourriel();
            SanitizedUser compteSan = compteDao.findById(courriel)
                    .orElseThrow(IllegalArgumentException::new)
                    .sanitize();

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
                points += pointsPourMatch(combat, LobbyRole.ROUGE);
            }
        }

        for (Combat combat : compte.getCombatsBlanc()) {
            if (combat.getTemps() > tempsMax) {
                points += pointsPourMatch(combat, LobbyRole.BLANC);
            }
        }

        return points;
    }

    private static int pointsPourMatch(Combat combat, LobbyRole role) {
        int pointsRouge;
        int pointsBlanc;

        if (combat.getPointsRouge() == 10 && combat.getPointsBlanc() == 10) {
            pointsRouge = Match.calculerPointsGagnant(combat.getCeintureRouge(), combat.getCeintureBlanc()) / 2;
            pointsBlanc = Match.calculerPointsGagnant(combat.getCeintureBlanc(), combat.getCeintureRouge()) / 2;
        } else if (combat.getPointsRouge() == 5 && combat.getPointsBlanc() == 5) {
            pointsRouge = Match.calculerPointsGagnant(combat.getCeintureRouge(), combat.getCeintureBlanc()) / 2;
            pointsBlanc = Match.calculerPointsGagnant(combat.getCeintureBlanc(), combat.getCeintureRouge()) / 2;
        } else if (combat.getPointsRouge() == 0 && combat.getPointsBlanc() == 0) {
            pointsRouge = 0;
            pointsBlanc = 0;
        } else if (combat.getPointsRouge() == 10) {
            pointsRouge = Match.calculerPointsGagnant(combat.getCeintureRouge(), combat.getCeintureBlanc());
            pointsBlanc = 0;
        } else if (combat.getPointsBlanc() == 10) {
            pointsRouge = 0;
            pointsBlanc = Match.calculerPointsGagnant(combat.getCeintureBlanc(), combat.getCeintureRouge());
        } else {
            throw new IllegalArgumentException("échec calcul points");
        }

        return role == LobbyRole.BLANC ? pointsBlanc : pointsRouge;
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


    /**----------------------------------------------------------------------
     *
     * Les fonctions ci-dessous sont pour le prototype android uniquement
     *
     -----------------------------------------------------------------------**/

    @PostMapping(value = "anciennete/{id}")
    public String passageAnciennete(@PathVariable String id) {
        Compte compte = compteDao.findByCourriel(id).orElseThrow(IllegalStateException::new);

        if(ExamenController.estEligibleAncien(compte)){
            int nouveauRole = compte.getRole().getId() + 1;
            Optional<Role> role = roleDao.findById(nouveauRole);

            if (!role.isPresent()) return "Une erreur est survenue";

            compte.setRole(role.get());

            compteDao.save(compte);

            return compte.getAlias() + " est maintenant un ancien";
        }
        else{
            return compte.getAlias() + " n'est pas éligible à devenir Ancient";
        }
    }

    private boolean estEligibleExamen(Compte c){
        return  (c.getGroupe().getId() < 6)
                &&( c.getPoints() >= 100)
                &&( c.getCredits() >= 10);
    }
    private void genereExamen(Compte professeur, Compte eleve, boolean estReussi){
        long temps = System.currentTimeMillis();

        Examen examen = new Examen(professeur, eleve);
        examen.setReussi(estReussi);
        examen.setTemps(temps);

        examen = examenDao.save(examen);
    }

    @PostMapping(value = "examen/reussi/{id}")
    public String genereExamenReussi(@PathVariable String id) {
        Compte professeur = compteDao.findByCourriel("v1@dojo").orElseThrow(IllegalStateException::new);
        Compte eleve = compteDao.findByCourriel(id).orElseThrow(IllegalStateException::new);

        if(estEligibleExamen(eleve)){
            int nouveauGroupe = eleve.getGroupe().getId() + 1;
            Optional<Groupe> groupe = groupeDao.findById(nouveauGroupe);

            if (!groupe.isPresent()) return "Une erreur est survenue";
            eleve.setGroupe(groupe.get());
            compteDao.save(eleve);

            genereExamen(professeur, eleve, true);

            return eleve.getAlias() + " vient de passer un examen";
        }
        else {
            return eleve.getAlias() + " n'est pas éligible à passer un examen";
        }
    }
    @PostMapping(value = "examen/echec/{id}")
    public String genereExamenEchec(@PathVariable String id) {
        Compte professeur = compteDao.findByCourriel("v1@dojo").orElseThrow(IllegalStateException::new);
        Compte eleve = compteDao.findByCourriel(id).orElseThrow(IllegalStateException::new);

        if(estEligibleExamen(eleve)){
            genereExamen(professeur, eleve, false);

            return eleve.getAlias() + " vient d'échouer un examen";
        }
        else {
            return eleve.getAlias() + " n'est pas éligible à passer un examen";
        }
    }

    private class GetDefaultAccountsTask implements Runnable {
        private List<SanitizedCompte> defaultAccounts;
        private static final String DEFAULT_PASSWORD = "";

        public List<SanitizedCompte> getDefaultAccounts() {
            return defaultAccounts;
        }

        @Override
        public void run() {
            List<SanitizedCompte> comptes =
                compteDao.
                    findAll()
                    .stream()
                    .filter(this::isDefault)
                    .map(Compte::sanitize)
                    .collect(Collectors.toList());
            synchronized (this) {
                defaultAccounts = comptes;
                notifyAll();
            }
        }

        private boolean isDefault(Compte compte) {
            final String DEFAULT_PASSWORD = "Patate123";
            return passwordEncoder.matches(DEFAULT_PASSWORD, compte.getPassword());
        }
    }
}
