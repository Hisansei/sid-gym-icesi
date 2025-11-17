package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import co.edu.icesi.sidgymicesi.services.IUserService;
import co.edu.icesi.sidgymicesi.services.mongo.ITrainerAssignmentService;
import co.edu.icesi.sidgymicesi.services.postgres.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;


@Controller
@RequestMapping("/mvc/admin/assignments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TrainerAssignmentMVCController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerAssignmentMVCController.class);

    private final ITrainerAssignmentService trainerAssignmentService;
    private final IUserService userService;
    private final IEmployeeService employeeService;
    private final ITrainerStatsService trainerStatsService;


    // ====================== LIST / HOME =========================

    @GetMapping
    public String list(
            @RequestParam(value = "userUsername", required = false) String userUsername,
            @RequestParam(value = "trainerId", required = false) String trainerId,
            Model model) {

        List<TrainerAssignment> all = trainerAssignmentService.listAll();

        if (userUsername != null && !userUsername.isBlank()) {
            all = all.stream()
                    .filter(a -> userUsername.equalsIgnoreCase(a.getUserUsername()))
                    .toList();
            model.addAttribute("filterUserUsername", userUsername);
        }

        if (trainerId != null && !trainerId.isBlank()) {
            all = all.stream()
                    .filter(a -> trainerId.equalsIgnoreCase(a.getTrainerId()))
                    .toList();
            model.addAttribute("filterTrainerId", trainerId);
        }

        List<TrainerAssignment> active = all.stream()
                .filter(TrainerAssignment::isActive)
                .toList();

        List<TrainerAssignment> history = all;

        Map<String, String> trainerNames = employeeService.findAllInstructors()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getId().toString(),
                        e -> e.getFirstName() + " " + e.getLastName()
                ));

        Map<String, List<TrainerMonthlyStat>> statsMap = trainerStatsService.listAll();

        List<TrainerMonthlyStat> statsByTrainer = new ArrayList<>();
        statsMap.values().forEach(statsByTrainer::addAll);

        model.addAttribute("active", active);
        model.addAttribute("history", history);
        model.addAttribute("trainerNames", trainerNames);
        model.addAttribute("statsByTrainer", statsByTrainer);

        return "admin/assignments/list";
    }

    // ====================== CREATE =========================

    @GetMapping("/create")
    public String createForm(@RequestParam(value = "userUsername", required = false) String userUsername,
                             Model model) {

        model.addAttribute("users", userService.findAllStudents());
        model.addAttribute("trainers", employeeService.findAllInstructors());

        TrainerAssignment assignment = new TrainerAssignment();
        if (userUsername != null && !userUsername.isBlank()) {
            assignment.setUserUsername(userUsername);
        }

        model.addAttribute("assignment", assignment);
        return "admin/assignments/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("assignment") TrainerAssignment assignment, Model model) {

        if (assignment.getUserUsername() == null || assignment.getUserUsername().isBlank()) {
            model.addAttribute("error", "Debe seleccionar un usuario.");
            model.addAttribute("users", userService.findAllStudents());
            model.addAttribute("trainers", employeeService.findAllInstructors());
            return "admin/assignments/form";
        }

        if (assignment.getTrainerId() == null || assignment.getTrainerId().isBlank()) {
            model.addAttribute("error", "Debe seleccionar un entrenador.");
            model.addAttribute("users", userService.findAllStudents());
            model.addAttribute("trainers", employeeService.findAllInstructors());
            return "admin/assignments/form";
        }

        // El servicio ya se encarga de cerrar asignaciones previas y poner fechas
        trainerAssignmentService.assign(
                assignment.getUserUsername(),
                assignment.getTrainerId()
        );

        return "redirect:/mvc/admin/assignments";
    }

    // ====================== DETAIL =========================

    @GetMapping("/detail")
    public String detail(@RequestParam("id") String id, Model model) {
        Optional<TrainerAssignment> opt = trainerAssignmentService.findById(id);
        if (opt.isEmpty()) {
            throw new NoSuchElementException("Asignación no encontrada: " + id);
        }
        model.addAttribute("assignment", opt.get());
        return "admin/assignments/detail";
    }

    // ====================== CLOSE / REASSIGN =========================

    @PostMapping("/close")
    public String close(@RequestParam("id") String id) {
        trainerAssignmentService.closeAssignment(id);
        return "redirect:/mvc/admin/assignments";
    }

    @GetMapping("/reassign")
    public String reassignForm(@RequestParam("id") String id, Model model) {
        TrainerAssignment assignment = trainerAssignmentService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Asignación no encontrada: " + id));

        model.addAttribute("assignment", assignment);
        model.addAttribute("trainers", employeeService.findAllInstructors());
        return "admin/assignments/reassign";
    }

    @PostMapping("/reassign")
    public String reassign(@RequestParam("id") String id,
                           @RequestParam("newTrainerId") String newTrainerId) {

        TrainerAssignment assignment = trainerAssignmentService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Asignación no encontrada: " + id));

        trainerAssignmentService.reassign(assignment.getUserUsername(), newTrainerId);
        return "redirect:/mvc/admin/assignments";
    }

}
