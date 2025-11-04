package co.edu.icesi.sidgymicesi.controller.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mvc/public/auth")
public class AuthControllerMVC {

    private static final Logger logger = LoggerFactory.getLogger(AuthControllerMVC.class);

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            logger.warn("Intento de login fallido");
            model.addAttribute("error", "email o contraseña incorrectos");
        }

        if (logout != null) {
            logger.info("Usuario cerró sesión exitosamente");
            model.addAttribute("message", "Has cerrado sesión exitosamente");
        }

        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        logger.warn("Intento de acceso no autorizado");
        return "auth/access-denied";
    }
}