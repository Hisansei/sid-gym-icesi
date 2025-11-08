package co.edu.icesi.sidgymicesi.services.impl;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.repository.IExerciseRepository;
import co.edu.icesi.sidgymicesi.services.IExerciseService;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseServiceImpl implements IExerciseService{

    private final IExerciseRepository exerciseRepository;

    @Override
    public Exercise save(Exercise exercise) {

        if (exercise.getName() == null || exercise.getName().isBlank()) {
            throw new IllegalArgumentException("El ejercicio debe tener un nombre.");
        }
        if (exercise.getType() == null || exercise.getType().isBlank()) {
            throw new IllegalArgumentException("El tipo de ejercicio debe ser especificado.");
        }
        if (exercise.getDurationSeconds() == null || exercise.getDurationSeconds() <= 0) {
            throw new IllegalArgumentException("La duración del ejercicio es obligatoria y debe ser un número entero positivo.");
        }
        if (exercise.getDifficulty() == null || exercise.getDifficulty().isBlank()) {
            throw new IllegalArgumentException("La dificultad del ejercicio debe ser especificada.");
        }

        if (exercise.getId() != null) {
            exerciseRepository.findById(exercise.getId()).ifPresent(existing -> {

                if (exercise.getDescription() == null || exercise.getDescription().isBlank()) {
                    exercise.setDescription(existing.getDescription());
                }
                
                if (exercise.getDemoVideos() == null || exercise.getDemoVideos().isEmpty()) {
                    exercise.setDemoVideos(existing.getDemoVideos());
                } else {

                    List<String> cleaned = cleanUrls(exercise.getDemoVideos());
                    exercise.setDemoVideos(cleaned == null ? existing.getDemoVideos() : cleaned);
                }
            });
        } else {

            if (exercise.getDescription() != null && exercise.getDescription().isBlank()) {
                exercise.setDescription(null);
            }

            if (exercise.getDemoVideos() != null) {
                List<String> cleaned = cleanUrls(exercise.getDemoVideos());
                exercise.setDemoVideos(cleaned);
            }
        }

        return exerciseRepository.save(exercise);
    }

    private List<String> cleanUrls(List<String> urls) {
        if (urls == null) return null;

        List<String> cleaned = urls.stream()
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .toList();

        return cleaned.isEmpty() ? null : cleaned;
    }

    @Override
    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }

    @Override
    public Optional<Exercise> findById(String id) {
        return exerciseRepository.findById(id);
    }

    @Override
    public List<Exercise> findByType(String type) {
        return exerciseRepository.findByType(type);
    }

    @Override
    public List<Exercise> findByDifficulty(String difficulty) {
        return exerciseRepository.findByDifficulty(difficulty);
    }

    @Override
    public List<Exercise> findByNameContainingIgnoreCase(String name) {
        return exerciseRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Exercise> findByDurationSeconds(Integer durationSeconds) {
        return exerciseRepository.findByDurationSeconds(durationSeconds);
    }

    @Override
    public void deleteById(String id) {
        exerciseRepository.deleteById(id);
    }

}