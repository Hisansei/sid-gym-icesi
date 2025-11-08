package co.edu.icesi.sidgymicesi.repository;

import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IRoutineRepository extends MongoRepository<Routine, String> {
    List<Routine> findByUsernameOrderByCreatedAtDesc(String username);
}