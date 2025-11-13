package co.edu.icesi.sidgymicesi.controller.mvc.postgres;

import java.util.List;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;

@Controller
@RequestMapping("/mvc/admin/trainers/stats")
@RequiredArgsConstructor
public class TrainerMonthlyStatMVCController {
    
    private final ITrainerStatsService trainerStatsService;

    @GetMapping
    public String getAllTrainerStats(Model model) {
        var stats = trainerStatsService.listAll();
        model.addAttribute("trainerStats", stats);
        return "admin/trainers/stats/list";
    }

    @GetMapping("/detail")
    public String getTrainerStatsDetail(@RequestParam String trainerUsername, Model model) {
        List<TrainerMonthlyStat> stats = trainerStatsService.listByTrainer(trainerUsername);

        if (stats.isEmpty()) {
            model.addAttribute("error", "Estadísticas no encontradas para el entrenador: " + trainerUsername);
            return "admin/trainers/stats/list";
        }

        model.addAttribute("trainerStats", stats);
        return "admin/trainers/stats/detail";
    }
}