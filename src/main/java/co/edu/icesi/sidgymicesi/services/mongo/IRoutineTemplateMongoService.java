package co.edu.icesi.sidgymicesi.services.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.RoutineTemplate;

import java.util.List;
import java.util.Optional;

public interface IRoutineTemplateMongoService {

    RoutineTemplate save(RoutineTemplate routineTemplate);

    List<RoutineTemplate> findAll();

    List<RoutineTemplate> findActiveTemplates();   // status = true

    List<RoutineTemplate> findByTrainer(String trainerId);

    Optional<RoutineTemplate> findById(String id);

    void deleteById(String id);
}
