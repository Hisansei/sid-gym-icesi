package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mvc/routines")
public class RoutineMVCController {

    private final IRoutineService routineService;
    private final IExerciseService exerciseService;

    public RoutineMVCController(IRoutineService routineService, IExerciseService exerciseService) {
        this.routineService = routineService;
        this.exerciseService = exerciseService;
    }

    @GetMapping({"", "/list"})
    @PreAuthorize("isAuthenticated()")
    public String list(Authentication auth, Model model) {
        model.addAttribute("routines", routineService.listByOwner(auth.getName()));
        return "routine/list";
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String quickCreate(Authentication auth) {
        Routine r = routineService.create(auth.getName(), "Nueva rutina", null);
        return "redirect:/mvc/routines/" + r.getId();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String detail(@PathVariable String id, Model model) {
        Routine routine = routineService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Routine not found"));
        // Opciones para “agregar ejercicio”
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

    @PostMapping("/{id}/items/add")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String addItem(@PathVariable String id,
                          @RequestParam String exerciseId,
                          @RequestParam(required = false) Integer sets,
                          @RequestParam(required = false) Integer reps,
                          @RequestParam(required = false) Integer durationSec,
                          @RequestParam(required = false) Integer restSeconds,
                          @RequestParam(required = false) String notes) {
        Routine.RoutineExercise newItem = new Routine.RoutineExercise();
        newItem.setExerciseId(exerciseId);
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

    @PostMapping("/{id}/reorder")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String reorder(@PathVariable String id, @RequestParam("order") List<String> orderedIds) {
        routineService.reorderExercises(id, orderedIds);
        return "redirect:/mvc/routines/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String delete(@PathVariable String id, Authentication auth) {
        routineService.deleteById(id);
        return "redirect:/mvc/routines";
    }
}
