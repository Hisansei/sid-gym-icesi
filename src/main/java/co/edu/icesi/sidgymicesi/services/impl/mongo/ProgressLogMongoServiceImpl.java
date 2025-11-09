package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.ProgressLog;
import co.edu.icesi.sidgymicesi.repository.mongo.IProgressLogRepository;
import co.edu.icesi.sidgymicesi.services.mongo.IProgressLogMongoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressLogMongoServiceImpl implements IProgressLogMongoService {

    private final IProgressLogRepository progressLogRepository;

    @Override
    public ProgressLog save(ProgressLog progressLog) {
        return progressLogRepository.save(progressLog);
    }

    @Override
    public List<ProgressLog> findAll() {
        return progressLogRepository.findAll();
    }

    @Override
    public Optional<ProgressLog> findById(String id) {
        return progressLogRepository.findById(id);
    }

    @Override
    public Optional<ProgressLog> findByRoutineAndDate(String routineId, LocalDate date) {
        return progressLogRepository.findByRoutineIdAndDate(routineId, date);
    }

    @Override
    public List<ProgressLog> findByUsername(String username) {
        return progressLogRepository.findByUsernameOrderByDateDesc(username);
    }

    @Override
    public void deleteById(String id) {
        progressLogRepository.deleteById(id);
    }
}
