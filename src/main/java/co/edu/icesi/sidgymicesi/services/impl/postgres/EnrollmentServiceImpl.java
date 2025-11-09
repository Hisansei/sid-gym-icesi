package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Enrollment;
import co.edu.icesi.sidgymicesi.model.postgres.EnrollmentId;
import co.edu.icesi.sidgymicesi.repository.postgres.IEnrollmentRepository;
import co.edu.icesi.sidgymicesi.services.postgres.IEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements IEnrollmentService {

    private final IEnrollmentRepository enrollmentRepository;

    @Override
    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Optional<Enrollment> findById(EnrollmentId id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    public void deleteById(EnrollmentId id) {
        enrollmentRepository.deleteById(id);
    }
}
