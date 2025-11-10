package co.edu.icesi.sidgymicesi.repository.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IProgressLogRepository extends MongoRepository<ProgressLog, String> {

    List<ProgressLog> findAllByRoutineIdOrderByDateDesc(String routineId);
    List<ProgressLog> findAllByOwnerUsernameOrderByDateDesc(String ownerUsername);
    Optional<ProgressLog> findByRoutineIdAndDate(String routineId, LocalDate date);

    List<ProgressLog> findAllByOwnerUsernameAndDateBetweenOrderByDateDesc(
            String ownerUsername, LocalDate from, LocalDate to);

    List<ProgressLog> findAllByRoutineIdAndDateBetweenOrderByDateDesc(
            String routineId, LocalDate from, LocalDate to);
}
