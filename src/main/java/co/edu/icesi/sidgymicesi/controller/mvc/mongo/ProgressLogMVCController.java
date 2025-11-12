package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mvc/progress")
public class ProgressLogMVCController {

    private final IRoutineService routineService;
    private final IProgressLogService progressService;
    private final IExerciseService exerciseService;

    public ProgressLogMVCController(IRoutineService routineService,
                                    IProgressLogService progressService,
                                    IExerciseService exerciseService) {
        this.routineService = routineService;
        this.progressService = progressService;
        this.exerciseService = exerciseService;
    }

    // Selección de rutina si no viene routineId (evita 400)
    @GetMapping(value = "/history", params = "!routineId")
    @PreAuthorize("isAuthenticated()")
    public String historySelector(Authentication auth, Model model) {
        List<Routine> routines = routineService.listByOwner(auth.getName());
        model.addAttribute("routines", routines);
        return "progress/history-select";
    }

    // Historial por rutina
    @GetMapping(value = "/history", params = "routineId")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public String history(@RequestParam("routineId") String routineId, Model model) {
        Routine routine = routineService.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada"));

        model.addAttribute("routine", routine);
        model.addAttribute("logs", progressService.listByRoutine(routineId));

        Map<String, Exercise> exerciseMap = exerciseService.findAll()
                .stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));
        model.addAttribute("exerciseMap", exerciseMap);

        return "progress/history";
    }

    // Form por query param
    @GetMapping("/log")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public String logFormByParam(@RequestParam("routineId") String routineId, Model model) {
        return showLogForm(routineId, model);
    }

    // Form por path
    @GetMapping("/{routineId}")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public String logForm(@PathVariable String routineId, Model model) {
        return showLogForm(routineId, model);
    }

    private String showLogForm(String routineId, Model model) {
        Routine routine = routineService.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada"));
        model.addAttribute("routine", routine);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("catalog", exerciseService.findAll());
        return "progress/log";
    }

    // POST por query param
    @PostMapping("/log")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public String submitLogByParam(Authentication auth,
                                   @RequestParam("routineId") String routineId,
                                   @RequestParam Map<String, String> form) {
        return doSubmitLog(auth, routineId, form);
    }

    // POST por path
    @PostMapping("/{routineId}")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public String submitLog(Authentication auth,
                            @PathVariable String routineId,
                            @RequestParam Map<String, String> form) {
        return doSubmitLog(auth, routineId, form);
    }

    private String doSubmitLog(Authentication auth, String routineId, Map<String, String> form) {
        Routine routine = routineService.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada"));

        List<ProgressLog.Entry> entries = new ArrayList<>();

        for (Routine.RoutineExercise it : routine.getExercises()) {
            String key = it.getId(); // id del item dentro de la rutina
            Integer reps = parseInt(form.get("reps_" + key));
            Integer secs = parseInt(form.get("time_" + key)); // usamos solo para 'completed'
            String rpe = form.get("rpe_" + key);
            String notes = form.getOrDefault("notes_" + key, "").trim();

            boolean completed = (reps != null && reps > 0)
                    || (secs != null && secs > 0)
                    || (rpe != null && !rpe.isBlank())
                    || (!notes.isBlank());

            ProgressLog.Entry e = new ProgressLog.Entry();
            e.setExerciseId(it.getExerciseId());
            e.setCompleted(completed);
            e.setReps(reps != null ? List.of(reps) : null); // List<Integer>
            e.setSets(null);
            e.setWeightKg(null);
            e.setEffortLevel(rpe);
            e.setNotesUser(notes);

            entries.add(e);
        }

        if (entries.isEmpty()) {
            return "redirect:/mvc/progress/" + routineId;
        }

        ProgressLog log = new ProgressLog();
        log.setOwnerUsername(auth.getName());
        log.setRoutineId(routineId);
        log.setDate(LocalDate.now());
        log.setEntries(entries);
        log.setCreatedAt(Instant.now());

        progressService.addLog(log);
        return "redirect:/mvc/progress/history?routineId=" + routineId;
    }

    private Integer parseInt(String v) {
        try {
            return (v == null || v.isBlank()) ? null : Integer.valueOf(v.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
