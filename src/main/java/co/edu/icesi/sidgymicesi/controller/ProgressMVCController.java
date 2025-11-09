package co.edu.icesi.sidgymicesi.controller;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.IProgressService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import co.edu.icesi.sidgymicesi.util.DemoCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/mvc/progress")
@RequiredArgsConstructor
public class ProgressMVCController {

    private final IRoutineService routineService;
    private final IProgressService progressService;

    @GetMapping("/{routineId}")
    public String logForm(@PathVariable String routineId, Model model) {
        Routine routine = routineService.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        model.addAttribute("routine", routine);
        model.addAttribute("today", LocalDate.now());
        return "progress/log";
    }

    @PostMapping("/{routineId}")
    public String submitLog(@PathVariable String routineId,
                            @RequestParam Map<String, String> form) {

        Routine routine = routineService.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        List<ProgressLog.Entry> entries = new ArrayList<>();

        for (Routine.RoutineExercise it : routine.getExercises()) {
            String key = it.getId(); // id del item dentro de la rutina del usuario

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
                    // el formulario captura un único valor; lo guardamos como lista de un elemento
                    .reps(reps != null ? List.of(reps) : null)
                    .sets(null)
                    .weightKg(null)
                    .effortLevel(rpe)
                    .notesUser(notes)
                    .build();

            entries.add(entry);
        }

        ProgressLog log = ProgressLog.builder()
                .ownerUsername(DemoCurrentUser.username())
                .routineId(routineId)
                .date(LocalDate.now())
                .entries(entries)
                .createdAt(Instant.now())
                .build();

        progressService.addLog(log);
        return "redirect:/mvc/routines/" + routineId;
    }

    private Integer parseInt(String v) {
        try {
            return (v == null || v.isBlank()) ? null : Integer.valueOf(v.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
