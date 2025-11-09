package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Subject;

import java.util.List;
import java.util.Optional;

public interface ISubjectService {

    Subject save(Subject subject);

    List<Subject> findAll();

    Optional<Subject> findById(String code);

    void deleteById(String code);
}
