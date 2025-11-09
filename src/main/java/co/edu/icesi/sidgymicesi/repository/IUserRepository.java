package co.edu.icesi.sidgymicesi.repository;

import co.edu.icesi.sidgymicesi.model.User;
import co.edu.icesi.sidgymicesi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // Borrado:
    void deleteByUsername(String username);
}