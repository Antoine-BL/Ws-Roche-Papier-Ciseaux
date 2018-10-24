package cgg.informatique.abl.webSocket.controleurs.mvc;

import cgg.informatique.abl.webSocket.configurations.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String index(@Autowired Authentication auth, Model model) {
        if (auth != null) {
            UserDetailsImpl details = (UserDetailsImpl)auth.getPrincipal();

            model.addAttribute("username", details.getAlias());
            model.addAttribute("profilePic", details.getAvatar());
            model.addAttribute("role", details.getRole().toString().toLowerCase());
            model.addAttribute("groupe", details.getGroupe().toString().toLowerCase());
        }

        return "index";
    }

    @GetMapping("/ecole")
    public String ecole() {
        return "ecole";
    }

    @GetMapping("/kumite")
    public String kumite() {
        return "kumite";
    }

    @GetMapping("/passage")
    public String passage() {
        return "passage";
    }
}
