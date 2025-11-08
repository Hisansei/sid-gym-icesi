package co.edu.icesi.sidgymicesi.controller;

import co.edu.icesi.sidgymicesi.model.*;
import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.*;
import co.edu.icesi.sidgymicesi.util.DemoCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/mvc/progress")
@RequiredArgsConstructor
public class ProgressMVCController {

    private final IRoutineService routineService;
    private final IProgressService progressService;
    private final DemoCurrentUser currentUser;

    @GetMapping("/log")
    public String logForm(@RequestParam String routineId, Model model) {
        Routine r = routineService.findById(routineId).orElse(null);
        model.addAttribute("routine", r);
        model.addAttribute("today", LocalDate.now());
        return "progress/log";
    }

    @PostMapping("/log")
    public String submitLog(@RequestParam String routineId,
                            @RequestParam("date") String date,
                            @RequestParam Map<String,String> form,
                            RedirectAttributes ra) {

        Routine r = routineService.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no existe"));

        List<ProgressEntry> entries = new ArrayList<>();
        for (Routine.RoutineItem it : r.getItems()) {
            Integer reps = parseInt(form.get("reps_" + it.getId()));
            Integer secs = parseInt(form.get("time_" + it.getId()));
            Integer rpe  = parseInt(form.get("rpe_"  + it.getId()));
            String notes = form.getOrDefault("notes_" + it.getId(), null);

            if (reps!=null || secs!=null || rpe!=null || (notes!=null && !notes.isBlank())) {
                entries.add(ProgressEntry.builder()
                        .itemId(it.getId())
                        .exerciseId(it.getExerciseId())
                        .repsDone(reps)
                        .timeSeconds(secs)
                        .effortRpe(rpe)
                        .notes(notes)
                        .build());
            }
        }

        ProgressLog log = ProgressLog.builder()
                .ownerUsername(currentUser.username())
                .routineId(routineId)
                .date(LocalDate.parse(date))
                .entries(entries)
                .build();

        progressService.addLog(log);
        ra.addFlashAttribute("message", "Progreso registrado.");
        return "redirect:/mvc/progress/history?routineId=" + routineId;
    }

    @GetMapping("/history")
    public String history(@RequestParam String routineId, Model model) {
        Routine r = routineService.findById(routineId).orElse(null);
        model.addAttribute("routine", r);
        model.addAttribute("logs", progressService.listByRoutine(routineId));
        return "progress/history";
    }

    private Integer parseInt(String v) {
        try { return (v==null || v.isBlank()) ? null : Integer.parseInt(v); }
        catch (NumberFormatException e) { return null; }
    }
}
