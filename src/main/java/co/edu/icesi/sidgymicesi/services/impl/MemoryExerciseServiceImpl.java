package co.edu.icesi.sidgymicesi.services.impl;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.services.IExerciseService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service @Primary
public class MemoryExerciseServiceImpl implements IExerciseService {

    private final Map<String, Exercise> store = new LinkedHashMap<>();

    @PostConstruct
    public void seed() {
        // semilla mínima para demo
        save(new Exercise(null, "Trote suave", "cardio",
                "Calentamiento aeróbico ligero", 600, "baja",
                List.of("https://youtu.be/demo1")));
        save(new Exercise(null, "Sentadilla", "fuerza",
                "Sentadilla con peso corporal", null, "media",
                List.of("https://youtu.be/demo2")));
        save(new Exercise(null, "Plancha", "fuerza",
                "Plancha isométrica", 60, "media",
                List.of("https://youtu.be/demo3")));
        save(new Exercise(null, "Estiramiento de cadera", "movilidad",
                "Aberturas y rotaciones", 120, "baja",
                List.of("https://youtu.be/demo4")));
    }

    @Override
    public Exercise save(Exercise exercise) {
        if (exercise == null || exercise.getName() == null || exercise.getName().isBlank())
            throw new IllegalArgumentException("Nombre de ejercicio requerido");
        if (exercise.getId() == null || exercise.getId().isBlank())
            exercise.setId(UUID.randomUUID().toString());
        store.put(exercise.getId(), exercise);
        return exercise;
    }

    @Override public List<Exercise> findAll() { return new ArrayList<>(store.values()); }
    @Override public Optional<Exercise> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override public List<Exercise> findByType(String type) {
        return store.values().stream()
                .filter(e -> e.getType()!=null && e.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    @Override public List<Exercise> findByDifficulty(String difficulty) {
        return store.values().stream()
                .filter(e -> e.getDifficulty()!=null && e.getDifficulty().equalsIgnoreCase(difficulty))
                .collect(Collectors.toList());
    }

    @Override public List<Exercise> findByNameContainingIgnoreCase(String name) {
        return store.values().stream()
                .filter(e -> e.getName()!=null && e.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override public List<Exercise> findByDurationSeconds(Integer s) {
        return store.values().stream()
                .filter(e -> Objects.equals(e.getDurationSeconds(), s)).collect(Collectors.toList());
    }

    @Override public void deleteById(String id) { store.remove(id); }
}
