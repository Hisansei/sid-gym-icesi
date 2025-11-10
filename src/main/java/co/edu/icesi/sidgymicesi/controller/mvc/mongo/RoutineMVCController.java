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

import java.util.List;

@Controller
@RequestMapping("/mvc/routines")
@RequiredArgsConstructor
public class RoutineMVCController {

    private final IRoutineService routineService;
    private final IExerciseService exerciseService;

    // Lista del usuario autenticado
    @GetMapping("")
    @PreAuthorize("hasAnyRole('STUDENT','EMPLOYEE','ADMIN')")
    public String list(Authentication auth, Model model) {
        List<Routine> routines = routineService.listByOwner(auth.getName());
        model.addAttribute("routines", routines);
        return "routines/list";
    }

    // Crear rápida (nombre por defecto)
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('STUDENT','EMPLOYEE')")
    public String createQuick(Authentication auth) {
        Routine r = routineService.create(auth.getName(), "Mi rutina", null);
        return "redirect:/mvc/routines/" + r.getId();
    }

    // Crear con nombre
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('STUDENT','EMPLOYEE')")
    public String create(Authentication auth, @RequestParam String name) {
        Routine r = routineService.create(auth.getName(), name, null);
        return "redirect:/mvc/routines/" + r.getId();
    }

    // Detalle por path
    @GetMapping("/{id}")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String detail(@PathVariable String id, Model model) {
        Routine routine = routineService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        model.addAttribute("routine", routine);
        model.addAttribute("catalog", exerciseService.findAll());
        return "routines/detail";
    }

    // Detalle por query param (compatibilidad)
    @GetMapping("/detail")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String detailParam(@RequestParam("id") String id, Model model) {
        return detail(id, model);
    }

    // Agregar item desde catálogo
    @PostMapping("/{id}/items/add")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
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

    // Quitar item
    @PostMapping("/{id}/items/remove")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String removeItem(@PathVariable String id, @RequestParam("itemId") String itemId) {
        routineService.removeItem(id, itemId);
        return "redirect:/mvc/routines/" + id;
    }

    // Reordenar items (usa el nombre correcto del servicio)
    @PostMapping("/{id}/reorder")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String reorder(@PathVariable String id, @RequestParam("orderedIds") List<String> orderedIds) {
        routineService.reorderExercises(id, orderedIds);
        return "redirect:/mvc/routines/" + id;
    }

    // Eliminar rutina (usa deleteById del servicio)
    @PostMapping("/{id}/delete")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String deleteRoutine(@PathVariable String id) {
        routineService.deleteById(id);
        return "redirect:/mvc/routines";
    }
}
