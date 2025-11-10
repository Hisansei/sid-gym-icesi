package co.edu.icesi.sidgymicesi.services.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Routine;

import java.util.List;
import java.util.Optional;

public interface IRoutineService {

    //
    Routine create(String ownerUsername, String name, String originTemplateId);

    Optional<Routine> findById(String id);
    List<Routine> listByOwner(String ownerUsername);

    //
    Routine addItem(String routineId, Routine.RoutineExercise newItem);
    Routine removeItem(String routineId, String itemId);
    Routine reorderExercises(String routineId, List<String> orderedIds);
    Routine rename(String routineId, String newName);

    void deleteById(String id);
}
