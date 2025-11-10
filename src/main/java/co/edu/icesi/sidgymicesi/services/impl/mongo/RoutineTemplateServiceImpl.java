package co.edu.icesi.sidgymicesi.services.impl.mongo;

import co.edu.icesi.sidgymicesi.model.mongo.RoutineTemplate;
import co.edu.icesi.sidgymicesi.repository.mongo.IRoutineTemplateRepository;
import co.edu.icesi.sidgymicesi.services.mongo.IRoutineTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoutineTemplateServiceImpl implements IRoutineTemplateService {

    private final IRoutineTemplateRepository routineTemplateRepository;

    @Override
    public RoutineTemplate save(RoutineTemplate routineTemplate) {
        return routineTemplateRepository.save(routineTemplate);
    }

    @Override
    public List<RoutineTemplate> findAll() {
        return routineTemplateRepository.findAll();
    }

    @Override
    public List<RoutineTemplate> findActiveTemplates() {
        return routineTemplateRepository.findByStatusTrue();
    }

    @Override
    public List<RoutineTemplate> findByTrainer(String trainerId) {
        return routineTemplateRepository.findByTrainerId(trainerId);
    }

    @Override
    public Optional<RoutineTemplate> findById(String id) {
        return routineTemplateRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        routineTemplateRepository.deleteById(id);
    }
}
