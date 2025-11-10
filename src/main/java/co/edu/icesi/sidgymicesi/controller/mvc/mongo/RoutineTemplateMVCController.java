package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.RoutineTemplate;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineTemplateService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/mvc/routine-templates")
public class RoutineTemplateMVCController {

    private final IRoutineTemplateService templateService;
    private final IExerciseService exerciseService;

    public RoutineTemplateMVCController(IRoutineTemplateService templateService,
                                        IExerciseService exerciseService) {
        this.templateService = templateService;
        this.exerciseService = exerciseService;
    }

    // LIST - visible autenticados
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("templates", templateService.findAll());
        return "routine-templates/list";
    }

    // DETAIL - visible autenticados (para adoptar)
    @GetMapping("/detail")
    public String detail(@RequestParam String id, Model model) {
        RoutineTemplate tpl = templateService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plantilla no encontrada"));
        model.addAttribute("template", tpl);
        model.addAttribute("catalog", exerciseService.findAll());
        return "routine-templates/detail";
    }

    // CREATE
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("template", new RoutineTemplate());
        model.addAttribute("catalog", exerciseService.findAll());
        return "routine-templates/form";
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PostMapping("/create")
    public String createSubmit(@ModelAttribute("template") RoutineTemplate template,
                               BindingResult br,
                               Model model) {
        if (template.getName() == null || template.getName().isBlank()) {
            br.rejectValue("name", "name.required", "El nombre es obligatorio");
        }
        if (br.hasErrors()) {
            model.addAttribute("catalog", exerciseService.findAll());
            return "routine-templates/form";
        }
        if (template.getExercises() == null) template.setExercises(new ArrayList<>());
        templateService.save(template);
        return "redirect:/mvc/routine-templates/list";
    }

    // EDIT
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @GetMapping("/edit")
    public String editForm(@RequestParam String id, Model model) {
        RoutineTemplate tpl = templateService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plantilla no encontrada"));
        model.addAttribute("template", tpl);
        model.addAttribute("catalog", exerciseService.findAll());
        return "routine-templates/form";
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PostMapping("/edit")
    public String editSubmit(@ModelAttribute("template") RoutineTemplate template,
                             BindingResult br,
                             Model model) {
        if (template.getId() == null || template.getId().isBlank()) {
            br.reject("id.required", "ID requerido");
        }
        if (template.getName() == null || template.getName().isBlank()) {
            br.rejectValue("name", "name.required", "El nombre es obligatorio");
        }
        if (br.hasErrors()) {
            model.addAttribute("catalog", exerciseService.findAll());
            return "routine-templates/form";
        }
        templateService.save(template);
        return "redirect:/mvc/routine-templates/detail?id=" + template.getId();
    }

    // DELETE
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        templateService.deleteById(id);
        return "redirect:/mvc/routine-templates/list";
    }
}
