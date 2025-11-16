package co.edu.icesi.sidgymicesi.controller.mvc.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.UserMonthlyStat;
import co.edu.icesi.sidgymicesi.services.postgres.IUserMonthlyStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mvc/admin/users/stats")
@RequiredArgsConstructor
public class UserMonthlyStatMVCController {

    private final IUserMonthlyStatsService userMonthlyStatsService;

    @GetMapping
    public String getAllUserStats(
            @RequestParam(value = "userUsername", required = false) String userUsername,
            Model model) {

        List<UserMonthlyStat> stats;

        // Si viene filtro por usuario, uso listByUser
        if (userUsername != null && !userUsername.isBlank()) {
            stats = userMonthlyStatsService.listByUser(userUsername);
            model.addAttribute("filterUserUsername", userUsername);
        } else {
            // listAll() devuelve Map<String, List<UserMonthlyStat>>
            Map<String, List<UserMonthlyStat>> statsByUser = userMonthlyStatsService.listAll();
            stats = new ArrayList<>();
            statsByUser.values().forEach(stats::addAll);
        }

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
