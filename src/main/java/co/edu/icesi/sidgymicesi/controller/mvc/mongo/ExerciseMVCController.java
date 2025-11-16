package co.edu.icesi.sidgymicesi.controller.mvc.mongo;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;

@Controller
@RequestMapping("/mvc/exercises")
@RequiredArgsConstructor
public class ExerciseMVCController {

    // Ruta: http://localhost:8081/sid-gym-icesi/mvc/exercises

    private final IExerciseService exerciseService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String catalog(@RequestParam(value = "q", required = false) String q,
                          @RequestParam(value = "type", required = false) String type,
                          @RequestParam(value = "difficulty", required = false) String difficulty,
                          Model model) {

        List<Exercise> list = exerciseService.findAll();

        System.out.println("Filtrando por dificultad: " + difficulty);

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
                    .filter(e -> e.getDifficulty() != null && e.getDifficulty().equalsIgnoreCase(difficulty.trim()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("exercises", list);
        return "exercises/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPLOYEE') and principal.employeeType == 'Instructor')")
    public String addExerciseForm(Model model) {
        model.addAttribute("exercise", new Exercise());
        return "exercises/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPLOYEE') and principal.employeeType == 'Instructor')")
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
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el ejercicio: " + e.getMessage());
            model.addAttribute("exercise", exercise);
            model.addAttribute("videosText", videosText == null ? "" : videosText);
            return "exercises/add";
        }
    }

    @GetMapping("/detail")
    @PreAuthorize("isAuthenticated()")
    public String detail(@RequestParam("id") String id, Model model) {
        Exercise exercise = exerciseService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado: " + id));

        String videosText = "";
        if (exercise.getDemoVideos() != null && !exercise.getDemoVideos().isEmpty()) {
            videosText = String.join("\n", exercise.getDemoVideos());
        }

        model.addAttribute("exercise", exercise);
        model.addAttribute("videosText", videosText);
        return "exercises/detail";
    }

    @GetMapping("/edit")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPLOYEE') and principal.employeeType == 'Instructor')")
    public String editForm(@RequestParam("id") String id, Model model) {
        Exercise exercise = exerciseService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado: " + id));

        String videosText = "";
        if (exercise.getDemoVideos() != null && !exercise.getDemoVideos().isEmpty()) {
            videosText = String.join("\n", exercise.getDemoVideos());
        }

        model.addAttribute("exercise", exercise);
        model.addAttribute("videosText", videosText);
        return "exercises/edit";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EMPLOYEE') and principal.employeeType == 'Instructor')")
    public String edit(@ModelAttribute("exercise") Exercise exercise,
                       @RequestParam(value = "videosText", required = false) String videosText,
                       Model model) {
        try {
            if (videosText != null) {
                List<String> vids = Arrays.stream(videosText.split("\n"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                exercise.setDemoVideos(vids);
            } else {
                exercise.setDemoVideos(null);
            }

            exerciseService.save(exercise);
            return "redirect:/mvc/exercises";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el ejercicio: " + e.getMessage());
            model.addAttribute("exercise", exercise);
            model.addAttribute("videosText", videosText == null ? "" : videosText);
            return "exercises/edit";
        }
    }
}
