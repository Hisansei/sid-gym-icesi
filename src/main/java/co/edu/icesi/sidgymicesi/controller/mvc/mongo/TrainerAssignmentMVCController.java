package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.services.mongo.ITrainerAssignmentService;
import co.edu.icesi.sidgymicesi.services.postgres.IEmployeeService;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/mvc/admin/assignments")
@RequiredArgsConstructor
public class TrainerAssignmentMVCController {

    private final ITrainerAssignmentService assignmentService;
    private final ITrainerStatsService trainerStatsService;
    private final IEmployeeService employeeService;

    // ====================== LIST / HOME =========================
    @GetMapping
    public String listPage(@RequestParam(value = "user", required = false) String userUsername,
                           Model model) {

        List<TrainerAssignment> active = assignmentService.listActive();
        List<TrainerAssignment> history = assignmentService.listAll();

        // Mapa id -> "Nombre Apellido"
        Set<String> ids = new HashSet<>();
        active.forEach(a -> ids.add(a.getTrainerId()));
        history.forEach(h -> ids.add(h.getTrainerId()));

        Map<String, String> trainerNames = new HashMap<>();
        for (String id : ids) {
            employeeService.findById(id).ifPresent(e ->
                    trainerNames.put(id, e.getFirstName() + " " + e.getLastName()));
        }

        model.addAttribute("active", active);
        model.addAttribute("history", history);
        model.addAttribute("statsByTrainer", trainerStatsService.listAll());
        model.addAttribute("trainerNames", trainerNames);

        // Si viene un usuario seleccionado (?user=)
        if (StringUtils.hasText(userUsername)) {
            model.addAttribute("selectedUser", userUsername);
            model.addAttribute("userHistory", assignmentService.listHistoryByUser(userUsername));
            assignmentService.findActiveByUser(userUsername)
                    .ifPresent(a -> model.addAttribute("userActiveAssignment", a));
        }

        return "admin/assignments/list";
    }

    // ================= FORM NUEVA ASIGNACIÓN ====================
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("trainers", employeeService.findAll());
        return "admin/assignments/new";
    }

    // ====================== CREATE (ASSIGN) =====================
    @PostMapping
    public String assign(@RequestParam("userUsername") @NotBlank String userUsername,
                         @RequestParam("trainerId") @NotBlank String trainerId,
                         RedirectAttributes ra) {
        try {
            assignmentService.assign(userUsername, trainerId);
            ra.addFlashAttribute("msg_success",
                    "Entrenador " + trainerId + " asignado a " + userUsername + " correctamente.");
        } catch (IllegalStateException ise) {
            ra.addFlashAttribute("msg_warn", ise.getMessage());
        } catch (IllegalArgumentException iae) {
            ra.addFlashAttribute("msg_error", iae.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg_error", "No se pudo realizar la asignación. " + e.getMessage());
        }

        ra.addAttribute("user", userUsername);
        return "redirect:/mvc/admin/assignments";
    }

    // ==================== FORM REASIGNAR ========================
    @GetMapping("/reassign")
    public String reassignPage(@RequestParam("userUsername") String userUsername, Model model) {
        model.addAttribute("userUsername", userUsername);
        assignmentService.findActiveByUser(userUsername)
                .ifPresent(a -> model.addAttribute("current", a));

        // Agregamos nombres de entrenadores para mostrar "Nombre (ID)"
        Map<String, String> trainerNames = new HashMap<>();
        assignmentService.findActiveByUser(userUsername).ifPresent(a ->
                employeeService.findById(a.getTrainerId()).ifPresent(e ->
                        trainerNames.put(e.getId(), e.getFirstName() + " " + e.getLastName())
                )
        );
        model.addAttribute("trainerNames", trainerNames);
        model.addAttribute("trainers", employeeService.findAll());

        return "admin/assignments/reassign";
    }

    // ======================== REASSIGN ==========================
    @PostMapping("/reassign")
    public String reassign(@RequestParam("userUsername") @NotBlank String userUsername,
                           @RequestParam("newTrainerId") @NotBlank String newTrainerId,
                           RedirectAttributes ra) {
        try {
            assignmentService.reassign(userUsername, newTrainerId);
            ra.addFlashAttribute("msg_success",
                    "Usuario " + userUsername + " reasignado a entrenador " + newTrainerId + " correctamente.");
        } catch (IllegalStateException ise) {
            ra.addFlashAttribute("msg_warn", ise.getMessage());
        } catch (IllegalArgumentException iae) {
            ra.addFlashAttribute("msg_error", iae.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg_error", "No se pudo realizar la reasignación. " + e.getMessage());
        }

        ra.addAttribute("user", userUsername);
        return "redirect:/mvc/admin/assignments";
    }

    // ========================== CLOSE ===========================
    @PostMapping("/close")
    public String close(@RequestParam("assignmentId") @NotBlank String assignmentId,
                        RedirectAttributes ra) {
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
