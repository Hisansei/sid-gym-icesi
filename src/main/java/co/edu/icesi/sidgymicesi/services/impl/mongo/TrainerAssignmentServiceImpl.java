package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import co.edu.icesi.sidgymicesi.repository.mongo.ITrainerAssignmentRepository;
import co.edu.icesi.sidgymicesi.services.mongo.ITrainerAssignmentService;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerAssignmentServiceImpl implements ITrainerAssignmentService {

    private final ITrainerAssignmentRepository trainerAssignmentRepository;
    private final ITrainerStatsService statsService;

    // ===================== NEGOCIO =====================

    @Override
    public TrainerAssignment assign(String userUsername, String trainerUsername) {
        trainerAssignmentRepository.findByUserUsernameAndActiveTrue(userUsername)
                .ifPresent(a -> {
                    a.setActive(false);
                    a.setEndedAt(LocalDateTime.now());
                    trainerAssignmentRepository.save(a);
                });

        TrainerAssignment newA = TrainerAssignment.builder()
                .userUsername(userUsername)
                .trainerId(trainerUsername)
                .assignedAt(LocalDateTime.now())
                .active(true)
                .build();

        newA = trainerAssignmentRepository.save(newA);

        statsService.registerNewAssignment(trainerUsername, YearMonth.now());

        return newA;
    }

    @Override
    public TrainerAssignment reassign(String userUsername, String newTrainerUsername) {
        return assign(userUsername, newTrainerUsername);
    }

    @Override
    public void closeAssignment(String assignmentId) {
        trainerAssignmentRepository.findById(assignmentId).ifPresent(a -> {
            if (a.isActive()) {
                a.setActive(false);
                a.setEndedAt(LocalDateTime.now());
                trainerAssignmentRepository.save(a);
            }
        });
    }

    @Override
    public List<TrainerAssignment> listActive() {
        return trainerAssignmentRepository.findByActiveTrue();
    }

    @Override
    public List<TrainerAssignment> listAll() {
        return trainerAssignmentRepository.findAll();
    }

    @Override
    public List<TrainerAssignment> listByTrainer(String trainerUsername) {
        return trainerAssignmentRepository.findByTrainerIdOrderByAssignedAtDesc(trainerUsername);
    }

    @Override
    public Optional<TrainerAssignment> findActiveByUser(String userUsername) {
        return trainerAssignmentRepository.findByUserUsernameAndActiveTrue(userUsername);
    }

    // ===================== REPO-LIKE (si los usas) =====================

    @Override
    public TrainerAssignment save(TrainerAssignment assignment) {
        return trainerAssignmentRepository.save(assignment);
    }

    @Override
    public List<TrainerAssignment> findAll() {
        return trainerAssignmentRepository.findAll();
    }

    @Override
    public List<TrainerAssignment> findAllActive() {
        return trainerAssignmentRepository.findByActiveTrue();
    }

    @Override
    public List<TrainerAssignment> findByTrainer(String trainerId) {
        return trainerAssignmentRepository.findByTrainerIdOrderByAssignedAtDesc(trainerId);
    }

    @Override
    public void deleteById(String id) {
        trainerAssignmentRepository.deleteById(id);
    }
}
