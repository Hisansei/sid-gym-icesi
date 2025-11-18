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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createForm(Model model) {
        model.addAttribute("exercises", exerciseService.findAll());
        return "routine/form";
    }

    // Crear rutina (quick-create o con selección de ejercicios)
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Authentication auth,
                         @RequestParam(value = "name", required = false) String name,
                         @RequestParam(value = "exerciseIds", required = false) List<String> exerciseIds) {

        String n = (name == null || name.isBlank()) ? "Nueva rutina" : name.trim();
        Routine r = routineService.create(auth.getName(), n, null);

        // Si viene selección de ejercicios desde el form.html
        if (exerciseIds != null && !exerciseIds.isEmpty()) {
            for (String exId : exerciseIds) {
                if (exId == null || exId.isBlank()) continue;

                Routine.RoutineExercise it = new Routine.RoutineExercise();
                it.setExerciseId(exId.trim());
                // Valores por defecto para ejercicios agregados en lote
                it.setSets(3);
                it.setReps(12);
                it.setRestSeconds(60);

                try {
                    routineService.addItem(r.getId(), it);
                } catch (Exception e) {
                    // Ignoramos errores individuales al crear en lote para no detener el proceso
                    e.printStackTrace();
                }
            }
        }
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

        // Filtramos solo los ejercicios activos para mostrar
        List<Routine.RoutineExercise> activeExercises = Optional.ofNullable(routine.getExercises())
                .orElseGet(ArrayList::new)
                .stream()
                .filter(Routine.RoutineExercise::isStatus)
                .sorted(Comparator.comparingInt(Routine.RoutineExercise::getOrder))
                .collect(Collectors.toList());

        model.addAttribute("routine", routine);
        model.addAttribute("exerciseOptions", exerciseOptions);
        model.addAttribute("exerciseMap", exerciseMap);
        model.addAttribute("activeExercises", activeExercises);

        return "routine/detail";
    }

    @PostMapping("/{id}/rename")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String rename(@PathVariable String id, @RequestParam String name) {
        routineService.rename(id, name);
        return "redirect:/mvc/routines/" + id;
    }

    // SOLUCIÓN ERROR ADD ITEM: Se agrega recepción de 'type' y manejo de excepciones
    @PostMapping("/{id}/items/add")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String addItem(@PathVariable String id,
                          @RequestParam(required = false) String exerciseId,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) String type, // <--- CORRECCIÓN: Nuevo campo Type
                          @RequestParam(required = false) Integer sets,
                          @RequestParam(required = false) Integer reps,
                          @RequestParam(required = false) Integer durationSec,
                          @RequestParam(required = false) Integer restSeconds,
                          RedirectAttributes ra) {
        try {
            Routine.RoutineExercise newItem = new Routine.RoutineExercise();

            // 1. Selección del catálogo
            if (exerciseId != null && !exerciseId.isBlank()) {
                newItem.setExerciseId(exerciseId.trim());
                // El servicio buscará el ejercicio y llenará nombre/tipo automáticamente
            }
            // 2. Ejercicio Personalizado
            else {
                if (name != null && !name.isBlank()) newItem.setName(name.trim());
                if (type != null && !type.isBlank()) newItem.setType(type.trim()); // <--- CORRECCIÓN: Asignar tipo
            }

            newItem.setSets(sets);
            newItem.setReps(reps);
            newItem.setDurationSeconds(durationSec);
            newItem.setRestSeconds(restSeconds);

            routineService.addItem(id, newItem);

        } catch (IllegalArgumentException e) {
            // Enviamos el error a la vista para que el usuario sepa qué pasó
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/mvc/routines/" + id;
    }

    @PostMapping("/{id}/items/{itemId}/delete")
    @PreAuthorize("@authz.isOwnerOfRoutine(#id, authentication)")
    public String removeItem(@PathVariable String id,
                             @PathVariable String itemId,
                             RedirectAttributes ra) {
        try {
            routineService.removeItem(id, itemId);
            ra.addFlashAttribute("successMessage", "Ejercicio eliminado de la rutina.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
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
    public String deleteRoutine(@PathVariable String id) {
        routineService.deleteById(id);
        return "redirect:/mvc/routines";
    }
}