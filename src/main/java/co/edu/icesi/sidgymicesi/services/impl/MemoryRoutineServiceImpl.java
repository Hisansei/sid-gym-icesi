package co.edu.icesi.sidgymicesi.services.impl;

import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.services.IRoutineService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service @Primary
public class MemoryRoutineServiceImpl implements IRoutineService {

    private final Map<String, Routine> store = new LinkedHashMap<>();

    @Override
    public Routine create(String ownerUsername, String name, String sourceTemplateId) {
        if (ownerUsername == null || ownerUsername.isBlank())
            throw new IllegalArgumentException("Usuario propietario requerido");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nombre de rutina requerido");

        Routine r = Routine.builder()
                .id(UUID.randomUUID().toString())
                .ownerUsername(ownerUsername)
                .name(name)
                .createdAt(LocalDateTime.now())
                .sourceTemplateId(sourceTemplateId)
                .status("ACTIVE")
                .items(new ArrayList<>())
                .build();
        store.put(r.getId(), r);
        return r;
    }

    @Override public Optional<Routine> findById(String routineId) { return Optional.ofNullable(store.get(routineId)); }

    @Override
    public List<Routine> listByOwner(String ownerUsername) {
        return store.values().stream()
                .filter(r -> Objects.equals(r.getOwnerUsername(), ownerUsername))
                .sorted(Comparator.comparing(Routine::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Routine addItem(String routineId, Routine.RoutineItem item) {
        Routine r = store.get(routineId);
        if (r == null) throw new IllegalArgumentException("Rutina no existe");
        if (item.getId() == null) item.setId(UUID.randomUUID().toString());
        if (item.getOrderIndex() == null) item.setOrderIndex(r.getItems().size() + 1);
        r.getItems().add(item);
        return r;
    }

    @Override
    public Routine removeItem(String routineId, String itemId) {
        Routine r = store.get(routineId);
        if (r == null) throw new IllegalArgumentException("Rutina no existe");
        r.getItems().removeIf(it -> Objects.equals(it.getId(), itemId));
        // normaliza orden
        int idx = 1;
        for (Routine.RoutineItem it : r.getItems()) it.setOrderIndex(idx++);
        return r;
    }

    @Override
    public Routine reorder(String routineId, List<String> orderedItemIds) {
        Routine r = store.get(routineId);
        if (r == null) throw new IllegalArgumentException("Rutina no existe");
        Map<String, Routine.RoutineItem> byId = r.getItems().stream()
                .collect(Collectors.toMap(Routine.RoutineItem::getId, it -> it));
        List<Routine.RoutineItem> ordered = new ArrayList<>();
        for (String id : orderedItemIds) {
            Routine.RoutineItem it = byId.get(id);
            if (it != null) ordered.add(it);
        }
        r.setItems(ordered);
        int idx = 1;
        for (Routine.RoutineItem it : r.getItems()) it.setOrderIndex(idx++);
        return r;
    }

    @Override
    public Routine rename(String routineId, String newName) {
        Routine r = store.get(routineId);
        if (r == null) throw new IllegalArgumentException("Rutina no existe");
        r.setName(newName);
        return r;
    }

    @Override public void deleteById(String routineId) { store.remove(routineId); }
}
