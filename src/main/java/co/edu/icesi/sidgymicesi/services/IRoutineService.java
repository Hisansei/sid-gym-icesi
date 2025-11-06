package co.edu.icesi.sidgymicesi.services;

import co.edu.icesi.sidgymicesi.model.*;
import java.util.*;

public interface IRoutineService {

    Routine create(String ownerUsername, String name, String sourceTemplateId);
    Optional<Routine> findById(String routineId);
    List<Routine> listByOwner(String ownerUsername);

    Routine addItem(String routineId, RoutineItem item);
    Routine removeItem(String routineId, String itemId);
    Routine reorder(String routineId, List<String> orderedItemIds);

    Routine rename(String routineId, String newName);
    void deleteById(String routineId);
}
