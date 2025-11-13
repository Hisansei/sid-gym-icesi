package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.User;
import co.edu.icesi.sidgymicesi.services.IUserService;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import co.edu.icesi.sidgymicesi.repository.mongo.ITrainerAssignmentRepository;
import co.edu.icesi.sidgymicesi.services.mongo.ITrainerAssignmentService;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerAssignmentServiceImpl implements ITrainerAssignmentService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TrainerAssignmentServiceImpl.class);

    private final ITrainerAssignmentRepository trainerAssignmentRepository;
    private final ITrainerStatsService trainerStatsService;
    private final IUserService userService;

    @Override
    public TrainerAssignment assign(String userUsername, String trainerId) {
        logger.info("Nueva asignación user={} trainerId={}", userUsername, trainerId);

        if (!StringUtils.hasText(userUsername) || !StringUtils.hasText(trainerId)) {
            throw new IllegalArgumentException("Usuario y entrenador son obligatorios.");
        }

        User user = userService.findByUsername(userUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el usuario con username '" + userUsername + "'."));

        User trainerUser = userService.findByEmployeeId(trainerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un usuario asociado al entrenador con id '" + trainerId + "'."));

        trainerAssignmentRepository.findByUserUsernameAndActiveTrue(userUsername)
                .ifPresent(a -> {
                    a.setActive(false);
                    a.setEndedAt(LocalDateTime.now());
                    trainerAssignmentRepository.save(a);
                });

        TrainerAssignment newAssignment = TrainerAssignment.builder()
                .userUsername(user.getUsername())
                .trainerId(trainerId)
                .assignedAt(LocalDateTime.now())
                .active(true)
                .build();

        TrainerAssignment saved = trainerAssignmentRepository.save(newAssignment);
        trainerStatsService.registerNewAssignment(trainerUser.getUsername(), YearMonth.now());

        return saved;
    }

    @Override
    public TrainerAssignment reassign(String userUsername, String newTrainerId) {
        return assign(userUsername, newTrainerId);
    }

    @Override
    public void closeAssignment(String assignmentId) {
        if (assignmentId == null || assignmentId.isBlank()) {
            throw new IllegalArgumentException("assignmentId es obligatorio");
        }
        trainerAssignmentRepository.findById(assignmentId).ifPresent(a -> {
            if (a.isActive()) {
                a.setActive(false);
                a.setEndedAt(LocalDateTime.now());
                trainerAssignmentRepository.save(a);
            }
        });
    }

    @Override public List<TrainerAssignment> listActive() { return trainerAssignmentRepository.findByActiveTrue(); }
    @Override public List<TrainerAssignment> listAll() { return trainerAssignmentRepository.findAll(); }
    @Override public List<TrainerAssignment> listByTrainer(String trainerId) { return trainerAssignmentRepository.findByTrainerIdOrderByAssignedAtDesc(trainerId); }
    @Override public Optional<TrainerAssignment> findActiveByUser(String userUsername) { return trainerAssignmentRepository.findByUserUsernameAndActiveTrue(userUsername); }
    @Override public TrainerAssignment save(TrainerAssignment a) { if (a==null) throw new IllegalArgumentException("assignment no puede ser null"); return trainerAssignmentRepository.save(a); }
    @Override public List<TrainerAssignment> findAll() { return trainerAssignmentRepository.findAll(); }
    @Override public List<TrainerAssignment> findAllActive() { return trainerAssignmentRepository.findByActiveTrue(); }
    @Override public List<TrainerAssignment> findByTrainer(String trainerId) { return trainerAssignmentRepository.findByTrainerIdOrderByAssignedAtDesc(trainerId); }
    @Override public void deleteById(String id) { if (id==null||id.isBlank()) throw new IllegalArgumentException("id es obligatorio"); trainerAssignmentRepository.deleteById(id); }
    @Override public List<TrainerAssignment> listHistoryByUser(String userUsername) { return trainerAssignmentRepository.findByUserUsernameOrderByAssignedAtDesc(userUsername); }
}