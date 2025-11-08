package co.edu.icesi.sidgymicesi.repository;

import co.edu.icesi.sidgymicesi.model.mongo.RoutineTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IRoutineTemplateRepository extends MongoRepository<RoutineTemplate, String> {
    List<RoutineTemplate> findByStatusTrue();
    List<RoutineTemplate> findByTrainerId(String trainerId);
}
