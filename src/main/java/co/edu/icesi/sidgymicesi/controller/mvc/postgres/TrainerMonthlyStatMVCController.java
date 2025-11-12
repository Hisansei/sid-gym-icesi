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

import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;

@Controller
@RequestMapping("/mvc/admin/trainers/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TrainerMonthlyStatMVCController {
    
    private final ITrainerStatsService trainerStatsService;

    @GetMapping
    public String getAllTrainerStats(Model model) {
        var stats = trainerStatsService.listAll();
        model.addAttribute("trainerStats", stats);
        return "admin/trainers/stats/list";
    }

    @GetMapping("/detail")
    public String getTrainerStatsDetail(@RequestParam String trainerUsername, @RequestParam String period, Model model) {

        YearMonth ym = YearMonth.parse(period); 

        Optional<TrainerMonthlyStat> optStat = trainerStatsService.get(trainerUsername, ym);

        if (optStat.isEmpty()) {
            model.addAttribute("error", "Estadísticas no encontradas para el entrenador: " + trainerUsername + " en el mes " + period);
            return "admin/trainers/stats/list";
        }

        TrainerMonthlyStat stat = optStat.get();
        model.addAttribute("trainerStat", stat);
        
        return "admin/trainers/stats/detail";
    }

    @GetMapping("/current")
    public String getTrainerStatsCurrentMonth(@RequestParam String trainerUsername, Model model) {
        YearMonth currentMonth = YearMonth.now();

        Optional<TrainerMonthlyStat> optStat = trainerStatsService.get(trainerUsername, currentMonth);

        if (optStat.isEmpty()) {
            model.addAttribute("error", "Estadísticas no encontradas para el entrenador: " + trainerUsername + " en el mes actual");
            return "admin/trainers/stats/list";
        }

        TrainerMonthlyStat stat = optStat.get();
        model.addAttribute("trainerStat", stat);
        
        return "admin/trainers/stats/detail";
    }

}