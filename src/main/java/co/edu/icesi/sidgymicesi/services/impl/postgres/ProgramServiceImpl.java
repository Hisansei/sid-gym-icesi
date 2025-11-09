package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Program;
import co.edu.icesi.sidgymicesi.repository.postgres.IProgramRepository;
import co.edu.icesi.sidgymicesi.services.postgres.IProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramServiceImpl implements IProgramService {

    private final IProgramRepository programRepository;

    @Override
    public Program save(Program program) {
        return programRepository.save(program);
    }

    @Override
    public List<Program> findAll() {
        return programRepository.findAll();
    }

    @Override
    public Optional<Program> findById(Integer code) {
        return programRepository.findById(code);
    }

    @Override
    public void deleteById(Integer code) {
        programRepository.deleteById(code);
    }
}
