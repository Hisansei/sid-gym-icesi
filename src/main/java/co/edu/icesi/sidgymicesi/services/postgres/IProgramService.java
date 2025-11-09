package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Program;

import java.util.List;
import java.util.Optional;

public interface IProgramService {

    Program save(Program program);

    List<Program> findAll();

    Optional<Program> findById(Integer code);

    void deleteById(Integer code);
}
