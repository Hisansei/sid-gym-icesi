package co.edu.icesi.sidgymicesi.controller.mvc;

import co.edu.icesi.sidgymicesi.services.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mvc/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    @GetMapping("/select")
    public String select(Authentication auth,
                         @RequestParam(value = "username", required = false) String username,
                         Model model) {

        String effectiveUsername = (username != null && !username.isBlank())
                ? username
                : (auth != null ? auth.getName() : "");

        model.addAttribute("username", effectiveUsername);
        return "reports/select";
    }

    @PostMapping("/generateConsistencyReport")
    public String generateConsistencyReport(@RequestParam String username,
                                            @RequestParam int numberOfWeeks,
                                            Model model) {
        if (username == null || username.isBlank()) {
            model.addAttribute("error", "Debes seleccionar un usuario para generar el reporte.");
            model.addAttribute("username", "");
            return "reports/select";
        }
        var report = reportService.generateConsistencyReport(username, numberOfWeeks);
        model.addAttribute("username", username);
        model.addAttribute("report", report);
        return "reports/consistencyReport";
    }

    @PostMapping("/generateExerciseTypeReport")
    public String generateExerciseTypeReport(@RequestParam String username, Model model) {
        if (username == null || username.isBlank()) {
            model.addAttribute("error", "Debes seleccionar un usuario para generar el reporte.");
            model.addAttribute("username", "");
            return "reports/select";
        }
        var report = reportService.generateExerciseTypeReport(username);
        model.addAttribute("username", username);
        model.addAttribute("report", report);
        return "reports/exerciseTypeReport";
    }
}
