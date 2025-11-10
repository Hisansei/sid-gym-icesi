package co.edu.icesi.sidgymicesi.services.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;

import java.util.List;
import java.util.Optional;

public interface IExerciseService {
    Exercise save(Exercise exercise);

    List<Exercise> findAll();
    Optional<Exercise> findById(String id);
    List<Exercise> findByType(String type);
    List<Exercise> findByDifficulty(String difficulty);
    List<Exercise> findByNameContainingIgnoreCase(String name);
    List<Exercise> findByDurationSeconds(Integer durationSeconds);

    void deleteById(String id);
}
