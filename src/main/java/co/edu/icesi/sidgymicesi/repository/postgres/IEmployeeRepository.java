package co.edu.icesi.sidgymicesi.repository.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, String> {
}
