package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import co.edu.icesi.sidgymicesi.repository.mongo.ITrainerAssignmentRepository;
import co.edu.icesi.sidgymicesi.services.mongo.ITrainerAssignmentMongoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerAssignmentMongoServiceImpl implements ITrainerAssignmentMongoService {

    private final ITrainerAssignmentRepository trainerAssignmentRepository;

    @Override
    public TrainerAssignment save(TrainerAssignment assignment) {
        return trainerAssignmentRepository.save(assignment);
    }

    @Override
    public List<TrainerAssignment> findAll() {
        return trainerAssignmentRepository.findAll();
    }

    @Override
    public Optional<TrainerAssignment> findById(String id) {
        return trainerAssignmentRepository.findById(id);
    }

    @Override
    public Optional<TrainerAssignment> findActiveByUser(String userUsername) {
        return trainerAssignmentRepository.findByUserUsernameAndActiveTrue(userUsername);
    }

    @Override
    public List<TrainerAssignment> findActiveByTrainer(String trainerId) {
        return trainerAssignmentRepository.findByTrainerIdAndActiveTrue(trainerId);
    }

    @Override
    public void deleteById(String id) {
        trainerAssignmentRepository.deleteById(id);
    }
}
