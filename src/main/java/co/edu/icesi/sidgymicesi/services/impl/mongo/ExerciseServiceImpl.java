package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.repository.mongo.IExerciseRepository;
import co.edu.icesi.sidgymicesi.services.mongo.IExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseServiceImpl implements IExerciseService {

    private final IExerciseRepository exerciseRepository;

    private static final Set<String> ALLOWED_TYPES = Set.of("cardio", "fuerza", "movilidad");

    @Override
    public Exercise save(Exercise exercise) {
        if (exercise == null) throw new IllegalArgumentException("Ejercicio nulo.");

        // Normalizaciones básicas
        if (exercise.getName() != null) exercise.setName(exercise.getName().trim());
        if (exercise.getType() != null) exercise.setType(exercise.getType().trim().toLowerCase());
        if (exercise.getDifficulty() != null) exercise.setDifficulty(exercise.getDifficulty().trim());
        if (exercise.getDescription() != null && exercise.getDescription().isBlank()) {
            exercise.setDescription(null);
        }
        if (exercise.getDemoVideos() != null) {
            exercise.setDemoVideos(cleanUrls(exercise.getDemoVideos()));
        }

        // Validaciones de negocio
        if (exercise.getName() == null || exercise.getName().isBlank()) {
            throw new IllegalArgumentException("El ejercicio debe tener un nombre.");
        }
        if (exercise.getType() == null || exercise.getType().isBlank()) {
            throw new IllegalArgumentException("El tipo de ejercicio es obligatorio.");
        }
        if (!ALLOWED_TYPES.contains(exercise.getType())) {
            throw new IllegalArgumentException("Tipo inválido. Use: cardio, fuerza o movilidad.");
        }
        // En tu versión anterior exigías duración > 0; mantengo esa regla
        if (exercise.getDurationSeconds() == null || exercise.getDurationSeconds() <= 0) {
            throw new IllegalArgumentException("La duración es obligatoria y debe ser > 0.");
        }
        if (exercise.getDifficulty() == null || exercise.getDifficulty().isBlank()) {
            throw new IllegalArgumentException("La dificultad es obligatoria.");
        }

        // Merge suave si viene con id (actualización parcial de campos opcionales)
        if (exercise.getId() != null) {
            exerciseRepository.findById(exercise.getId()).ifPresent(existing -> {
                if (exercise.getDescription() == null) {
                    exercise.setDescription(existing.getDescription());
                }
                if (exercise.getDemoVideos() == null) {
                    exercise.setDemoVideos(existing.getDemoVideos());
                }
            });
        }

        return exerciseRepository.save(exercise);
    }

    @Override
    public Exercise update(Exercise updated) {
        if (updated == null || updated.getId() == null || updated.getId().isBlank()) {
            throw new IllegalArgumentException("Debe enviar el id del ejercicio a actualizar.");
        }

        Exercise current = exerciseRepository.findById(updated.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe ejercicio con id: " + updated.getId()));

        current.setName(updated.getName());
        current.setType(updated.getType());
        current.setDescription(updated.getDescription());
        current.setDurationSeconds(updated.getDurationSeconds());
        current.setDifficulty(updated.getDifficulty());
        current.setDemoVideos(updated.getDemoVideos());

        normalize(current);
        validate(current);

        return exerciseRepository.save(current);
    }

    /* ===== Helpers ===== */

    private void normalize(Exercise e) {
        if (e.getName() != null) e.setName(e.getName().trim());
        if (e.getType() != null) e.setType(e.getType().trim().toLowerCase());
        if (e.getDifficulty() != null) e.setDifficulty(e.getDifficulty().trim());
        if (e.getDescription() != null && e.getDescription().isBlank()) e.setDescription(null);
        if (e.getDemoVideos() != null) e.setDemoVideos(cleanUrls(e.getDemoVideos()));
    }

    private void validate(Exercise e) {
        if (e.getName() == null || e.getName().isBlank()) {
            throw new IllegalArgumentException("El ejercicio debe tener un nombre.");
        }
        if (e.getType() == null || e.getType().isBlank()) {
            throw new IllegalArgumentException("El tipo de ejercicio es obligatorio.");
        }
        if (!ALLOWED_TYPES.contains(e.getType())) {
            throw new IllegalArgumentException("Tipo inválido. Use: cardio, fuerza o movilidad.");
        }
        if (e.getDurationSeconds() != null && e.getDurationSeconds() < 0) {
            throw new IllegalArgumentException("La duración no puede ser negativa.");
        }
    }

    private List<String> cleanUrls(List<String> urls) {
        if (urls == null) return null;
        List<String> cleaned = urls.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());
        return cleaned.isEmpty() ? null : cleaned;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Exercise> findById(String id) {
        return exerciseRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exercise> findByType(String type) {
        if (type == null) return List.of();
        return exerciseRepository.findByType(type.trim().toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exercise> findByDifficulty(String difficulty) {
        if (difficulty == null) return List.of();
        return exerciseRepository.findByDifficulty(difficulty.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exercise> findByNameContainingIgnoreCase(String name) {
        if (name == null || name.isBlank()) return List.of();
        return exerciseRepository.findByNameContainingIgnoreCase(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exercise> findByDurationSeconds(Integer durationSeconds) {
        if (durationSeconds == null || durationSeconds <= 0) return List.of();
        return exerciseRepository.findByDurationSeconds(durationSeconds);
    }

    @Override
    public void deleteById(String id) {
        exerciseRepository.deleteById(id);
    }
}
