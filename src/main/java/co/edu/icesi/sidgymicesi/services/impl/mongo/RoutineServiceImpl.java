package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Exercise;
import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.model.mongo.RoutineTemplate;
import co.edu.icesi.sidgymicesi.repository.mongo.IExerciseRepository;
import co.edu.icesi.sidgymicesi.repository.mongo.IRoutineRepository;
import co.edu.icesi.sidgymicesi.repository.mongo.IRoutineTemplateRepository;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineService;
import co.edu.icesi.sidgymicesi.services.postgres.IUserMonthlyStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoutineServiceImpl implements IRoutineService {

    private static final List<String> ALLOWED_TYPES = List.of("cardio", "fuerza", "movilidad");

    private final IRoutineRepository routineRepo;
    private final IRoutineTemplateRepository templateRepo;
    private final IExerciseRepository exerciseRepo;
    private final IUserMonthlyStatsService userStatsService;

    // ========== CREATE ==========

    @Override
    public Routine create(String ownerUsername, String name, String originTemplateId) {
        if (ownerUsername == null || ownerUsername.isBlank()) throw new IllegalArgumentException("ownerUsername requerido");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name requerido");

        Routine r = Routine.builder()
                .ownerUsername(ownerUsername.trim())
                .name(name.trim())
                .sourceTemplateId(isBlank(originTemplateId) ? null : originTemplateId)
                .createdAt(Instant.now())
                .status(true)
                .exercises(new ArrayList<>())
                .build();

        // Si viene de plantilla, copiamos los ejercicios
        if (!isBlank(originTemplateId)) {
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
                        .status(true)
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

        // Guardar rutina y actualizar estadísticas
        Routine saved = routineRepo.save(r);

        try {
            userStatsService.incrementRoutinesStarted(ownerUsername, YearMonth.now());
        } catch (RuntimeException ex) {
            // Compensación simple en caso de fallo en Postgres
            routineRepo.deleteById(saved.getId());
            throw ex;
        }
        return saved;
    }

    @Override
    public Routine createFromTemplate(String ownerUsername, RoutineTemplate template) {
        return create(ownerUsername, template.getName(), template.getId());
    }

    // ========== READ ==========

    @Override
    public List<Routine> listByOwner(String ownerUsername) {
        return routineRepo.findByOwnerUsernameOrderByCreatedAtDesc(ownerUsername);
    }

    @Override
    public Optional<Routine> findById(String id) {
        return routineRepo.findById(id);
    }

    // ========== UPDATE: ADD ITEM ==========

    @Override
    public Routine addItem(String routineId, Routine.RoutineExercise newItem) {
        Routine r = routineRepo.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada: " + routineId));

        if (r.getExercises() == null) {
            r.setExercises(new ArrayList<>());
        }

        // Lógica de llenado de datos si es del catálogo
        if (newItem.getExerciseId() != null) {
            Exercise ex = exerciseRepo.findById(newItem.getExerciseId())
                    .orElseThrow(() -> new IllegalArgumentException("Ejercicio ID no existe: " + newItem.getExerciseId()));

            if (newItem.getName() == null) newItem.setName(ex.getName());
            if (newItem.getType() == null) newItem.setType(ex.getType());
            if (newItem.getDescription() == null) newItem.setDescription(ex.getDescription());
            if (newItem.getDurationSeconds() == null) newItem.setDurationSeconds(ex.getDurationSeconds());
            if (newItem.getDifficulty() == null) newItem.setDifficulty(ex.getDifficulty());
            if (newItem.getDemoVideos() == null) newItem.setDemoVideos(ex.getDemoVideos());
        } else {
            // Personalizado: requiere name + type (Corrección del bug principal)
            if (isBlank(newItem.getName()) || isBlank(newItem.getType())) {
                throw new IllegalArgumentException("Ejercicio personalizado requiere name y type");
            }
        }

        newItem.setStatus(true);
        if (isBlank(newItem.getId())) newItem.setId(UUID.randomUUID().toString());
        newItem.setOrder(nextOrder(r));

        validateItem(newItem);

        r.getExercises().add(newItem);
        return routineRepo.save(r);
    }

    // ========== UPDATE: REMOVE ITEM ==========

    @Override
    public Routine removeItem(String routineId, String itemId) {
        Routine r = routineRepo.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada: " + routineId));

        if (r.getExercises() == null || r.getExercises().isEmpty()) {
            throw new IllegalStateException("La rutina no tiene ejercicios.");
        }

        long activeCount = r.getExercises().stream().filter(Routine.RoutineExercise::isStatus).count();
        Optional<Routine.RoutineExercise> itemOpt = r.getExercises().stream()
                .filter(it -> Objects.equals(it.getId(), itemId))
                .findFirst();

        if (!itemOpt.isPresent()) {
            throw new NoSuchElementException("Ejercicio no encontrado en la rutina: " + itemId);
        }

        Routine.RoutineExercise item = itemOpt.get();
        if (item.isStatus() && activeCount <= 1) {
            throw new IllegalStateException("No se puede dejar una rutina sin ejercicios.");
        }

        item.setStatus(false); // Soft delete del item para mantener historial
        normalizeOrder(r.getExercises());
        return routineRepo.save(r);
    }

    // ========== UPDATE: REORDER ==========

    @Override
    public Routine reorderExercises(String routineId, List<String> orderedIds) {
        Routine r = routineRepo.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada: " + routineId));

        if (r.getExercises() == null) return r;

        Map<String, Routine.RoutineExercise> map = r.getExercises().stream()
                .collect(Collectors.toMap(Routine.RoutineExercise::getId, e -> e));

        int order = 1;
        for (String id : orderedIds) {
            if (map.containsKey(id)) {
                map.get(id).setOrder(order++);
            }
        }
        return routineRepo.save(r);
    }

    // ========== UPDATE: RENAME ==========

    @Override
    public Routine rename(String routineId, String newName) {
        if (isBlank(newName)) throw new IllegalArgumentException("newName requerido");
        Routine r = routineRepo.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada: " + routineId));
        r.setName(newName.trim());
        return routineRepo.save(r);
    }

    // ========== DELETE (HARD DELETE SOLICITADO) ==========

    @Override
    public void deleteById(String id) {
        if (!routineRepo.existsById(id)) {
            throw new NoSuchElementException("Rutina no encontrada: " + id);
        }
        // CORRECCIÓN: Borrado real de la BD
        routineRepo.deleteById(id);
    }

    // ======= Helpers =======

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static int nextOrder(Routine r) {
        if (r.getExercises() == null || r.getExercises().isEmpty()) return 1;
        return r.getExercises().stream()
                .filter(Routine.RoutineExercise::isStatus)
                .mapToInt(Routine.RoutineExercise::getOrder)
                .max()
                .orElse(0) + 1;
    }

    private static void normalizeOrder(List<Routine.RoutineExercise> items) {
        if (items == null) return;
        List<Routine.RoutineExercise> active = items.stream()
                .filter(Routine.RoutineExercise::isStatus)
                .sorted(Comparator.comparingInt(Routine.RoutineExercise::getOrder))
                .collect(Collectors.toList());
        int i = 1;
        for (Routine.RoutineExercise e : active) {
            e.setOrder(i++);
        }
    }

    private void validateItem(Routine.RoutineExercise it) {
        if (it.getType() != null && !ALLOWED_TYPES.contains(it.getType().trim().toLowerCase())) {
            throw new IllegalArgumentException("type inválido (permitidos: cardio, fuerza, movilidad)");
        }
        if (it.getSets() < 0) throw new IllegalArgumentException("sets no puede ser negativo");
        if (it.getReps() < 0) throw new IllegalArgumentException("reps no puede ser negativo");
        if (it.getRestSeconds() < 0) throw new IllegalArgumentException("restSeconds no puede ser negativo");
        if (it.getDurationSeconds() != null && it.getDurationSeconds() < 0) {
            throw new IllegalArgumentException("durationSeconds no puede ser negativo");
        }
    }
}