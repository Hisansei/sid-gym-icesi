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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
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
        String trainerUsername = authentication.getName();

        // Buscamos por ID de empleado (lo estándar) y por username (para datos legados/seeds)
        List<TrainerAssignment> byId = trainerAssignmentService.listByTrainer(trainerId);
        List<TrainerAssignment> byUsername = trainerAssignmentService.listByTrainer(trainerUsername);

        // Unificamos listas para evitar duplicados si el ID y username coincidieran
        Set<TrainerAssignment> combined = new HashSet<>();
        if (byId != null) combined.addAll(byId);
        if (byUsername != null) combined.addAll(byUsername);

        List<TrainerAssignment> assignments = combined.stream()
                .filter(Objects::nonNull)
                .filter(TrainerAssignment::isActive)
                // Ordenar por fecha de asignación descendente
                .sorted(Comparator.comparing(TrainerAssignment::getAssignedAt, Comparator.nullsLast(Comparator.reverseOrder())))
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

        // Validamos seguridad: permitimos si el usuario está asignado al ID o al Username del entrenador
        assertHasActiveAssignment(authentication, username);

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

        assertHasActiveAssignment(authentication, username);

        List<ProgressLog> logs = progressLogService.listByOwner(username);

        Set<String> trainerIds = new HashSet<>();
        for (ProgressLog log : logs) {
            if (log.getTrainerFeedback() != null) {
                // Obtenemos todos los IDs/Nombres de entrenadores únicos presentes en el feedback
                log.getTrainerFeedback().forEach(fb -> trainerIds.add(fb.getTrainerId()));
            }
        }

        Map<String, String> trainerNamesMap = new HashMap<>();
        for (String id : trainerIds) {
            trainerNamesMap.put(id, id);
        }

        model.addAttribute("trainerNames", trainerNamesMap);

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

    private void assertHasActiveAssignment(Authentication authentication, String username) {
        String trainerId = currentTrainerId(authentication);
        String trainerUsername = authentication.getName();

        List<TrainerAssignment> byId = trainerAssignmentService.listByTrainer(trainerId);
        List<TrainerAssignment> byName = trainerAssignmentService.listByTrainer(trainerUsername);

        boolean hasActiveById = byId != null && byId.stream()
                .filter(TrainerAssignment::isActive)
                .anyMatch(a -> username.equalsIgnoreCase(a.getUserUsername()));

        boolean hasActiveByName = byName != null && byName.stream()
                .filter(TrainerAssignment::isActive)
                .anyMatch(a -> username.equalsIgnoreCase(a.getUserUsername()));

        if (!hasActiveById && !hasActiveByName) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes una asignación activa sobre el usuario " + username);
        }
    }

    @PostMapping("/progress/{logId}/feedback")
    public String addFeedback(@PathVariable("logId") String logId,
                              @RequestParam("username") String username, // Para saber a dónde volver
                              @RequestParam("message") String message,
                              Authentication authentication) {

        // Validamos seguridad
        assertHasActiveAssignment(authentication, username);

        // Guardamos el Username/ID del entrenador
        String trainerUsername = authentication.getName();

        if (message != null && !message.isBlank()) {
            progressLogService.addFeedback(logId, trainerUsername, message.trim());
        }

        // Redirigir de vuelta a la lista de progresos de ese usuario
        return "redirect:/mvc/trainer/users/" + username + "/progress";
    }
}