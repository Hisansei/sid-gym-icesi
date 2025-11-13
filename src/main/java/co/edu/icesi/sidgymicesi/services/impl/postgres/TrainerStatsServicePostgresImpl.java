package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStatId;
import co.edu.icesi.sidgymicesi.repository.postgres.ITrainerMonthlyStatRepository;
import co.edu.icesi.sidgymicesi.services.postgres.ITrainerStatsService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;

@Service
@Primary
@Transactional
public class TrainerStatsServicePostgresImpl implements ITrainerStatsService {

    private final ITrainerMonthlyStatRepository repo;

    public TrainerStatsServicePostgresImpl(ITrainerMonthlyStatRepository repo) {
        this.repo = repo;
    }

    private static String ym(YearMonth p) { return p.toString(); } // YYYY-MM

    private TrainerMonthlyStat ensure(String trainerUsername, YearMonth period) {
        TrainerMonthlyStatId id = new TrainerMonthlyStatId(trainerUsername, ym(period));
        return repo.findById(id).orElseGet(() -> repo.save(
                TrainerMonthlyStat.builder()
                        .id(id)
                        .newAssignments(0)
                        .build()
        ));
    }

    @Override
    public void registerNewAssignment(String trainerUsername, YearMonth period) {
        TrainerMonthlyStat s = ensure(trainerUsername, period);
        s.setNewAssignments(s.getNewAssignments() + 1);
        repo.save(s);
    }

    @Override
    public void registerFollowUp(String trainerUsername, YearMonth period) {
        TrainerMonthlyStat s = ensure(trainerUsername, period);
        s.setFollowupsMade(s.getFollowupsMade() + 1);
        repo.save(s);
    }

    @Override
    public Optional<TrainerMonthlyStat> get(String t, YearMonth p) {
        return repo.findById(new TrainerMonthlyStatId(t, ym(p)));
    }

    @Override
    public List<TrainerMonthlyStat> listByTrainer(String t) {
        return repo.findByIdTrainerUsernameOrderByIdPeriodDesc(t);
    }

    @Override
    public Map<String, List<TrainerMonthlyStat>> listAll() {
        Map<String, List<TrainerMonthlyStat>> out = new LinkedHashMap<>();
        for (var s : repo.findAll()) {
            out.computeIfAbsent(s.getId().getTrainerUsername(), k -> new ArrayList<>()).add(s);
        }
        out.values().forEach(list ->
                list.sort(Comparator.comparing(e -> e.getId().getPeriod(), Comparator.reverseOrder()))
        );
        return out;
    }
}