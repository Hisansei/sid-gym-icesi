package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.model.mongo.RoutineTemplate;
import co.edu.icesi.sidgymicesi.repository.mongo.IExerciseRepository;
import co.edu.icesi.sidgymicesi.repository.mongo.IRoutineRepository;
import co.edu.icesi.sidgymicesi.repository.mongo.IRoutineTemplateRepository;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import co.edu.icesi.sidgymicesi.services.postgres.IUserMonthlyStatsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RoutineServiceImpl implements IRoutineService {

    private final IRoutineRepository routineRepo;
    private final IRoutineTemplateRepository templateRepo;
    private final IExerciseRepository exerciseRepo;
    private final IUserMonthlyStatsService userStatsService;

    @Override
    public Routine create(String ownerUsername, String name, String originTemplateId) {
        if (ownerUsername == null || ownerUsername.isBlank()) throw new IllegalArgumentException("ownerUsername requerido");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name requerido");

        Routine r = Routine.builder()
                .ownerUsername(ownerUsername.trim())
                .name(name.trim())
                .sourceTemplateId(originTemplateId)
                .createdAt(Instant.now())
                .status(true)
                .exercises(new ArrayList<>())
                .build();

        // Si clona desde plantilla: copiar ejercicios + targets
        if (originTemplateId != null && !originTemplateId.isBlank()) {
            RoutineTemplate tpl = templateRepo.findById(originTemplateId)
                    .orElseThrow(() -> new NoSuchElementException("Plantilla no encontrada: " + originTemplateId));

            List<Routine.RoutineExercise> items = new ArrayList<>();
            int order = 1;
            for (RoutineTemplate.TemplateItem ti : tpl.getExercises()) {
                Exercise ex = exerciseRepo.findById(ti.getExerciseId()).orElse(null);

                Routine.RoutineExercise item = Routine.RoutineExercise.builder()
                        .id(UUID.randomUUID().toString())
                        .order(order++)
                        .exerciseId(ti.getExerciseId())
                        .name(ex != null ? ex.getName() : null)
                        .type(ex != null ? ex.getType() : null)
                        .description(ex != null ? ex.getDescription() : null)
                        .durationSeconds(ex != null ? ex.getDurationSeconds() : null)
                        .difficulty(ex != null ? ex.getDifficulty() : null)
                        .demoVideos(ex != null ? ex.getDemoVideos() : null)
                        .sets(ti.getSets())
                        .reps(ti.getReps())
                        .restSeconds(ti.getRestSeconds())
                        .build();

                validateItem(item);
                items.add(item);
            }
            r.setExercises(items);
        }

        Routine saved = routineRepo.save(r);
        userStatsService.incrementRoutinesStarted(ownerUsername, YearMonth.now());
        return saved;
    }

    @Override public Optional<Routine> findById(String id) { return routineRepo.findById(id); }
    @Override public List<Routine> listByOwner(String ownerUsername) { return routineRepo.findByOwnerUsernameOrderByCreatedAtDesc(ownerUsername); }

    @Override
    public Routine addItem(String routineId, Routine.RoutineExercise newItem) {
        Routine r = routineRepo.findById(routineId).orElseThrow(() -> new NoSuchElementException("Rutina no encontrada"));

        // Enriquecer metadatos del ejercicio si viene exerciseId
        if (newItem.getExerciseId() != null && !newItem.getExerciseId().isBlank()) {
            exerciseRepo.findById(newItem.getExerciseId()).ifPresent(ex -> {
                newItem.setName(ex.getName());
                newItem.setType(ex.getType());
                newItem.setDescription(ex.getDescription());
                newItem.setDurationSeconds(ex.getDurationSeconds());
                newItem.setDifficulty(ex.getDifficulty());
                newItem.setDemoVideos(ex.getDemoVideos());
            });
        }
        newItem.setId(UUID.randomUUID().toString());
        newItem.setOrder(nextOrder(r));
        validateItem(newItem);

        r.getExercises().add(newItem);
        return routineRepo.save(r);
    }

    @Override
    public Routine removeItem(String routineId, String itemId) {
        Routine r = routineRepo.findById(routineId).orElseThrow(() -> new NoSuchElementException("Rutina no encontrada"));
        r.getExercises().removeIf(e -> Objects.equals(e.getId(), itemId));
        recompactOrders(r);
        return routineRepo.save(r);
    }

    @Override
    public Routine reorderExercises(String routineId, List<String> orderedIds) {
        Routine r = routineRepo.findById(routineId).orElseThrow(() -> new NoSuchElementException("Rutina no encontrada"));
        Map<String, Integer> pos = new LinkedHashMap<>();
        int i = 1;
        for (String id : orderedIds) pos.put(id, i++);
        r.getExercises().forEach(e -> e.setOrder(pos.getOrDefault(e.getId(), e.getOrder())));
        r.getExercises().sort(Comparator.comparingInt(Routine.RoutineExercise::getOrder));
        return routineRepo.save(r);
    }

    @Override
    public Routine rename(String routineId, String newName) {
        Routine r = routineRepo.findById(routineId).orElseThrow(() -> new NoSuchElementException("Rutina no encontrada"));
        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("newName requerido");
        r.setName(newName.trim());
        return routineRepo.save(r);
    }

    @Override public void deleteById(String id) { routineRepo.deleteById(id); }

    // --- helpers ---
    private static int nextOrder(Routine r) {
        return r.getExercises().stream().mapToInt(Routine.RoutineExercise::getOrder).max().orElse(0) + 1;
    }

    private static void recompactOrders(Routine r) {
        int o = 1;
        for (Routine.RoutineExercise e : r.getExercises().stream()
                .sorted(Comparator.comparingInt(Routine.RoutineExercise::getOrder)).toList()) {
            e.setOrder(o++);
        }
    }

    private static void validateItem(Routine.RoutineExercise it) {
        if (it.getSets() < 0 || it.getReps() < 0) throw new IllegalArgumentException("sets/reps no pueden ser negativos");
        if (it.getDurationSeconds() != null && it.getDurationSeconds() < 0) throw new IllegalArgumentException("durationSeconds inválido");
    }
}
