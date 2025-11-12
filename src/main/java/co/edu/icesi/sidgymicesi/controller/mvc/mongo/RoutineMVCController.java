package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mvc/routines")
@RequiredArgsConstructor
public class RoutineMVCController {

    private final IRoutineService routineService;
    private final IExerciseService exerciseService;

    @GetMapping({"", "/list"})
    @PreAuthorize("isAuthenticated()")
    public String list(Authentication auth, Model model) {
        model.addAttribute("routines", routineService.listByOwner(auth.getName()));
        return "routine/list";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String quickCreate(Authentication auth,
                              @RequestParam(value = "name", required = false) String name) {
        String n = (name == null || name.isBlank()) ? "Nueva rutina" : name.trim();
        Routine r = routineService.create(auth.getName(), n, null);
        return "redirect:/mvc/routines/" + r.getId();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String detail(@PathVariable String id, Model model) {
        Routine routine = routineService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));
        List<Exercise> exerciseOptions = exerciseService.findAll();
        Map<String, Exercise> exerciseMap = exerciseOptions.stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));
        model.addAttribute("routine", routine);
        model.addAttribute("exerciseOptions", exerciseOptions);
        model.addAttribute("exerciseMap", exerciseMap);
        return "routine/detail";
    }

    @PostMapping("/{id}/rename")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String rename(@PathVariable String id, @RequestParam String name) {
        routineService.rename(id, name);
        return "redirect:/mvc/routines/" + id;
    }

    // Agregar ejercicio a la rutina (desde catálogo o personalizado)
    @PostMapping("/{id}/items/add")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String addItem(@PathVariable String id,
                          @RequestParam(required = false) String exerciseId,
                          @RequestParam(required = false) String name, // nombre personalizado si aplica
                          @RequestParam(required = false) Integer sets,
                          @RequestParam(required = false) Integer reps,
                          @RequestParam(required = false) Integer durationSec,
                          @RequestParam(required = false) Integer restSeconds) {
        Routine.RoutineExercise newItem = new Routine.RoutineExercise();
        if (exerciseId != null && !exerciseId.isBlank()) newItem.setExerciseId(exerciseId.trim());
        if (name != null && !name.isBlank()) newItem.setName(name.trim());
        newItem.setSets(sets);
        newItem.setReps(reps);
        newItem.setDurationSeconds(durationSec);
        newItem.setRestSeconds(restSeconds);
        routineService.addItem(id, newItem);
        return "redirect:/mvc/routines/" + id;
    }

    @PostMapping("/{id}/items/{itemId}/delete")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String removeItem(@PathVariable String id, @PathVariable String itemId) {
        routineService.removeItem(id, itemId);
        return "redirect:/mvc/routines/" + id;
    }

    // Reordenar ejercicios
    @PostMapping("/{id}/reorder")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String reorder(@PathVariable String id, @RequestParam("order") List<String> orderedIds) {
        routineService.reorderExercises(id, orderedIds);
        return "redirect:/mvc/routines/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String deleteRoutine(@PathVariable String id) {
        routineService.deleteById(id);
        return "redirect:/mvc/routines";
    }
}
