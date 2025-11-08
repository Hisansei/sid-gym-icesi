package co.edu.icesi.sidgymicesi.repository;

import java.util.List;
import java.util.Optional;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface IExerciseRepository extends MongoRepository<Exercise, String> {

    // Consultas:

    List<Exercise> findAll();

    Optional<Exercise> findById(String id);

    List<Exercise> findByType(String type);

    List<Exercise> findByDifficulty(String difficulty);

    List<Exercise> findByNameContainingIgnoreCase(String name);

    List<Exercise> findByDurationSeconds(Integer durationSeconds);

    // Borrado...
    
    void deleteById(String id);
}