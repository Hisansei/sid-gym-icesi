package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Employee;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {

    Employee save(Employee employee);

    List<Employee> findAll();

    Optional<Employee> findById(String id);

    void deleteById(String id);
}
