package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Department;
import co.edu.icesi.sidgymicesi.repository.postgres.IDepartmentRepository;
import co.edu.icesi.sidgymicesi.services.postgres.IDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements IDepartmentService {

    private final IDepartmentRepository departmentRepository;

    @Override
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public Optional<Department> findById(Integer code) {
        return departmentRepository.findById(code);
    }

    @Override
    public void deleteById(Integer code) {
        departmentRepository.deleteById(code);
    }
}
