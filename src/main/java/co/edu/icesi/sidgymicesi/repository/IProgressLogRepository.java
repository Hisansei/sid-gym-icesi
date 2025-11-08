package co.edu.icesi.sidgymicesi.repository;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IProgressLogRepository extends MongoRepository<ProgressLog, String> {
    Optional<ProgressLog> findByRoutineIdAndDate(String routineId, LocalDate date);
    List<ProgressLog> findByUsernameOrderByDateDesc(String username);
}
