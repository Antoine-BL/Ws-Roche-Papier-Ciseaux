package cgg.informatique.abl.webSocket.controleurs.mvc;

import cgg.informatique.abl.webSocket.dao.CompteService;
import cgg.informatique.abl.webSocket.dto.CompteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class CompteMvcController {
    private CompteService compteService;

    public CompteMvcController(@Autowired CompteService compteService) {
        this.compteService = compteService;
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
