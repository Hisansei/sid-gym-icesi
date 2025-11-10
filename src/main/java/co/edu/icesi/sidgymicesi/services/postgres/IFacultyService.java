package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Faculty;

import java.util.List;
import java.util.Optional;

public interface IFacultyService {

    Faculty save(Faculty faculty);

    List<Faculty> findAll();

    Optional<Faculty> findById(Integer code);

    void deleteById(Integer code);
}
