package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ITrainerStatsService {
    void registerNewAssignment(String trainerUsername, YearMonth period);

    Optional<TrainerMonthlyStat> get(String trainerUsername, YearMonth period);
    List<TrainerMonthlyStat> listByTrainer(String trainerUsername);
    Map<String, List<TrainerMonthlyStat>> listAll();
}
