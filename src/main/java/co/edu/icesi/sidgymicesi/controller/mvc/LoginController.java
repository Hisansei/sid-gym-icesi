package co.edu.icesi.sidgymicesi.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mvc/auth")
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}