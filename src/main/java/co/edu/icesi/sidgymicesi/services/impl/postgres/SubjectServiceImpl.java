package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Subject;
import co.edu.icesi.sidgymicesi.repository.postgres.ISubjectRepository;
import co.edu.icesi.sidgymicesi.services.postgres.ISubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectServiceImpl implements ISubjectService {

    private final ISubjectRepository subjectRepository;

    @Override
    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    @Override
    public Optional<Subject> findById(String code) {
        return subjectRepository.findById(code);
    }

    @Override
    public void deleteById(String code) {
        subjectRepository.deleteById(code);
    }
}
