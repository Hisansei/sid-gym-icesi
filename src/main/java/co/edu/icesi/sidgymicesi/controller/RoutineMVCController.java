package co.edu.icesi.sidgymicesi.controller;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.IExerciseService;
import co.edu.icesi.sidgymicesi.services.IRoutineService;
import co.edu.icesi.sidgymicesi.util.DemoCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/mvc/routines")
@RequiredArgsConstructor
public class RoutineMVCController {

    private final IRoutineService routineService;
    private final IExerciseService exerciseService;

    @GetMapping("")
    public String list(Model model) {
        String owner = DemoCurrentUser.username();
        List<Routine> routines = routineService.listByOwner(owner);
        model.addAttribute("routines", routines);
        return "routine/list";
    }

    @PostMapping("/create")
    public String create(@RequestParam String name) {
        String owner = DemoCurrentUser.username();
        Routine r = routineService.create(owner, name, null);
        return "redirect:/mvc/routines/" + r.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        Routine routine = routineService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        model.addAttribute("routine", routine);
        model.addAttribute("catalog", exerciseService.findAll());
        return "routine/detail";
    }

    @PostMapping("/{id}/items/add")
    public String addItem(@PathVariable String id,
                          @RequestParam("exerciseId") String exerciseId,
                          @RequestParam(name = "sets", required = false, defaultValue = "3") int sets,
                          @RequestParam(name = "reps", required = false, defaultValue = "10") int reps,
                          @RequestParam(name = "restSeconds", required = false, defaultValue = "60") int restSeconds,
                          @RequestParam(name = "order", required = false, defaultValue = "0") int order) {

        Exercise ex = exerciseService.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado"));

        Routine.RoutineExercise item = Routine.RoutineExercise.builder()
                .id(java.util.UUID.randomUUID().toString())
                .order(order)
                .exerciseId(ex.getId())
                .name(ex.getName())
                .type(ex.getType())
                .description(ex.getDescription())
                .durationSeconds(ex.getDurationSeconds())
                .difficulty(ex.getDifficulty())
                .demoVideos(ex.getDemoVideos())
                .sets(sets)
                .reps(reps)
                .restSeconds(restSeconds)
                .build();

        routineService.addItem(id, item);
        return "redirect:/mvc/routines/" + id;
    }

    @PostMapping("/{id}/items/remove")
    public String removeItem(@PathVariable String id,
                             @RequestParam("itemId") String itemId) {
        routineService.removeItem(id, itemId);
        return "redirect:/mvc/routines/" + id;
    }

    @PostMapping("/{id}/rename")
    public String rename(@PathVariable String id,
                         @RequestParam("name") String name) {
        routineService.rename(id, name);
        return "redirect:/mvc/routines/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        routineService.deleteById(id);
        return "redirect:/mvc/routines";
    }
}
