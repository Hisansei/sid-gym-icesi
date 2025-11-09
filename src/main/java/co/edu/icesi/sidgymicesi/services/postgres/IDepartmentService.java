package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Department;

import java.util.List;
import java.util.Optional;

public interface IDepartmentService {

    Department save(Department department);

    List<Department> findAll();

    Optional<Department> findById(Integer code);

    void deleteById(Integer code);
}
