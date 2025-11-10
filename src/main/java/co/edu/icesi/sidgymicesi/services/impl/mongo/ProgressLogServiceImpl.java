package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.repository.mongo.IProgressLogRepository;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogService;
import co.edu.icesi.sidgymicesi.services.postgres.IUserMonthlyStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressLogServiceImpl implements IProgressLogService {

    private final IProgressLogRepository progressRepo;
    private final IUserMonthlyStatsService userStatsService;

    @Override
    public ProgressLog addLog(ProgressLog log) {
        if (log.getDate() == null) log.setDate(LocalDate.now());
        ProgressLog saved = progressRepo.save(log);

        YearMonth ym = YearMonth.from(saved.getDate());
        userStatsService.incrementFollowupsMade(saved.getOwnerUsername(), ym);

        return saved;
    }

    @Override
    public List<ProgressLog> listByRoutine(String routineId) {
        return progressRepo.findAllByRoutineIdOrderByDateDesc(routineId);
    }

    @Override
    public List<ProgressLog> listByOwner(String ownerUsername) {
        return progressRepo.findAllByOwnerUsernameOrderByDateDesc(ownerUsername);
    }

    @Override
    public Optional<ProgressLog> findByRoutineAndDate(String routineId, LocalDate date) {
        return progressRepo.findByRoutineIdAndDate(routineId, date);
    }

    @Override
    public void deleteLog(String logId) {
        progressRepo.deleteById(logId);
    }
}
