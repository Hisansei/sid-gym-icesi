package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Country;
import co.edu.icesi.sidgymicesi.repository.postgres.ICountryRepository;
import co.edu.icesi.sidgymicesi.services.postgres.ICountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CountryServiceImpl implements ICountryService {

    private final ICountryRepository countryRepository;

    @Override
    public Country save(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    @Override
    public Optional<Country> findById(Integer code) {
        return countryRepository.findById(code);
    }

    @Override
    public void deleteById(Integer code) {
        countryRepository.deleteById(code);
    }
}
