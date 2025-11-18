package co.edu.icesi.sidgymicesi.repository;

import co.edu.icesi.sidgymicesi.model.User;
import co.edu.icesi.sidgymicesi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    // Consultas:
    List<User> findAll();

    List<User> findByRole(Role role);

    Optional<User> findByUsername(String username);

    Optional<User> findByStudentId(String studentId);

    Optional<User> findByEmployeeId(String employeeId);

    boolean existsById(String id);

    @Query("SELECT u FROM User u LEFT JOIN u.employee e " +
           // Caso 1: Estudiante
           "WHERE u.role = :studentRole " +
           // Caso 2: Empleado que NO es Instructor
           "OR (u.role = :employeeRole AND e.employeeType.name != :instructorType)")
    List<User> findAllStudentsAndNonInstructors(
        @Param("studentRole") Role studentRole, 
        @Param("employeeRole") Role employeeRole, 
        @Param("instructorType") String instructorType
    );

    // Borrado:
    void deleteByUsername(String username);
}