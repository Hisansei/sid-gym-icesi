package co.edu.icesi.sidgymicesi.services.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IProgressLogMongoService {

    ProgressLog save(ProgressLog progressLog);

    List<ProgressLog> findAll();

    Optional<ProgressLog> findById(String id);

    Optional<ProgressLog> findByRoutineAndDate(String routineId, LocalDate date);

    List<ProgressLog> findByUsername(String username);

    void deleteById(String id);
}
