package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Area;

import java.util.List;
import java.util.Optional;

public interface IAreaService {

    Area save(Area area);

    List<Area> findAll();

    Optional<Area> findById(Integer code);

    void deleteById(Integer code);
}
