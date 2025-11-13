package co.edu.icesi.sidgymicesi.repository.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ITrainerMonthlyStatRepository extends JpaRepository<TrainerMonthlyStat, TrainerMonthlyStatId> {
        
        List<TrainerMonthlyStat> findByIdTrainerUsernameOrderByIdPeriodDesc(String trainerUsername);

        @Modifying
        @Query(
                value = """
                INSERT INTO trainer_monthly_stats (trainer_username, period, new_assignments)
                VALUES (:trainerUsername, :period, 1)
                ON CONFLICT (trainer_username, period)
                DO UPDATE SET new_assignments = trainer_monthly_stats.new_assignments + 1
                """,
                nativeQuery = true
        )
        void upsertIncrementNewAssignments(@Param("trainerUsername") String trainerUsername,
                                        @Param("period") String period);

        @Modifying
        @Query(
                value = """
                INSERT INTO trainer_monthly_stats (trainer_username, period, followups_made)
                VALUES (:trainerUsername, :period, 1)
                ON CONFLICT (trainer_username, period)
                DO UPDATE SET followups_made = trainer_monthly_stats.followups_made + 1
                """,
                nativeQuery = true
        )
        void upsertIncrementFollowupsMade(@Param("trainerUsername") String trainerUsername,
                                        @Param("period") String period);

}