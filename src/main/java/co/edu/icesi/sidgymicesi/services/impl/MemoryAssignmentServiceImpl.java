package co.edu.icesi.sidgymicesi.services.impl;

import co.edu.icesi.sidgymicesi.model.mongo.TrainerAssignment;
import co.edu.icesi.sidgymicesi.services.IAssignmentService;
import co.edu.icesi.sidgymicesi.services.ITrainerStatsService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
public class MemoryAssignmentServiceImpl implements IAssignmentService {

    private final Map<String, TrainerAssignment> store = new LinkedHashMap<>();
    private final Map<String, String> activeByUser = new HashMap<>();
    private final ITrainerStatsService statsService;

    public MemoryAssignmentServiceImpl(ITrainerStatsService statsService) {
        this.statsService = statsService;
    }

    @Override
    public TrainerAssignment assign(String userUsername, String trainerUsername) {
        Objects.requireNonNull(userUsername, "userUsername requerido");
        Objects.requireNonNull(trainerUsername, "trainerUsername requerido");

        // cierra la activa (si existe)
        Optional.ofNullable(activeByUser.get(userUsername))
                .map(store::get)
                .filter(TrainerAssignment::isActive)
                .ifPresent(a -> {
                    a.setActive(false);
                    a.setEndedAt(LocalDateTime.now());
                });

        TrainerAssignment a = TrainerAssignment.builder()
                .id(UUID.randomUUID().toString())
                .userUsername(userUsername)
                .trainerId(trainerUsername)   // usamos trainerUsername como id
                .active(true)
                .assignedAt(LocalDateTime.now())
                .build();

        store.put(a.getId(), a);
        activeByUser.put(userUsername, a.getId());

        statsService.registerNewAssignment(trainerUsername, YearMonth.now());
        return a;
    }

    @Override
    public TrainerAssignment reassign(String userUsername, String newTrainerUsername) {
        return assign(userUsername, newTrainerUsername);
    }

    @Override
    public Optional<TrainerAssignment> findActiveByUser(String userUsername) {
        return Optional.ofNullable(activeByUser.get(userUsername)).map(store::get);
    }

    @Override public List<TrainerAssignment> listActive() {
        return store.values().stream().filter(TrainerAssignment::isActive).collect(Collectors.toList());
    }

    @Override public List<TrainerAssignment> listAll() { return new ArrayList<>(store.values()); }

    @Override public List<TrainerAssignment> listByTrainer(String trainerUsername) {
        return store.values().stream()
                .filter(a -> Objects.equals(a.getTrainerId(), trainerUsername))
                .sorted(Comparator.comparing(TrainerAssignment::getAssignedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void closeAssignment(String assignmentId) {
        TrainerAssignment a = store.get(assignmentId);
        if (a != null && a.isActive()) {
            a.setActive(false);
            a.setEndedAt(LocalDateTime.now());
            activeByUser.computeIfPresent(a.getUserUsername(), (u,id) -> id.equals(assignmentId) ? null : id);
        }
    }
}
