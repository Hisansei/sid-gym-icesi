package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.services.mongo.ITrainerAssignmentService;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mvc/admin/assignments")
@RequiredArgsConstructor
public class TrainerAssignmentMVCController {
    private final ITrainerAssignmentService assignmentService;
    private final ITrainerStatsService trainerStatsService;

    // ====================== LIST / HOME ======================

    @GetMapping
    public String listPage(
            @RequestParam(value = "user", required = false) String userUsername,
            Model model
    ) {
        // Listas principales
        List<TrainerAssignment> active = assignmentService.listActive();
        List<TrainerAssignment> history = assignmentService.listAll();

        // Estadísticas por entrenador
        Map<String, List<TrainerMonthlyStat>> statsByTrainer = trainerStatsService.listAll();

        model.addAttribute("active", active);
        model.addAttribute("history", history);
        model.addAttribute("statsByTrainer", statsByTrainer);

        if (StringUtils.hasText(userUsername)) {
            model.addAttribute("selectedUser", userUsername);
            model.addAttribute("userHistory", assignmentService.listHistoryByUser(userUsername));
            assignmentService.findActiveByUser(userUsername)
                    .ifPresent(a -> model.addAttribute("userActiveAssignment", a));
        }

        model.addAttribute("usersQuery", "");
        model.addAttribute("trainersQuery", "");

        return "admin/assignments/list";
    }

    // =================== FORM NUEVA ASIGNACIÓN ===================
    @GetMapping("/new")
    public String newPage(Model model) {
        // (Opcional) precargar listas de usuarios/entrenadores aquí si luego haces selects
        return "admin/assignments/new";
    }

    // ====================== CREATE (ASSIGN) ======================
    @PostMapping
    public String assign(
            @RequestParam("userUsername") @NotBlank String userUsername,
            @RequestParam("trainerUsername") @NotBlank String trainerUsername,
            RedirectAttributes ra
    ) {
        try {
            assignmentService.assign(userUsername, trainerUsername);
            ra.addFlashAttribute("msg_success",
                    "Entrenador " + trainerUsername + " asignado a " + userUsername + " correctamente.");
        } catch (IllegalStateException ise) {
            ra.addFlashAttribute("msg_warn", ise.getMessage());
        } catch (IllegalArgumentException iae) {
            ra.addFlashAttribute("msg_error", iae.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg_error", "No se pudo realizar la asignación. " + e.getMessage());
        }

        // Volvemos al listado y resaltamos al usuario
        ra.addAttribute("user", userUsername);
        return "redirect:/mvc/admin/assignments";
    }

    // ====================== FORM REASIGNAR ======================
    @GetMapping("/reassign")
    public String reassignPage(
            @RequestParam("userUsername") @NotBlank String userUsername,
            Model model
    ) {
        model.addAttribute("userUsername", userUsername);
        assignmentService.findActiveByUser(userUsername)
                .ifPresent(current -> model.addAttribute("current", current));
        return "admin/assignments/reassign";
    }

    // ====================== REASSIGN ======================
    @PostMapping("/reassign")
    public String reassign(
            @RequestParam("userUsername") @NotBlank String userUsername,
            @RequestParam("newTrainerUsername") @NotBlank String newTrainerUsername,
            RedirectAttributes ra
    ) {
        try {
            assignmentService.reassign(userUsername, newTrainerUsername);
            ra.addFlashAttribute("msg_success",
                    "Usuario " + userUsername + " reasignado a entrenador " + newTrainerUsername + " correctamente.");
        } catch (IllegalStateException ise) {
            ra.addFlashAttribute("msg_warn", ise.getMessage());
        } catch (IllegalArgumentException iae) {
            ra.addFlashAttribute("msg_error", iae.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg_error", "No se pudo realizar la reasignación. " + e.getMessage());
        }

        // Volvemos al listado y dejamos seleccionado al usuario
        ra.addAttribute("user", userUsername);
        return "redirect:/mvc/admin/assignments/reassign?user=" + userUsername;

    }

    // ====================== CLOSE (no requiere vista) ======================
    @PostMapping("/close")
    public String close(
            @RequestParam("assignmentId") @NotBlank String assignmentId,
            RedirectAttributes ra
    ) {
        try {
            assignmentService.closeAssignment(assignmentId);
            ra.addFlashAttribute("msg_success", "Asignación cerrada correctamente.");
        } catch (IllegalArgumentException iae) {
            ra.addFlashAttribute("msg_error", iae.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg_error", "No se pudo cerrar la asignación. " + e.getMessage());
        }
        return "redirect:/mvc/admin/assignments";
    }
}