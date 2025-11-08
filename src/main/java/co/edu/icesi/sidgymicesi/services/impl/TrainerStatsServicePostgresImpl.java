package co.edu.icesi.sidgymicesi.services.impl;

import co.edu.icesi.sidgymicesi.model.TrainerMonthlyStats;
import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStat;
import co.edu.icesi.sidgymicesi.model.postgres.TrainerMonthlyStatId;
import co.edu.icesi.sidgymicesi.repository.ITrainerMonthlyStatRepository;
import co.edu.icesi.sidgymicesi.services.ITrainerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class TrainerStatsServicePostgresImpl implements ITrainerStatsService {

    private final ITrainerMonthlyStatRepository repo;

    @Override
    public void registerNewAssignment(String trainerUsername, YearMonth period) {
        var id = new TrainerMonthlyStatId(trainerUsername, period.toString());
        var entity = repo.findById(id).orElseGet(() ->
                TrainerMonthlyStat.builder().id(id).newAssignments(0).build());
        entity.setNewAssignments(entity.getNewAssignments() + 1);
        repo.save(entity);
    }

    @Override
    public Optional<TrainerMonthlyStats> get(String trainerUsername, YearMonth period) {
        return repo.findById(new TrainerMonthlyStatId(trainerUsername, period.toString()))
                .map(this::toModel);
    }

    @Override
    public List<TrainerMonthlyStats> listByTrainer(String trainerUsername) {
        return repo.findByIdTrainerUsernameOrderByIdPeriodDesc(trainerUsername).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<TrainerMonthlyStats>> listAll() {
        return repo.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.groupingBy(
                        TrainerMonthlyStats::getTrainerUsername,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    list.sort(Comparator.comparing(TrainerMonthlyStats::getPeriod).reversed());
                                    return list;
                                }
                        )));
    }

    private TrainerMonthlyStats toModel(TrainerMonthlyStat e) {
        return TrainerMonthlyStats.builder()
                .trainerUsername(e.getId().getTrainerUsername())
                .period(YearMonth.parse(e.getId().getPeriod()))
                .newAssignments(e.getNewAssignments())
                .build();
    }
}
