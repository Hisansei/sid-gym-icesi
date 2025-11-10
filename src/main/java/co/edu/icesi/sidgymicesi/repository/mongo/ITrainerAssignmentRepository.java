package co.edu.icesi.sidgymicesi.repository.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ITrainerAssignmentRepository extends MongoRepository<TrainerAssignment, String> {
    Optional<TrainerAssignment> findByUserUsernameAndActiveTrue(String username);
    List<TrainerAssignment> findByTrainerIdAndActiveTrue(String trainerId);
}
