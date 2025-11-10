package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/mvc/progress")
@RequiredArgsConstructor
public class ProgressLogMVCController {

    private final IRoutineService routineService;
    private final IProgressLogService progressService;
    private final IExerciseService exerciseService;

    // Formulario por query param: /mvc/progress/log?routineId=...
    @GetMapping("/log")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public String logFormByParam(@RequestParam("routineId") String routineId, Model model) {
        return showLogForm(routineId, model);
    }

    // Formulario por path: /mvc/progress/{routineId}
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
        // Si tu vista usa catálogo para mostrar nombres, mantenlo:
        model.addAttribute("catalog", exerciseService.findAll());
        return "progress/log";
    }

    // Compatibilidad POST por query param
    @PostMapping("/log")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public String submitLogByParam(Authentication auth,
                                   @RequestParam("routineId") String routineId,
                                   @RequestParam Map<String, String> form) {
        return doSubmitLog(auth, routineId, form);
    }

    // POST por path: /mvc/progress/{routineId}
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
            String key = it.getId();
            Integer reps = parseInt(form.get("reps_" + key));          // ej: "10"
            Integer secs = parseInt(form.get("time_" + key));          // ej: "300" (no se persiste en el modelo actual)
            String rpe = form.get("rpe_" + key);                       // ej: "7"
            String notes = form.getOrDefault("notes_" + key, "").trim();

            boolean completed = (reps != null && reps > 0)
                    || (secs != null && secs > 0)
                    || (rpe != null && !rpe.isBlank())
                    || (!notes.isBlank());

            ProgressLog.Entry entry = ProgressLog.Entry.builder()
                    .exerciseId(it.getExerciseId())
                    .completed(completed)
                    // el modelo guarda listas; si el formulario trae un valor simple lo guardamos como lista de 1
                    .reps(reps != null ? List.of(reps) : null)
                    .sets(null)
                    .weightKg(null)
                    .effortLevel(rpe)
                    .notesUser(notes)
                    .build();

            entries.add(entry);
        }

        ProgressLog log = ProgressLog.builder()
                .ownerUsername(auth.getName())
                .routineId(routineId)
                .date(LocalDate.now())
                .entries(entries)
                .createdAt(Instant.now())
                .build();

        progressService.addLog(log);
        // Lleva al histórico simple de la rutina
        return "redirect:/mvc/progress/history?routineId=" + routineId;
    }

    // Histórico básico: /mvc/progress/history?routineId=...
    @GetMapping("/history")
    @PreAuthorize("@authz.isOwnerOfRoutine(#routineId, authentication)")
    public String history(@RequestParam("routineId") String routineId, Model model) {
        Routine routine = routineService.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada"));
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
