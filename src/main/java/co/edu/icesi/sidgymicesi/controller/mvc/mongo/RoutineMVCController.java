package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mvc/routines")
@RequiredArgsConstructor
public class RoutineMVCController {

    private final IRoutineService routineService;
    private final IExerciseService exerciseService;

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No hay usuario autenticado.");
        }
        return auth.getName();
    }

    @GetMapping("")
    public String list(Model model) {
        List<Routine> routines = routineService.listByOwner(currentUsername());
        model.addAttribute("routines", routines);
        return "routines/list"; // <- carpeta correcta
    }

    // Enlace rápido desde la lista: GET /mvc/routines/create
    @GetMapping("/create")
    public String createQuick() {
        Routine r = routineService.create(currentUsername(), "Mi rutina", null);
        return "redirect:/mvc/routines/" + r.getId();
    }

    // Mantengo la opción POST /mvc/routines/create (si luego haces un formulario con nombre)
    @PostMapping("/create")
    public String create(@RequestParam String name) {
        Routine r = routineService.create(currentUsername(), name, null);
        return "redirect:/mvc/routines/" + r.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        Routine routine = routineService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        model.addAttribute("routine", routine);
        model.addAttribute("catalog", exerciseService.findAll());
        return "routines/detail"; // <- carpeta correcta
    }

    // Compatibilidad con enlaces existentes: /mvc/routines/detail?id=...
    @GetMapping("/detail")
    public String detailParam(@RequestParam("id") String id, Model model) {
        return detail(id, model);
    }

    @PostMapping("/{id}/items/add")
    public String addItem(@PathVariable String id,
                          @RequestParam("exerciseId") String exerciseId,
                          @RequestParam(name = "sets", defaultValue = "3") int sets,
                          @RequestParam(name = "reps", defaultValue = "10") int reps,
                          @RequestParam(name = "restSeconds", defaultValue = "60") int restSeconds,
                          @RequestParam(name = "order", defaultValue = "0") int order) {

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
                         @RequestParam("name") String newName) {
        routineService.rename(id, newName);
        return "redirect:/mvc/routines/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        routineService.deleteById(id);
        return "redirect:/mvc/routines";
    }
}
