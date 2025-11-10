package co.edu.icesi.sidgymicesi.services.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;

import java.util.List;
import java.util.Optional;

public interface ITrainerAssignmentService {

    TrainerAssignment assign(String userUsername, String trainerUsername);
    TrainerAssignment reassign(String userUsername, String newTrainerUsername);
    void closeAssignment(String assignmentId);

    List<TrainerAssignment> listActive();
    List<TrainerAssignment> listAll();
    List<TrainerAssignment> listByTrainer(String trainerUsername);

    Optional<TrainerAssignment> findActiveByUser(String userUsername);

    TrainerAssignment save(TrainerAssignment assignment);
    List<TrainerAssignment> findAll();
    List<TrainerAssignment> findAllActive();
    List<TrainerAssignment> findByTrainer(String trainerId);
    void deleteById(String id);
}
