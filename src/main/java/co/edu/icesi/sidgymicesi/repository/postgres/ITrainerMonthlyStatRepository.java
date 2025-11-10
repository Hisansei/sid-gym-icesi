package co.edu.icesi.sidgymicesi.repository.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStatId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITrainerMonthlyStatRepository extends JpaRepository<TrainerMonthlyStat, TrainerMonthlyStatId> {
    List<TrainerMonthlyStat> findByIdTrainerUsernameOrderByIdPeriodDesc(String trainerUsername);
}
