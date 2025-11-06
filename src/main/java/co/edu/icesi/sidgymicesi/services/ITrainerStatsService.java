package co.edu.icesi.sidgymicesi.services;

import co.edu.icesi.sidgymicesi.model.TrainerMonthlyStats;
import java.time.YearMonth;
import java.util.*;

public interface ITrainerStatsService {
    void registerNewAssignment(String trainerUsername, YearMonth period);

    Optional<TrainerMonthlyStats> get(String trainerUsername, YearMonth period);
    List<TrainerMonthlyStats> listByTrainer(String trainerUsername);
    Map<String, List<TrainerMonthlyStats>> listAll(); // agrupado por entrenador
}
