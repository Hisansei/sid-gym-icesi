package co.edu.icesi.sidgymicesi.repository.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.UserMonthlyStat;
import co.edu.icesi.sidgymicesi.model.postgres.UserMonthlyStatId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserMonthlyStatRepository extends JpaRepository<UserMonthlyStat, UserMonthlyStatId> {
    List<UserMonthlyStat> findByIdUserUsernameOrderByIdPeriodDesc(String userUsername);
}
