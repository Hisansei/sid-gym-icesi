package co.edu.icesi.sidgymicesi.controller;

import java.util.Optional;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import co.edu.icesi.sidgymicesi.model.User;
import co.edu.icesi.sidgymicesi.services.IUserService;

@Controller
@RequestMapping("/mvc/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final IUserService userService;

    // Ruta: http://localhost:8081/sid-gym-icesi/mvc/admin/users

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users/list";
    }

    @GetMapping("/detail")
    public String getUserDetail(@RequestParam String username, Model model) {

        Optional<User> opt = userService.findByUsername(username);

        if (opt.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado: " + username);
            model.addAttribute("users", userService.findAll());
            return "admin/users/list";
        }

        User user = opt.get();
        model.addAttribute("actualUser", user);

        return "admin/users/detail";
    }

}