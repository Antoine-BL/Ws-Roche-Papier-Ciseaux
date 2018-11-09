package cgg.informatique.abl.webSocket.controleurs.mvc;

import cgg.informatique.abl.webSocket.dao.*;
import cgg.informatique.abl.webSocket.dto.CompteDto;
import cgg.informatique.abl.webSocket.dto.ModifyCompteDto;
import cgg.informatique.abl.webSocket.entites.Avatar;
import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Examen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CompteMvcController {
    private CompteService compteService;
    private CompteDao compteDao;
    private AvatarDao avatarDao;
    private CombatDao combatDao;
    private ExamenDao examenDao;
    private PasswordEncoder passwordEncoder;

    public CompteMvcController(
            @Autowired CompteService compteService,
            @Autowired CompteDao compteDao,
            @Autowired AvatarDao avatarDao,
            @Autowired CombatDao combatDao,
            @Autowired ExamenDao examenDao,
            @Autowired PasswordEncoder passwordEncoder
            ) {
        this.compteService = compteService;
        this.compteDao = compteDao;
        this.avatarDao = avatarDao;
        this.combatDao = combatDao;
        this.examenDao = examenDao;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/monCompte")
    public String gestionCompte(Model model, @Autowired Authentication auth) {
        Compte compte = (Compte)auth.getPrincipal();
        ModifyCompteDto compteDto = new ModifyCompteDto();

        compteDto.setAlias(compte.getAlias());
        compteDto.setAvatar(compte.getAvatar().getImage());
        compteDto.setCourriel(compte.getCourriel());

        model.addAttribute("user", compteDto);

        return "gestionCompte";
    }

    @GetMapping("/monCompte/supprimer")
    public String confirmationSuppression(Model model) {
        model.addAttribute("password", "");

        return "supprimerCompte";
    }

    @PutMapping("/monCompte")
    public ModelAndView modifierCompte(
                                  @ModelAttribute("user") @Valid ModifyCompteDto compteDto,
                                  BindingResult result,
                                  WebRequest request,
                                  Errors errors,
                                  @Autowired Authentication auth) {
        ModelAndView model = new ModelAndView("gestionCompte");

        Compte compte = (Compte)auth.getPrincipal();

        if (!compteDto.getCourriel().equals(compte.getCourriel())) throw new IllegalArgumentException();

        if (!compteDto.getPassword().isEmpty()) {
            if (validerMotPasse(compteDto, compte.getPassword())) {
                compte.setMotPasse(passwordEncoder.encode(compteDto.getPassword()));
            }
        }

        compte.setAlias(compteDto.getAlias());
        compte.setAvatar(new Avatar(compteDto.getAvatar()));

        avatarDao.saveAndFlush(compte.getAvatar());
        compteDao.saveAndFlush(compte);

        compteDto.setMatchingPassword("");
        compteDto.setOldPassword("");
        compteDto.setPassword("");
        model.addObject("user", compteDto);

        return model;
    }

    @DeleteMapping("/monCompte")
    public ModelAndView supprimerCompte(HttpServletRequest request) throws ServletException {
        ModelAndView model = new ModelAndView("index");

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
        Compte compte = (Compte) token.getPrincipal();
        String password = request.getParameter("password");
        if (password == null || password.isEmpty()) throw new IllegalArgumentException();

        if (!passwordEncoder.matches(password, compte.getPassword())) throw new IllegalArgumentException();

        List<Combat> combats = new ArrayList<>();

        combats.addAll(compte.getCombatsArbitre());
        combats.addAll(compte.getCombatsRouge());
        combats.addAll(compte.getCombatsBlanc());

        for (Combat combat : compte.getCombatsArbitre()) {
            combat.setArbitre(null);
        }

        for (Combat combat : compte.getCombatsBlanc()) {
            combat.setBlanc(null);
        }

        for (Combat combat : compte.getCombatsRouge()) {
            combat.setRouge(null);
        }

        combatDao.saveAll(combats);
        combatDao.flush();

        List<Examen> examens = new ArrayList<>();

        examens.addAll(compte.getExamensEleve());
        examens.addAll(compte.getExamensProf());

        for (Examen combat : compte.getExamensProf()) {
            combat.setProfesseur(null);
        }

        for (Examen combat : compte.getExamensProf()) {
            combat.setEleve(null);
        }

        examenDao.saveAll(examens);
        examenDao.flush();

        compteDao.delete(compte);
        compteDao.flush();

        request.logout();
        request.getSession().invalidate();

        return model;
    }

    private boolean validerMotPasse(ModifyCompteDto compteDto, String vraiMotPasse) {
        if (!passwordEncoder.matches(compteDto.getOldPassword(), vraiMotPasse)) return  false;
        return compteDto.getPassword().equals(compteDto.getMatchingPassword());
    }


    @GetMapping("/compte/creer")
    public String createAccountPage(Model model) {
        CompteDto compteDto = new CompteDto();

        model.addAttribute("user", compteDto);

        return "creerCompte";
    }

    @PostMapping("/compte/creer")
    public ModelAndView createAccount(
            @ModelAttribute("user") @Valid CompteDto compteDto,
            BindingResult result,
            WebRequest request,
            Errors errors
    ) {
        if (result.hasErrors()) return new ModelAndView("creerCompte", "user", compteDto);
        try {
            compteService.registerNewUserAccount(compteDto);

            return new ModelAndView("login", "user", compteDto);
        } catch (IllegalArgumentException e) {
            return new ModelAndView("creerCompte", "user", compteDto);
        }
    }

    @GetMapping(value = "/connexion")
    public String login(Model model) {
        CompteDto compteDto = new CompteDto();

        model.addAttribute("user", compteDto);

        return "login";
    }
}
