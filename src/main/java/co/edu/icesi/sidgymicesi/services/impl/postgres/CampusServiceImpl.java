package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Campus;
import co.edu.icesi.sidgymicesi.repository.postgres.ICampusRepository;
import co.edu.icesi.sidgymicesi.services.postgres.ICampusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CampusServiceImpl implements ICampusService {

    private final ICampusRepository campusRepository;

    @Override
    public Campus save(Campus campus) {
        return campusRepository.save(campus);
    }

    @Override
    public List<Campus> findAll() {
        return campusRepository.findAll();
    }

    @Override
    public Optional<Campus> findById(Integer code) {
        return campusRepository.findById(code);
    }

    @Override
    public void deleteById(Integer code) {
        campusRepository.deleteById(code);
    }
}
