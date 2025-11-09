package co.edu.icesi.sidgymicesi.services.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;

import java.util.List;
import java.util.Optional;

public interface ITrainerAssignmentMongoService {

    TrainerAssignment save(TrainerAssignment assignment);

    List<TrainerAssignment> findAll();

    Optional<TrainerAssignment> findById(String id);

    Optional<TrainerAssignment> findActiveByUser(String userUsername);

    List<TrainerAssignment> findActiveByTrainer(String trainerId);

    void deleteById(String id);
}
