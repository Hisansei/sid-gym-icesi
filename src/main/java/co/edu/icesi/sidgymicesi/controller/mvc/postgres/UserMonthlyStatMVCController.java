package co.edu.icesi.sidgymicesi.controller.mvc.postgres;

import java.util.List;
import java.util.Optional;
import java.time.YearMonth;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import co.edu.icesi.sidgymicesi.model.postgres.UserMonthlyStat;
import co.edu.icesi.sidgymicesi.services.postgres.IUserMonthlyStatsService;

@Controller
@RequestMapping("/mvc/admin/users/stats")
@RequiredArgsConstructor
public class UserMonthlyStatMVCController {
    
    private final IUserMonthlyStatsService userMonthlyStatsService;

    @GetMapping
    public String getAllUserStats(Model model) {
        var stats = userMonthlyStatsService.listAll();
        model.addAttribute("userStats", stats);
        return "admin/users/stats/list";
    }

    @GetMapping("/detail")
    public String getUserStatsDetail(@RequestParam String userUsername, Model model) {
        List<UserMonthlyStat> stats = userMonthlyStatsService.listByUser(userUsername);

        if (stats.isEmpty()) {
            model.addAttribute("error", "Estadísticas no encontradas para el usuario: " + userUsername);
            return "admin/users/stats/list";
        }

        model.addAttribute("userStats", stats);
        return "admin/users/stats/detail";
    }
}