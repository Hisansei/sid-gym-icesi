package co.edu.icesi.sidgymicesi.controller;

import co.edu.icesi.sidgymicesi.model.*;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.*;
import co.edu.icesi.sidgymicesi.util.DemoCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/mvc/routines")
@RequiredArgsConstructor
public class RoutineMVCController {

    private final IRoutineService routineService;
    private final IExerciseService exerciseService;
    private final DemoCurrentUser currentUser;

    @GetMapping
    public String list(Model model) {
        var owner = currentUser.username();
        model.addAttribute("routines", routineService.listByOwner(owner));
        model.addAttribute("owner", owner);
        return "routines/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("name", "");
        return "routines/create";
    }

    @PostMapping("/create")
    public String create(@RequestParam("name") String name, RedirectAttributes ra) {
        var owner = currentUser.username();
        routineService.create(owner, name, null);
        ra.addFlashAttribute("message", "Rutina creada.");
        return "redirect:/mvc/routines";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam String id, Model model) {
        Routine r = routineService.findById(id).orElse(null);
        model.addAttribute("routine", r);
        model.addAttribute("catalog", exerciseService.findAll());
        return "routines/detail";
    }

    @PostMapping("/{id}/items/add")
    public String addItem(@PathVariable("id") String routineId,
                          @RequestParam(required = false) String exerciseId,
                          @RequestParam(required = false) String customName,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) Integer targetReps,
                          @RequestParam(required = false) Integer targetTimeSeconds,
                          @RequestParam(required = false) Integer targetIntensity,
                          RedirectAttributes ra) {

        Routine.RoutineItem item = Routine.RoutineItem.builder()
                .exerciseId(exerciseId != null && !exerciseId.isBlank() ? exerciseId : null)
                .customName(customName)
                .type(type)
                .description(description)
                .targetReps(targetReps)
                .targetTimeSeconds(targetTimeSeconds)
                .targetIntensity(targetIntensity)
                .build();

        routineService.addItem(routineId, item);
        ra.addFlashAttribute("message", "Ejercicio agregado a la rutina.");
        return "redirect:/mvc/routines/detail?id=" + routineId;
    }

    @PostMapping("/{id}/items/{itemId}/remove")
    public String removeItem(@PathVariable("id") String routineId,
                             @PathVariable String itemId,
                             RedirectAttributes ra) {
        routineService.removeItem(routineId, itemId);
        ra.addFlashAttribute("message", "Ítem eliminado.");
        return "redirect:/mvc/routines/detail?id=" + routineId;
    }

    @PostMapping("/{id}/rename")
    public String rename(@PathVariable("id") String routineId,
                         @RequestParam("name") String newName,
                         RedirectAttributes ra) {
        routineService.rename(routineId, newName);
        ra.addFlashAttribute("message", "Rutina renombrada.");
        return "redirect:/mvc/routines/detail?id=" + routineId;
    }

    @PostMapping("/{id}/delete")
    public String deleteRoutine(@PathVariable("id") String routineId, RedirectAttributes ra) {
        routineService.deleteById(routineId);
        ra.addFlashAttribute("message", "Rutina eliminada.");
        return "redirect:/mvc/routines";
    }
}
