package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.UserMonthlyStat;
import co.edu.icesi.sidgymicesi.model.postgres.UserMonthlyStatId;
import co.edu.icesi.sidgymicesi.repository.postgres.IUserMonthlyStatRepository;
import co.edu.icesi.sidgymicesi.services.postgres.IUserMonthlyStatsService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;

@Service
@Transactional
public class UserMonthlyStatsServicePostgresImpl implements IUserMonthlyStatsService {

    private final IUserMonthlyStatRepository repo;

    public UserMonthlyStatsServicePostgresImpl(IUserMonthlyStatRepository repo) {
        this.repo = repo;
    }

    private static String ym(YearMonth p) { return p.toString(); } // YYYY-MM

    private UserMonthlyStat ensure(String userUsername, YearMonth period) {
        UserMonthlyStatId id = new UserMonthlyStatId(userUsername, ym(period));
        return repo.findById(id).orElseGet(() -> repo.save(
                UserMonthlyStat.builder()
                        .id(id)
                        .routinesStarted(0)
                        .followupsMade(0)
                        .build()
        ));
    }

    @Override
    public void incrementRoutinesStarted(String userUsername, YearMonth period) {
        UserMonthlyStat s = ensure(userUsername, period);
        s.setRoutinesStarted(s.getRoutinesStarted() + 1);
        repo.save(s);
    }

    @Override
    public void incrementFollowupsMade(String userUsername, YearMonth period) {
        UserMonthlyStat s = ensure(userUsername, period);
        s.setFollowupsMade(s.getFollowupsMade() + 1);
        repo.save(s);
    }

    @Override public Optional<UserMonthlyStat> get(String userUsername, YearMonth period) {
        return repo.findById(new UserMonthlyStatId(userUsername, ym(period)));
    }

    @Override public List<UserMonthlyStat> listByUser(String userUsername) {
        return repo.findByIdUserUsernameOrderByIdPeriodDesc(userUsername);
    }

    @Override
    public Map<String, List<UserMonthlyStat>> listAll() {
        Map<String, List<UserMonthlyStat>> out = new LinkedHashMap<>();
        for (var s : repo.findAll()) {
            out.computeIfAbsent(s.getId().getUserUsername(), k -> new ArrayList<>()).add(s);
        }
        out.values().forEach(list ->
                list.sort(Comparator.comparing(e -> e.getId().getPeriod(), Comparator.reverseOrder()))
        );
        return out;
    }
}
