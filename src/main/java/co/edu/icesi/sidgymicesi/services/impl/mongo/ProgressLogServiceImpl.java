package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.repository.mongo.IProgressLogRepository;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogService;
import co.edu.icesi.sidgymicesi.services.postgres.IUserMonthlyStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressLogServiceImpl implements IProgressLogService {

    private final IProgressLogRepository progressRepo;
    private final IUserMonthlyStatsService userStatsService;

    @Override
    public ProgressLog addLog(ProgressLog log) {
        if (log.getDate() == null) log.setDate(LocalDate.now());
        validate(log);

        // Buscamos si ya existe un log HOY para esta rutina
        Optional<ProgressLog> existing = progressRepo.findByRoutineIdAndDate(log.getRoutineId(), log.getDate());

        if (existing.isPresent()) {
            ProgressLog curr = existing.get();
            // Validamos que sea el mismo dueño
            if (curr.getOwnerUsername().equalsIgnoreCase(log.getOwnerUsername())) {

                // --- LOGICA DE MEZCLA (MERGE) ---
                List<ProgressLog.Entry> currentEntries = curr.getEntries();
                if (currentEntries == null) currentEntries = new ArrayList<>();

                // Recorremos las nuevas entradas
                for (ProgressLog.Entry newEntry : log.getEntries()) {
                    // Eliminamos si ya existía entrada para este ejercicio (para sobrescribir)
                    currentEntries.removeIf(e -> e.getExerciseId().equals(newEntry.getExerciseId()));
                    // Agregamos la nueva
                    currentEntries.add(newEntry);
                }

                curr.setEntries(currentEntries);
                // Guardamos la actualización
                return progressRepo.save(curr);
            }
        }

        // Si es nuevo para el día
        ProgressLog saved = progressRepo.save(log);

        // Actualizar estadísticas
        YearMonth ym = YearMonth.from(saved.getDate());
        userStatsService.incrementFollowupsMade(saved.getOwnerUsername(), ym);

        return saved;
    }

    private void validate(ProgressLog log) {
        if (log.getOwnerUsername() == null || log.getOwnerUsername().isBlank())
            throw new IllegalArgumentException("ownerUsername requerido");
        if (log.getRoutineId() == null || log.getRoutineId().isBlank())
            throw new IllegalArgumentException("routineId requerido");
        if (log.getDate().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("date no puede ser futura");
        if (log.getEntries() == null || log.getEntries().isEmpty())
            throw new IllegalArgumentException("Debe registrar al menos un ítem de progreso");

        for (ProgressLog.Entry e : log.getEntries()) {
            if (e.getExerciseId() == null || e.getExerciseId().isBlank())
                throw new IllegalArgumentException("exerciseId requerido en entry");

            // Validar RPE 1-10 si viene numérico
            String rpe = e.getEffortLevel();
            if (rpe != null && !rpe.isBlank()) {
                if (rpe.chars().allMatch(Character::isDigit)) {
                    try {
                        int v = Integer.parseInt(rpe);
                        if (v < 1 || v > 10) {
                            throw new IllegalArgumentException("effortLevel numérico debe estar entre 1 y 10");
                        }
                    } catch (NumberFormatException ignored) { /* no-op */ }
                }
            }
        }
    }

    @Override
    public void deleteLog(String logId) {
        progressRepo.deleteById(logId);
    }

    @Override
    public void addFeedback(String logId, String trainerId, String message) {
        ProgressLog log = progressRepo.findById(logId)
                .orElseThrow(() -> new NoSuchElementException("Log no encontrado"));

        if (log.getTrainerFeedback() == null) {
            log.setTrainerFeedback(new ArrayList<>());
        }

        ProgressLog.TrainerFeedback fb = ProgressLog.TrainerFeedback.builder()
                .trainerId(trainerId)
                .message(message)
                .createdAt(java.time.Instant.now())
                .build();

        log.getTrainerFeedback().add(fb);
        progressRepo.save(log);
    }

    @Override
    public List<ProgressLog> listByRoutine(String routineId) {
        return progressRepo.findAllByRoutineIdOrderByDateDesc(routineId);
    }

    @Override
    public List<ProgressLog> listByOwner(String ownerUsername) {
        return progressRepo.findAllByOwnerUsernameOrderByDateDesc(ownerUsername);
    }

    @Override
    public Optional<ProgressLog> findByRoutineAndDate(String routineId, LocalDate date) {
        return progressRepo.findByRoutineIdAndDate(routineId, date);
    }

    @Override
    public Optional<ProgressLog> findById(String logId) {
        return progressRepo.findById(logId);
    }

    @Override
    public List<ProgressLog> listByOwnerBetween(String ownerUsername, LocalDate from, LocalDate to) {
        LocalDate f = (from == null) ? LocalDate.now().minusMonths(1) : from;
        LocalDate t = (to == null) ? LocalDate.now() : to;
        return progressRepo.findAllByOwnerUsernameAndDateBetweenOrderByDateDesc(ownerUsername, f, t);
    }

    @Override
    public List<ProgressLog> listByRoutineBetween(String routineId, LocalDate from, LocalDate to) {
        LocalDate f = (from == null) ? LocalDate.now().minusMonths(1) : from;
        LocalDate t = (to == null) ? LocalDate.now() : to;
        return progressRepo.findAllByRoutineIdAndDateBetweenOrderByDateDesc(routineId, f, t);
    }

    @Override
    public Map<String, Object> summarizeOwnerBetween(String ownerUsername, LocalDate from, LocalDate to) {
        List<ProgressLog> logs = listByOwnerBetween(ownerUsername, from, to);

        int sessions = logs.size();
        WeekFields wf = WeekFields.ISO;
        Map<String, List<Integer>> weekToRpes = new LinkedHashMap<>();

        for (ProgressLog log : logs) {
            String wk = log.getDate().getYear() + "-W" + log.getDate().get(wf.weekOfWeekBasedYear());
            for (var e : log.getEntries()) {
                String rpe = e.getEffortLevel();
                if (rpe != null && rpe.chars().allMatch(Character::isDigit)) {
                    int v = Integer.parseInt(rpe);
                    if (v >= 1 && v <= 10) {
                        weekToRpes.computeIfAbsent(wk, k -> new ArrayList<>()).add(v);
                    }
                }
            }
        }

        Map<String, Double> weeklyAvgRpe = new LinkedHashMap<>();
        weekToRpes.forEach((k, v) -> {
            double avg = v.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            weeklyAvgRpe.put(k, Math.round(avg * 10.0) / 10.0);
        });

        return Map.of(
                "totalSessions", sessions,
                "weeklyAvgRpe", weeklyAvgRpe
        );
    }
}