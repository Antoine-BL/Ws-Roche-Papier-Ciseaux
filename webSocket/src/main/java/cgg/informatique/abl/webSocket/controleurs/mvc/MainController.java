package cgg.informatique.abl.webSocket.controleurs.mvc;

import cgg.informatique.abl.webSocket.configurations.UserDetailsImpl;
import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private CompteDao compteDao;

    public MainController(@Autowired CompteDao compteDao) {
        this.compteDao = compteDao;
    }

    @GetMapping("/")
    public String index(@Autowired Authentication auth, Model model) {
        loadUserInfoIntoModel(auth, model);

        return "index";
    }

    @GetMapping("/ecole")
    public String ecole(@Autowired Authentication auth, Model model) {
        loadUserInfoIntoModel(auth, model);

        List<Compte> comptes = compteDao.findAll();
        HashMap<Role, List<Compte>> roles = new HashMap<>();

        for(Compte compte : comptes) {
            if (!roles.containsKey(compte.getRole())) {
                List<Compte> comptesRole = new ArrayList<>();
                comptesRole.add(compte);
                roles.put(compte.getRole(), comptesRole);
            } else {
                roles.get(compte.getRole()).add(compte);
            }
        }

        for (Map.Entry<Role, List<Compte>> entree : roles.entrySet()) {
            model.addAttribute(entree.getKey().toString(), entree.getValue());
        }

        return "ecole";
    }

    @GetMapping("/kumite")
    public String kumite(@Autowired Authentication auth, Model model) {
        loadUserInfoIntoModel(auth, model);

        return "kumite";
    }

    @GetMapping("/passage")
    public String passage(@Autowired Authentication auth, Model model) {
        loadUserInfoIntoModel(auth, model);

        return "passage";
    }

    private void loadUserInfoIntoModel(Authentication auth, Model model) {
        if (auth != null) {
            UserDetailsImpl details = (UserDetailsImpl)auth.getPrincipal();

            model.addAttribute("username", details.getAlias());
            model.addAttribute("profilePic", details.getAvatar());
            model.addAttribute("role", details.getRole().toString().toLowerCase());
            model.addAttribute("groupe", details.getGroupe().toString().toLowerCase());
        }

        model.addAttribute("authentifie", auth != null);
    }
}
