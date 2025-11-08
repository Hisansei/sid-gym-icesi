package co.edu.icesi.sidgymicesi.services;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import java.time.LocalDate;
import java.util.*;

public interface IProgressService {
    ProgressLog addLog(ProgressLog log);
    List<ProgressLog> listByRoutine(String routineId);
    List<ProgressLog> listByOwner(String ownerUsername);

    Optional<ProgressLog> findByRoutineAndDate(String routineId, LocalDate date);
    void deleteLog(String logId);
}
