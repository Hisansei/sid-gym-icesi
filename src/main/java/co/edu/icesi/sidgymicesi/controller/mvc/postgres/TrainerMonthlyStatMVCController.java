package co.edu.icesi.sidgymicesi.controller.mvc.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;
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
@RequestMapping("/mvc/admin/trainers/stats")
@RequiredArgsConstructor
public class TrainerMonthlyStatMVCController {

    private final ITrainerStatsService trainerStatsService;

    @GetMapping
    public String getAllTrainerStats(
            @RequestParam(value = "trainerUsername", required = false) String trainerUsername,
            Model model) {

        List<TrainerMonthlyStat> stats;

        // Si viene filtro por entrenador, uso directamente listByTrainer
        if (trainerUsername != null && !trainerUsername.isBlank()) {
            stats = trainerStatsService.listByTrainer(trainerUsername);
            model.addAttribute("filterTrainerUsername", trainerUsername);
        } else {
            // listAll() devuelve Map<String, List<TrainerMonthlyStat>>
            Map<String, List<TrainerMonthlyStat>> statsByTrainer = trainerStatsService.listAll();
            stats = new ArrayList<>();
            statsByTrainer.values().forEach(stats::addAll);
        }

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
