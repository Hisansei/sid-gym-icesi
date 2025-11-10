package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentService {

    Student save(Student student);

    List<Student> findAll();

    Optional<Student> findById(String id);

    void deleteById(String id);
}
