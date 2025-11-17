package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import co.edu.icesi.sidgymicesi.security.CustomUserDetails;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import co.edu.icesi.sidgymicesi.services.mongo.ITrainerAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mvc/trainer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE') and principal.employeeType == 'Instructor'")
public class TrainerDashboardMVCController {

    private final ITrainerAssignmentService trainerAssignmentService;
    private final IRoutineService routineService;
    private final IProgressLogService progressLogService;

    /**
     * Panel principal del entrenador: lista de usuarios que tiene asignados (solo asignaciones activas).
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String trainerId = currentTrainerId(authentication);

        List<TrainerAssignment> assignments = trainerAssignmentService
                .listByTrainer(trainerId)
                .stream()
                .filter(Objects::nonNull)
                .filter(TrainerAssignment::isActive)
                .collect(Collectors.toList());

        model.addAttribute("assignments", assignments);
        return "trainer/dashboard";
    }

    /**
     * Rutinas de un usuario asignado al entrenador.
     */
    @GetMapping("/users/{username}/routines")
    public String userRoutines(@PathVariable("username") String username,
                               Authentication authentication,
                               Model model) {

        String trainerId = currentTrainerId(authentication);
        assertHasActiveAssignment(trainerId, username);

        List<Routine> routines = routineService.listByOwner(username);

        model.addAttribute("username", username);
        model.addAttribute("routines", routines);
        return "trainer/user-routines";
    }

    /**
     * Progreso (logs) de un usuario asignado al entrenador.
     */
    @GetMapping("/users/{username}/progress")
    public String userProgress(@PathVariable("username") String username,
                               Authentication authentication,
                               Model model) {

        String trainerId = currentTrainerId(authentication);
        assertHasActiveAssignment(trainerId, username);

        List<ProgressLog> logs = progressLogService.listByOwner(username);

        model.addAttribute("username", username);
        model.addAttribute("logs", logs);
        return "trainer/user-progress";
    }

    // ---------------------------------------
    // Helpers
    // ---------------------------------------


    private String currentTrainerId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autenticado");
        }
        if (details.getUser() == null || details.getUser().getEmployeeId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario actual no tiene empleado asociado");
        }
        return details.getUser().getEmployeeId();
    }


    private void assertHasActiveAssignment(String trainerId, String username) {
        boolean hasActive = trainerAssignmentService
                .listByTrainer(trainerId)
                .stream()
                .filter(TrainerAssignment::isActive)
                .anyMatch(a -> username.equalsIgnoreCase(a.getUserUsername()));

        if (!hasActive) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes una asignación activa sobre el usuario " + username);
        }
    }
}
