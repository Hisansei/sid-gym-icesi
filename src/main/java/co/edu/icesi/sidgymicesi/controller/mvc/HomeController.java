package co.edu.icesi.sidgymicesi.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mvc/home")
public class HomeController {
    @GetMapping
    public String home() {
        return "home";
    }
}