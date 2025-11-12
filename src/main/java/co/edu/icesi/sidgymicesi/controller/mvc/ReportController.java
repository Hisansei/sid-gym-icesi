package co.edu.icesi.sidgymicesi.controller.mvc;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import co.edu.icesi.sidgymicesi.services.IReportService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/mvc/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    @GetMapping("/select")
    public String selectReport(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        return "reports/select";
    }

    @PostMapping("/generateConsistencyReport")
    public String generateConsistencyReport(@RequestParam String username, @RequestParam int numberOfWeeks, Model model) {
        var report = reportService.generateConsistencyReport(username, numberOfWeeks);
        model.addAttribute("report", report);
        return "reports/consistencyReport";
    }

    @PostMapping("/generateExerciseTypeReport")
    public String generateExerciseTypeReport(@RequestParam String username, Model model) {
        var report = reportService.generateExerciseTypeReport(username);
        model.addAttribute("report", report);
        return "reports/exerciseTypeReport";
    }

}