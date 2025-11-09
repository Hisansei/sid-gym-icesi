package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Campus;

import java.util.List;
import java.util.Optional;

public interface ICampusService {

    Campus save(Campus campus);

    List<Campus> findAll();

    Optional<Campus> findById(Integer code);

    void deleteById(Integer code);
}
