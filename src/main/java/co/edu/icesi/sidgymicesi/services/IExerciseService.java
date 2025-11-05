package co.edu.icesi.sidgymicesi.services;

import java.util.List;
import java.util.Optional;

import co.edu.icesi.sidgymicesi.model.Exercise;

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