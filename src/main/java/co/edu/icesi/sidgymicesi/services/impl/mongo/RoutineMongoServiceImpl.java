package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.Routine;
import co.edu.icesi.sidgymicesi.repository.mongo.IRoutineRepository;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineMongoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoutineMongoServiceImpl implements IRoutineMongoService {

    private final IRoutineRepository routineRepository;

    @Override
    public Routine save(Routine routine) {
        return routineRepository.save(routine);
    }

    @Override
    public List<Routine> findAll() {
        return routineRepository.findAll();
    }

    @Override
    public Optional<Routine> findById(String id) {
        return routineRepository.findById(id);
    }

    @Override
    public List<Routine> findByUsername(String username) {
        return routineRepository.findByOwnerUsernameOrderByCreatedAtDesc(username);
    }

    @Override
    public void deleteById(String id) {
        routineRepository.deleteById(id);
    }
}

