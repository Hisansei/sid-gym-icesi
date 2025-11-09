package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.City;
import co.edu.icesi.sidgymicesi.repository.postgres.ICityRepository;
import co.edu.icesi.sidgymicesi.services.postgres.ICityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CityServiceImpl implements ICityService {

    private final ICityRepository cityRepository;

    @Override
    public City save(City city) {
        return cityRepository.save(city);
    }

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }

    @Override
    public Optional<City> findById(Integer code) {
        return cityRepository.findById(code);
    }

    @Override
    public void deleteById(Integer code) {
        cityRepository.deleteById(code);
    }
}
