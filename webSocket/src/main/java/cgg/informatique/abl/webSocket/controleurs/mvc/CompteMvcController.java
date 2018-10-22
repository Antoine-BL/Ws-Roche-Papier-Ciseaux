package cgg.informatique.abl.webSocket.controleurs.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CompteMvcController {
    @GetMapping("/compte/creer")
    public String createAccount(Model model) {
        return "creerCompte";
    }

    @GetMapping("/connexion")
    public String login(Model model) {
        return "login";
    }
}
