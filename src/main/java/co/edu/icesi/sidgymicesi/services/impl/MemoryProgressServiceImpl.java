package co.edu.icesi.sidgymicesi.services.impl;

import co.edu.icesi.sidgymicesi.model.ProgressLog;
import co.edu.icesi.sidgymicesi.services.IProgressService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service @Primary
public class MemoryProgressServiceImpl implements IProgressService {

    private final Map<String, ProgressLog> store = new LinkedHashMap<>();

    @Override
    public ProgressLog addLog(ProgressLog log) {
        if (log.getId() == null) log.setId(UUID.randomUUID().toString());
        if (log.getDate() == null) log.setDate(LocalDate.now());
        store.put(log.getId(), log);
        return log;
    }

    @Override
    public List<ProgressLog> listByRoutine(String routineId) {
        return store.values().stream()
                .filter(p -> Objects.equals(p.getRoutineId(), routineId))
                .sorted(Comparator.comparing(ProgressLog::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<ProgressLog> listByOwner(String ownerUsername) {
        return store.values().stream()
                .filter(p -> Objects.equals(p.getOwnerUsername(), ownerUsername))
                .sorted(Comparator.comparing(ProgressLog::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProgressLog> findByRoutineAndDate(String routineId, LocalDate date) {
        return store.values().stream()
                .filter(p -> Objects.equals(p.getRoutineId(), routineId) && Objects.equals(p.getDate(), date))
                .findFirst();
    }

    @Override public void deleteLog(String logId) { store.remove(logId); }
}
