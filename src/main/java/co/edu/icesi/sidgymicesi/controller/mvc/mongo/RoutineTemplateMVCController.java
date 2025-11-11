package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
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

    @GetMapping({"", "/list"})
    public String list(Model model) {
        model.addAttribute("templates", templateService.findAll());
        return "routine-templates/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        RoutineTemplate tpl = templateService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Template not found"));
        // Ordenar por 'order'
        List<RoutineTemplate.TemplateItem> items = new ArrayList<>(
                Optional.ofNullable(tpl.getExercises()).orElse(List.of()));
        items.sort(Comparator.comparingInt(it -> Optional.ofNullable(it.getOrder()).orElse(Integer.MAX_VALUE)));
        // Map de ejercicios para mostrar nombre/tipo en la vista
        Map<String, Exercise> exerciseMap = exerciseService.findAll()
                .stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));
        model.addAttribute("template", tpl);
        model.addAttribute("items", items);
        model.addAttribute("exerciseMap", exerciseMap);
        return "routine-templates/detail";
    }

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
    public String edit(@PathVariable String id, @ModelAttribute("template") RoutineTemplate template) {
        template.setId(id);
        normalizeOrders(template);
        templateService.save(template);
        return "redirect:/mvc/routine-templates/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public String delete(@PathVariable String id) {
        templateService.deleteById(id);
        return "redirect:/mvc/routine-templates";
    }

    @PostMapping("/{id}/adopt")
    @PreAuthorize("isAuthenticated()")
    public String adopt(@PathVariable String id, Authentication auth) {
        // Crea una rutina del usuario a partir de la plantilla
        Routine routine = routineService.create(auth.getName(), "Rutina basada en plantilla", id);
        return "redirect:/mvc/routines/" + routine.getId();
    }

    private void normalizeOrders(RoutineTemplate template) {
        if (template.getExercises() == null) return;
        List<RoutineTemplate.TemplateItem> items = new ArrayList<>(template.getExercises());
        items.sort(Comparator.comparingInt(i -> Optional.ofNullable(i.getOrder()).orElse(Integer.MAX_VALUE)));
        int order = 1;
        for (RoutineTemplate.TemplateItem it : items) {
            it.setOrder(order++);
        }
        template.setExercises(items);
    }
}
