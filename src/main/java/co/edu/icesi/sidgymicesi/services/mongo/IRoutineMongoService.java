package co.edu.icesi.sidgymicesi.services.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Routine;

import java.util.List;
import java.util.Optional;

public interface IRoutineMongoService {

    Routine save(Routine routine);

    List<Routine> findAll();

    Optional<Routine> findById(String id);

    List<Routine> findByUsername(String username);

    void deleteById(String id);
}
