package co.edu.icesi.sidgymicesi.services.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IProgressLogService {

    ProgressLog addLog(ProgressLog log);

    List<ProgressLog> listByRoutine(String routineId);
    List<ProgressLog> listByOwner(String ownerUsername);
    Optional<ProgressLog> findByRoutineAndDate(String routineId, LocalDate date);

    Optional<ProgressLog> findById(String logId);

    List<ProgressLog> listByOwnerBetween(String ownerUsername, LocalDate from, LocalDate to);
    List<ProgressLog> listByRoutineBetween(String routineId, LocalDate from, LocalDate to);

    Map<String, Object> summarizeOwnerBetween(String ownerUsername, LocalDate from, LocalDate to);

    void deleteLog(String logId);
}
