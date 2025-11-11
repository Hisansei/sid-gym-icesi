package co.edu.icesi.sidgymicesi.repository.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IExerciseRepository extends MongoRepository<Exercise, String> {

    // Lecturas
    List<Exercise> findAll();
    Optional<Exercise> findById(String id);
    List<Exercise> findByType(String type);
    List<Exercise> findByDifficulty(String difficulty);
    List<Exercise> findByNameContainingIgnoreCase(String name);
    List<Exercise> findByDurationSeconds(Integer durationSeconds);

    // Borrado
    void deleteById(String id);

    List<Exercise> findByTypeIgnoreCase(String type);
}
