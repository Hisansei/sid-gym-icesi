package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.City;

import java.util.List;
import java.util.Optional;

public interface ICityService {

    City save(City city);

    List<City> findAll();

    Optional<City> findById(Integer code);

    void deleteById(Integer code);
}
