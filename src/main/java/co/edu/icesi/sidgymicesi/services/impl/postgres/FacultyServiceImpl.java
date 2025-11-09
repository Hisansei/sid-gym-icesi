package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Faculty;
import co.edu.icesi.sidgymicesi.repository.postgres.IFacultyRepository;
import co.edu.icesi.sidgymicesi.services.postgres.IFacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FacultyServiceImpl implements IFacultyService {

    private final IFacultyRepository facultyRepository;

    @Override
    public Faculty save(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    @Override
    public List<Faculty> findAll() {
        return facultyRepository.findAll();
    }

    @Override
    public Optional<Faculty> findById(Integer code) {
        return facultyRepository.findById(code);
    }

    @Override
    public void deleteById(Integer code) {
        facultyRepository.deleteById(code);
    }
}
