package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Country;

import java.util.List;
import java.util.Optional;

public interface ICountryService {

    Country save(Country country);

    List<Country> findAll();

    Optional<Country> findById(Integer code);

    void deleteById(Integer code);
}
