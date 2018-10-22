package cgg.informatique.abl.webSocket.controleurs.mvc;

import cgg.informatique.abl.webSocket.dto.CompteDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;

@Controller
public class CompteMvcController {
    @GetMapping("/compte/creer")
    public String createAccountPage(Model model) {
        CompteDto compteDto = new CompteDto();

        model.addAttribute("user", compteDto);

        return "creerCompte";
    }

    @PostMapping("/compte/creer")
    public String createAccount(
            @ModelAttribute("user") @Valid CompteDto compteDto,
            BindingResult result,
            WebRequest request,
            Errors errors
    ) {
        return "index";
    }

    @GetMapping("/connexion")
    public String login(Model model) {
        return "login";
    }
}
