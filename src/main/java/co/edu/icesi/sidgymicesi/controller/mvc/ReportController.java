package co.edu.icesi.sidgymicesi.controller.mvc;

import co.edu.icesi.sidgymicesi.services.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mvc/reports")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReportController {

    private final IReportService reportService;

    @GetMapping
    public String select(@RequestParam(name = "username", required = false) String username,
                         @RequestParam(name = "numberOfWeeks", required = false) Integer numberOfWeeks,
                         Authentication authentication,
                         Model model) {

        if (authentication == null) {
            return "redirect:/mvc/public/auth/login";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        String effectiveUsername;

        if (isAdmin && StringUtils.hasText(username)) {
            effectiveUsername = username.trim();
        } else {
            effectiveUsername = authentication.getName();
        }

        if (numberOfWeeks == null || numberOfWeeks <= 0) {
            numberOfWeeks = 8;
        }

        model.addAttribute("username", effectiveUsername);          // usuario "objetivo" para el reporte
        model.addAttribute("currentUsername", authentication.getName()); // usuario logueado
        model.addAttribute("numberOfWeeks", numberOfWeeks);

        return "reports/select";
    }

    @PostMapping("/consistency")
    public String generateConsistencyReport(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "numberOfWeeks", defaultValue = "8") int numberOfWeeks,
            Authentication authentication,
            Model model) {

        if (authentication == null) {
            return "redirect:/mvc/public/auth/login";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        String targetUsername;

        if (isAdmin) {
            // Para admin, el username es obligatorio
            if (!StringUtils.hasText(username)) {
                model.addAttribute("error", "Debes seleccionar un usuario para generar el reporte.");
                model.addAttribute("username", authentication.getName());
                model.addAttribute("currentUsername", authentication.getName());
                model.addAttribute("numberOfWeeks", numberOfWeeks);
                return "reports/select";
            }
            targetUsername = username.trim();
        } else {
            targetUsername = authentication.getName();
        }

        List<Map<String, Object>> reportList = reportService.generateConsistencyReport(targetUsername, numberOfWeeks);
        Map<String, Object> report = reportList.isEmpty() ? Map.of() : reportList.get(0);

        model.addAttribute("username", targetUsername);
        model.addAttribute("numberOfWeeks", numberOfWeeks);
        model.addAttribute("report", report);

        return "reports/consistencyReport";
    }

    @PostMapping("/exercise-type")
    public String generateExerciseTypeReport(
            @RequestParam(name = "username", required = false) String username,
            Authentication authentication,
            Model model) {

        if (authentication == null) {
            return "redirect:/mvc/public/auth/login";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        String targetUsername;

        if (isAdmin) {
            if (!StringUtils.hasText(username)) {
                model.addAttribute("error", "Debes seleccionar un usuario para generar el reporte.");
                model.addAttribute("username", authentication.getName());
                model.addAttribute("currentUsername", authentication.getName());
                model.addAttribute("numberOfWeeks", 8);
                return "reports/select";
            }
            targetUsername = username.trim();
        } else {
            targetUsername = authentication.getName();
        }

        Map<String, Long> report = reportService.generateExerciseTypeReport(targetUsername);

        model.addAttribute("username", targetUsername);
        model.addAttribute("report", report);

        return "reports/exerciseTypeReport";
    }
}
