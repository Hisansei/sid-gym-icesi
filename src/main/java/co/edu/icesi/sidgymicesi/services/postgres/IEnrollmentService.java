package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Enrollment;
import co.edu.icesi.sidgymicesi.model.postgres.EnrollmentId;

import java.util.List;
import java.util.Optional;

public interface IEnrollmentService {

    Enrollment save(Enrollment enrollment);

    List<Enrollment> findAll();

    Optional<Enrollment> findById(EnrollmentId id);

    void deleteById(EnrollmentId id);
}
