package co.edu.icesi.sidgymicesi.services.impl;

import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.IRoutineService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MemoryRoutineServiceImpl implements IRoutineService {

    private final Map<String, Routine> store = new ConcurrentHashMap<>();

    @Override
    public Routine create(String ownerUsername, String name, String originTemplateId) {
        Routine r = Routine.builder()
                .id(UUID.randomUUID().toString())
                .ownerUsername(ownerUsername)
                .name(name)
                .sourceTemplateId(originTemplateId)
                .createdAt(Instant.now())
                .status(true)
                .exercises(new ArrayList<>())
                .build();
        store.put(r.getId(), r);
        return r;
    }

    @Override
    public Optional<Routine> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Routine> listByOwner(String ownerUsername) {
        return store.values().stream()
                .filter(r -> Objects.equals(r.getOwnerUsername(), ownerUsername))
                .sorted(Comparator.comparing(Routine::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Routine addItem(String routineId, Routine.RoutineExercise item) {
        Routine r = store.get(routineId);
        if (r == null) throw new NoSuchElementException("Routine not found: " + routineId);

        if (item.getId() == null || item.getId().isBlank()) {
            item.setId(UUID.randomUUID().toString());
        }
        // Si no viene 'order', lo ponemos al final.
        if (item.getOrder() <= 0) {
            int next = r.getExercises().stream()
                    .mapToInt(Routine.RoutineExercise::getOrder)
                    .max().orElse(0) + 1;
            item.setOrder(next);
        }
        r.getExercises().add(item);
        // Normalizamos el orden
        r.getExercises().sort(Comparator.comparingInt(Routine.RoutineExercise::getOrder));
        return r;
    }

    @Override
    public Routine removeItem(String routineId, String itemId) {
        Routine r = store.get(routineId);
        if (r == null) throw new NoSuchElementException("Routine not found: " + routineId);
        r.setExercises(
                r.getExercises().stream()
                        .filter(it -> !Objects.equals(it.getId(), itemId))
                        .collect(Collectors.toList())
        );
        return r;
    }

    @Override
    public Routine reorder(String routineId, List<String> orderedItemIds) {
        Routine r = store.get(routineId);
        if (r == null) throw new NoSuchElementException("Routine not found: " + routineId);

        Map<String, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < orderedItemIds.size(); i++) {
            orderMap.put(orderedItemIds.get(i), i + 1);
        }
        r.getExercises().forEach(it ->
                it.setOrder(orderMap.getOrDefault(it.getId(), it.getOrder()))
        );
        r.getExercises().sort(Comparator.comparingInt(Routine.RoutineExercise::getOrder));
        return r;
    }

    @Override
    public Routine rename(String routineId, String newName) {
        Routine r = store.get(routineId);
        if (r == null) throw new NoSuchElementException("Routine not found: " + routineId);
        r.setName(newName);
        return r;
    }

    @Override
    public void deleteById(String routineId) {
        store.remove(routineId);
    }
}
