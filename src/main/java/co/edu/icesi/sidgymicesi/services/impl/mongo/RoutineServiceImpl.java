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

import java.time.Instant;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineServiceImpl implements IRoutineService {

    private static final int MAX_EXERCISES = 50;
    private static final Set<String> ALLOWED_TYPES = Set.of("cardio", "fuerza", "movilidad");

    private final IRoutineRepository routineRepo;
    private final IRoutineTemplateRepository templateRepo;
    private final IExerciseRepository exerciseRepo;
    private final IUserMonthlyStatsService userStatsService;

    // ========== CREATE ==========
    @Override
    public Routine create(String ownerUsername, String name, String originTemplateId) {
        if (ownerUsername == null || ownerUsername.isBlank())
            throw new IllegalArgumentException("ownerUsername requerido");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("name requerido");

        Routine r = Routine.builder()
                .ownerUsername(ownerUsername.trim())
                .name(name.trim())
                .sourceTemplateId(isBlank(originTemplateId) ? null : originTemplateId)
                .createdAt(Instant.now())
                .status(true)
                .exercises(new ArrayList<>())
                .build();

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

        // Guardar rutina y actualizar estadísticas con compensación
        Routine saved = routineRepo.save(r);
        try {
            userStatsService.incrementRoutinesStarted(ownerUsername, YearMonth.now());
        } catch (RuntimeException ex) {
            // Compensación: revertir rutina creada si falla Postgres
            routineRepo.deleteById(saved.getId());
            throw ex;
        }
        return saved;
    }

    @Override
    public Routine createFromTemplate(String ownerUsername, RoutineTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("template requerido");
        }
        String templateId = template.getId();
        if (templateId == null || templateId.isBlank()) {
            throw new IllegalArgumentException("template sin id");
        }

        String name = (template.getName() != null && !template.getName().isBlank())
                ? template.getName()
                : "Rutina desde plantilla";

        // Reutilizamos la lógica que ya llena los ejercicios desde la plantilla
        return create(ownerUsername, name, templateId);
    }

    // ========== READ ==========
    @Override
    public Optional<Routine> findById(String id) { return routineRepo.findById(id); }

    @Override
    public List<Routine> listByOwner(String ownerUsername) {
        return routineRepo.findByOwnerUsernameOrderByCreatedAtDesc(ownerUsername);
    }

    // ========== UPDATE: ADD ITEM ==========
    @Override
    public Routine addItem(String routineId, Routine.RoutineExercise newItem) {
        Routine r = routineRepo.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada: " + routineId));

        if (r.getExercises() == null) r.setExercises(new ArrayList<>());
        if (r.getExercises().size() >= MAX_EXERCISES) {
            throw new IllegalStateException("La rutina ya tiene el máximo de " + MAX_EXERCISES + " ejercicios");
        }

        // Si viene de catálogo, rellenar campos desde Exercise (si faltan)
        if (!isBlank(newItem.getExerciseId())) {
            Exercise ex = exerciseRepo.findById(newItem.getExerciseId())
                    .orElseThrow(() -> new NoSuchElementException("Ejercicio no encontrado: " + newItem.getExerciseId()));
            if (newItem.getName() == null) newItem.setName(ex.getName());
            if (newItem.getType() == null) newItem.setType(ex.getType());
            if (newItem.getDescription() == null) newItem.setDescription(ex.getDescription());
            if (newItem.getDurationSeconds() == null) newItem.setDurationSeconds(ex.getDurationSeconds());
            if (newItem.getDifficulty() == null) newItem.setDifficulty(ex.getDifficulty());
            if (newItem.getDemoVideos() == null) newItem.setDemoVideos(ex.getDemoVideos());
        } else {
            // Personalizado: requiere name + type
            if (isBlank(newItem.getName()) || isBlank(newItem.getType())) {
                throw new IllegalArgumentException("Ejercicio personalizado requiere name y type");
            }
        }

        newItem.setStatus(true);

        // Asignar id y orden
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

        // Contar solo ejercicios ACTIVOS
        long activeCount = r.getExercises().stream()
                .filter(Routine.RoutineExercise::isStatus)
                .count();

        Optional<Routine.RoutineExercise> itemOpt = r.getExercises().stream()
                .filter(it -> Objects.equals(it.getId(), itemId))
                .findFirst();

        if (!itemOpt.isPresent()) {
            throw new NoSuchElementException("Ejercicio no encontrado en la rutina: " + itemId);
        }

        Routine.RoutineExercise item = itemOpt.get();

        // Si el ejercicio que quiero borrar está activo y es el único → NO permitir
        if (item.isStatus() && activeCount <= 1) {
            throw new IllegalStateException(
                    "No se puede dejar una rutina sin ejercicios. Agrega otro ejercicio antes de quitar este."
            );
        }

        // Soft delete del ejercicio
        item.setStatus(false);

        // Normalizamos el orden SOLO de los ejercicios activos
        normalizeOrder(r.getExercises());

        return routineRepo.save(r);
    }

    // ========== UPDATE: REORDER ==========
    @Override
    public Routine reorderExercises(String routineId, List<String> orderedIds) {
        Routine r = routineRepo.findById(routineId)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada: " + routineId));
        if (r.getExercises() == null) r.setExercises(new ArrayList<>());

        List<Routine.RoutineExercise> all = r.getExercises();

        List<Routine.RoutineExercise> active = all.stream()
                .filter(Routine.RoutineExercise::isStatus)
                .collect(Collectors.toList());

        List<Routine.RoutineExercise> inactive = all.stream()
                .filter(e -> !e.isStatus())
                .collect(Collectors.toList());

        Set<String> current = active.stream()
                .map(Routine.RoutineExercise::getId)
                .collect(Collectors.toSet());
        Set<String> target = new HashSet<>(orderedIds);

        if (current.size() != orderedIds.size() || !current.equals(target)) {
            throw new IllegalArgumentException("La lista de IDs no coincide con los ejercicios activos actuales");
        }

        Map<String, Routine.RoutineExercise> byId = active.stream()
                .collect(Collectors.toMap(Routine.RoutineExercise::getId, e -> e));

        List<Routine.RoutineExercise> reorderedActive = new ArrayList<>();
        int order = 1;
        for (String id : orderedIds) {
            Routine.RoutineExercise e = byId.get(id);
            e.setOrder(order++);
            reorderedActive.add(e);
        }

        // Opcional: reasignar órdenes consecutivos también a inactivos después
        for (Routine.RoutineExercise e : inactive) {
            e.setOrder(order++);
        }

        List<Routine.RoutineExercise> newList = new ArrayList<>();
        newList.addAll(reorderedActive);
        newList.addAll(inactive);

        r.setExercises(newList);
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

    // ========== DELETE ==========
    @Override
    public void deleteById(String id) {
        // Buscar la rutina por ID
        Routine routine = routineRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rutina no encontrada: " + id));

        // (Soft delete)
        routine.setStatus(false);

        for (Routine.RoutineExercise exercise : routine.getExercises()) {
            exercise.setStatus(false);
        }

        routineRepo.save(routine);
    }

    // ======= Helpers =======
    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static int nextOrder(Routine r) {
        if (r.getExercises() == null || r.getExercises().isEmpty()) {
            return 1;
        }
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
        // Tipo permitido si viene
        if (it.getType() != null && !ALLOWED_TYPES.contains(it.getType().trim().toLowerCase())) {
            throw new IllegalArgumentException("type inválido (permitidos: cardio, fuerza, movilidad)");
        }
        // sets/reps/restSeconds son int (no nullables) en el modelo; validar no-negativos
        if (it.getSets() < 0) throw new IllegalArgumentException("sets no puede ser negativo");
        if (it.getReps() < 0) throw new IllegalArgumentException("reps no puede ser negativo");
        if (it.getRestSeconds() < 0) throw new IllegalArgumentException("restSeconds no puede ser negativo");
        // durationSeconds es Integer: si viene, no-negativo
        if (it.getDurationSeconds() != null && it.getDurationSeconds() < 0) {
            throw new IllegalArgumentException("durationSeconds no puede ser negativo");
        }
    }
}