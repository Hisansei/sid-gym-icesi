package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import co.edu.icesi.sidgymicesi.services.mongo.ITrainerAssignmentService;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mvc/admin/assignments")
@RequiredArgsConstructor
public class TrainerAssignmentMVCController {

    private final ITrainerAssignmentService assignmentService;
    private final ITrainerStatsService statsService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", assignmentService.listActive());
        model.addAttribute("history", assignmentService.listAll());
        model.addAttribute("statsByTrainer", statsService.listAll());
        return "admin/assignments/list";
    }

    @GetMapping("/new")
    public String newForm() { return "admin/assignments/new"; }

    @PostMapping
    public String create(@RequestParam String userUsername,
                         @RequestParam String trainerUsername,
                         RedirectAttributes ra) {
        TrainerAssignment a = assignmentService.assign(userUsername, trainerUsername);
        ra.addFlashAttribute("message",
                "Asignado " + userUsername + " → " + trainerUsername);
        return "redirect:/mvc/admin/assignments";
    }

    @GetMapping("/reassign")
    public String reassignForm(@RequestParam String userUsername, Model model) {
        model.addAttribute("userUsername", userUsername);
        model.addAttribute("current",
                assignmentService.findActiveByUser(userUsername).orElse(null));
        return "admin/assignments/reassign";
    }

    @PostMapping("/reassign")
    public String reassign(@RequestParam String userUsername,
                           @RequestParam String newTrainerUsername,
                           RedirectAttributes ra) {
        assignmentService.reassign(userUsername, newTrainerUsername);
        ra.addFlashAttribute("message",
                "Reasignado " + userUsername + " → " + newTrainerUsername);
        return "redirect:/mvc/admin/assignments";
    }

    @PostMapping("/close")
    public String close(@RequestParam String id, RedirectAttributes ra) {
        assignmentService.closeAssignment(id);
        ra.addFlashAttribute("message", "Asignación cerrada");
        return "redirect:/mvc/admin/assignments";
    }
}
