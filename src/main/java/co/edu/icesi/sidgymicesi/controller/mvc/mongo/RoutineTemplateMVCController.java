package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.model.mongo.RoutineTemplate;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineTemplateService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mvc/routine-templates")
public class RoutineTemplateMVCController {

    private final IRoutineTemplateService templateService;
    private final IExerciseService exerciseService;
    private final IRoutineService routineService;

    public RoutineTemplateMVCController(IRoutineTemplateService templateService,
                                        IExerciseService exerciseService,
                                        IRoutineService routineService) {
        this.templateService = templateService;
        this.exerciseService = exerciseService;
        this.routineService = routineService;
    }

    // Listar todas
    @GetMapping({"", "/list"})
    @PreAuthorize("isAuthenticated()")
    public String list(Model model) {
        model.addAttribute("templates", templateService.findAll());
        return "routine-templates/list";
    }

    // Detalle
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String detail(@PathVariable String id, Model model) {
        RoutineTemplate tpl = templateService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Template not found"));
        // Ordenar por 'order' para la vista
        List<RoutineTemplate.TemplateItem> items = new ArrayList<>(
                Optional.ofNullable(tpl.getExercises()).orElse(List.of()));
        items.sort(Comparator.comparingInt(it -> Optional.ofNullable(it.getOrder()).orElse(Integer.MAX_VALUE)));

        Map<String, Exercise> exerciseMap = exerciseService.findAll()
                .stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));

        model.addAttribute("template", tpl);
        model.addAttribute("items", items);
        model.addAttribute("exerciseMap", exerciseMap);
        return "routine-templates/detail";
    }

    // Crear plantilla (entrenador/admin)
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("template", new RoutineTemplate());
        model.addAttribute("exercises", exerciseService.findAll());
        return "routine-templates/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public String create(@ModelAttribute("template") RoutineTemplate template) {
        normalizeOrders(template);
        RoutineTemplate saved = templateService.save(template);
        return "redirect:/mvc/routine-templates/" + saved.getId();
    }

    // Editar plantilla (entrenador/admin)
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public String editForm(@PathVariable String id, Model model) {
        RoutineTemplate tpl = templateService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Template not found"));
        model.addAttribute("template", tpl);
        model.addAttribute("exercises", exerciseService.findAll());
        return "routine-templates/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public String edit(@PathVariable String id, @ModelAttribute("template") RoutineTemplate updated) {
        updated.setId(id);
        normalizeOrders(updated);
        templateService.save(updated);
        return "redirect:/mvc/routine-templates/" + id;
    }

    // Eliminar plantilla (entrenador/admin)
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public String delete(@PathVariable String id) {
        templateService.deleteById(id);
        return "redirect:/mvc/routine-templates";
    }

    // Adoptar plantilla -> crea rutina del usuario y redirige al detalle de la rutina creada
    @PostMapping("/{id}/adopt")
    @PreAuthorize("isAuthenticated()")
    public String adopt(Authentication auth, @PathVariable String id) {
        RoutineTemplate tpl = templateService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Template not found"));
        var newRoutine = routineService.create(auth.getName(), tpl.getName(), tpl.getId());
        return "redirect:/mvc/routines/" + newRoutine.getId();
    }

    private void normalizeOrders(RoutineTemplate t) {
        List<RoutineTemplate.TemplateItem> list =
                Optional.ofNullable(t.getExercises()).orElseGet(ArrayList::new);
        for (int i = 0; i < list.size(); i++) {
            if (Objects.isNull(list.get(i).getOrder())) {
                list.get(i).setOrder(i + 1);
            }
        }
        t.setExercises(list);
    }
}
