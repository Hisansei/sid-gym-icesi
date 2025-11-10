package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogService;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/mvc/progress")
@RequiredArgsConstructor
public class ProgressLogMVCController {

    private final IRoutineService routineService;
    private final IProgressLogService progressService;
    private final IExerciseService exerciseService;

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No hay usuario autenticado.");
        }
        return auth.getName();
    }

    // Compatibilidad con la vista: GET /mvc/progress/log?routineId=...
    @GetMapping("/log")
    public String logFormByParam(@RequestParam("routineId") String routineId, Model model) {
        return logForm(routineId, model);
    }

    @GetMapping("/{routineId}")
    public String logForm(@PathVariable String routineId, Model model) {
        Routine routine = routineService.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        model.addAttribute("routine", routine);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("catalog", exerciseService.findAll()); // la vista usa #vars.catalog
        return "progress/log";
    }

    // Compatibilidad con form action="/mvc/progress/log"
    @PostMapping("/log")
    public String submitLogByParam(@RequestParam("routineId") String routineId,
                                   @RequestParam Map<String, String> form) {
        return submitLog(routineId, form);
    }

    @PostMapping("/{routineId}")
    public String submitLog(@PathVariable String routineId,
                            @RequestParam Map<String, String> form) {

        Routine routine = routineService.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        List<ProgressLog.Entry> entries = new ArrayList<>();

        for (Routine.RoutineExercise it : routine.getExercises()) {
            String key = it.getId(); // id del item en la rutina

            Integer reps = parseInt(form.get("reps_" + key));
            Integer secs = parseInt(form.get("time_" + key));
            String rpe = form.get("rpe_" + key);
            String notes = form.getOrDefault("notes_" + key, "");

            boolean completed = (reps != null && reps > 0)
                    || (secs != null && secs > 0)
                    || (rpe != null && !rpe.isBlank())
                    || (notes != null && !notes.isBlank());

            ProgressLog.Entry entry = ProgressLog.Entry.builder()
                    .exerciseId(it.getExerciseId())
                    .completed(completed)
                    // El formulario captura un valor simple; lo guardamos como lista de un elemento
                    .reps(reps != null ? List.of(reps) : null)
                    .sets(null)
                    .weightKg(null)
                    .effortLevel(rpe)
                    .notesUser(notes)
                    .build();

            entries.add(entry);
        }

        ProgressLog log = ProgressLog.builder()
                .ownerUsername(currentUsername())
                .routineId(routineId)
                .date(LocalDate.now())
                .entries(entries)
                .createdAt(Instant.now())
                .build();

        progressService.addLog(log);
        return "redirect:/mvc/routines/" + routineId;
    }

    // Histórico de una rutina: /mvc/progress/history?routineId=...
    @GetMapping("/history")
    public String history(@RequestParam("routineId") String routineId, Model model) {
        Routine routine = routineService.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        model.addAttribute("routine", routine);
        model.addAttribute("logs", progressService.listByRoutine(routineId));
        return "progress/history";
    }

    private Integer parseInt(String v) {
        try {
            return (v == null || v.isBlank()) ? null : Integer.valueOf(v.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
