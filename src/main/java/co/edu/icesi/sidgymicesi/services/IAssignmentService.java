package co.edu.icesi.sidgymicesi.services;

import co.edu.icesi.sidgymicesi.model.TrainerAssignment;
import java.util.*;

public interface IAssignmentService {
    TrainerAssignment assign(String userUsername, String trainerUsername);
    TrainerAssignment reassign(String userUsername, String newTrainerUsername);

    Optional<TrainerAssignment> findActiveByUser(String userUsername);
    List<TrainerAssignment> listActive();
    List<TrainerAssignment> listAll();           // histórico
    List<TrainerAssignment> listByTrainer(String trainerUsername);

    void closeAssignment(String assignmentId);
}