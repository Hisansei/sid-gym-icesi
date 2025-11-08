package co.edu.icesi.sidgymicesi.controller;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.services.IExerciseService;

@Controller
@RequestMapping("/mvc/exercises")
@RequiredArgsConstructor
public class ExerciseMVCController {

    private final IExerciseService exerciseService;

    @GetMapping
    public String catalog(@RequestParam(value = "q", required = false) String q,
                          @RequestParam(value = "type", required = false) String type,
                          @RequestParam(value = "difficulty", required = false) String difficulty,
                          Model model) {

        List<Exercise> list = exerciseService.findAll();

        if (q != null && !q.isBlank()) {
            list = list.stream()
                    .filter(e -> e.getName() != null && e.getName().toLowerCase().contains(q.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (type != null && !type.isBlank()) {
            list = list.stream()
                    .filter(e -> e.getType() != null && e.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        if (difficulty != null && !difficulty.isBlank()) {
            list = list.stream()
                    .filter(e -> e.getDifficulty() != null && e.getDifficulty().equalsIgnoreCase(difficulty))
                    .collect(Collectors.toList());
        }

        model.addAttribute("exercises", list);
        return "exercises/list";
    }

    @GetMapping("/add")
    public String addExerciseForm(Model model) {
        model.addAttribute("exercise", new Exercise());
        return "exercises/add";
    }

    @PostMapping("/add")
    public String addExercise(@ModelAttribute("exercise") Exercise exercise,
                              @RequestParam(value = "videosText", required = false) String videosText,
                              Model model) {
        try {
            if (videosText != null) {
                List<String> vids = Arrays.stream(videosText.split("\n"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                exercise.setDemoVideos(vids);
            }
            exerciseService.save(exercise);
            return "redirect:/mvc/exercises";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("exercise", exercise);
            return "exercises/add";
        }
    }

    @GetMapping("/detail")
    public String detail(@RequestParam String id, Model model) {
        Exercise e = exerciseService.findById(id).orElse(null);
        model.addAttribute("exercise", e);
        return "exercises/detail";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam String id, Model model) {
        Exercise actual = exerciseService.findById(id).orElse(null);
        model.addAttribute("actualExercise", actual);
        String videosText = (actual != null && actual.getDemoVideos() != null)
                ? String.join("\n", actual.getDemoVideos())
                : "";
        model.addAttribute("videosText", videosText);
        return "exercises/edit";
    }

    @PostMapping("/edit")
    public String editExercise(@ModelAttribute("actualExercise") Exercise form,
                       @RequestParam(value = "videosText", required = false) String videosText,
                       Model model,
                       RedirectAttributes ra) {
        try {
            Exercise existing = exerciseService.findById(form.getId())
            .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado."));

            if (videosText != null) {
                List<String> vids = Arrays.stream(videosText.split("\n"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                form.setDemoVideos(vids);
            }

            exerciseService.save(form);
            ra.addFlashAttribute("message", "Ejercicio actualizado correctamente.");
            return "redirect:/mvc/exercises/detail?id=" + form.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("actualExercise", form);
            model.addAttribute("videosText", videosText != null ? videosText : "");
            return "exercises/edit";
        }
    }

    @GetMapping("/delete")
    public String deleteExercise(@RequestParam String id, RedirectAttributes ra) {
        exerciseService.deleteById(id);
        ra.addFlashAttribute("message", "Ejercicio eliminado correctamente.");
        return "redirect:/mvc/exercises";
    }
}