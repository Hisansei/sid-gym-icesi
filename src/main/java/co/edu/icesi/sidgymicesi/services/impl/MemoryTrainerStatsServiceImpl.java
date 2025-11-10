package co.edu.icesi.sidgymicesi.services.impl;

import co.edu.icesi.sidgymicesi.model.TrainerMonthlyStats;
import co.edu.icesi.sidgymicesi.services.ITrainerStatsService;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;

@Service
public class MemoryTrainerStatsServiceImpl implements ITrainerStatsService {

    private final Map<String, TrainerMonthlyStats> store = new LinkedHashMap<>();
    private String key(String trainer, YearMonth ym) { return trainer + "::" + ym; }

    @Override
    public void registerNewAssignment(String trainerUsername, YearMonth period) {
        String k = key(trainerUsername, period);
        TrainerMonthlyStats s = store.get(k);
        if (s == null) {
            s = TrainerMonthlyStats.builder()
                    .trainerUsername(trainerUsername)
                    .period(period)
                    .newAssignments(0)
                    .build();
            store.put(k, s);
        }
        s.setNewAssignments(s.getNewAssignments() + 1);
    }

    @Override public Optional<TrainerMonthlyStats> get(String t, YearMonth p) {
        return Optional.ofNullable(store.get(key(t, p)));
    }

    @Override
    public List<TrainerMonthlyStats> listByTrainer(String t) {
        List<TrainerMonthlyStats> out = new ArrayList<>();
        for (var e : store.entrySet()) {
            if (e.getKey().startsWith(t + "::")) out.add(e.getValue());
        }
        out.sort(Comparator.comparing(TrainerMonthlyStats::getPeriod).reversed());
        return out;
    }

    @Override
    public Map<String, List<TrainerMonthlyStats>> listAll() {
        Map<String, List<TrainerMonthlyStats>> grouped = new LinkedHashMap<>();
        for (var s : store.values()) {
            grouped.computeIfAbsent(s.getTrainerUsername(), k -> new ArrayList<>()).add(s);
        }
        for (var list : grouped.values()) {
            list.sort(Comparator.comparing(TrainerMonthlyStats::getPeriod).reversed());
        }
        return grouped;
    }
}
