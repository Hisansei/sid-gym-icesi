package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.EmployeeType;

import java.util.List;
import java.util.Optional;

public interface IEmployeeTypeService {

    EmployeeType save(EmployeeType employeeType);

    List<EmployeeType> findAll();

    Optional<EmployeeType> findById(String name);

    void deleteById(String name);
}
