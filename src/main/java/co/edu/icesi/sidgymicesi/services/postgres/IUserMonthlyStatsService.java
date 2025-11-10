package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.UserMonthlyStat;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IUserMonthlyStatsService {
    void incrementRoutinesStarted(String userUsername, YearMonth period);
    void incrementFollowupsMade(String userUsername, YearMonth period);

    Optional<UserMonthlyStat> get(String userUsername, YearMonth period);
    List<UserMonthlyStat> listByUser(String userUsername);
    Map<String, List<UserMonthlyStat>> listAll();
}

