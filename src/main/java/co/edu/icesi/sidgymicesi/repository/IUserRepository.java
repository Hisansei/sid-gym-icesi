package co.edu.icesi.sidgymicesi.repository;

import co.edu.icesi.sidgymicesi.model.Role;
import co.edu.icesi.sidgymicesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    // Consultas
    List<User> findAll();

    Optional<User> findByUsername(String username);

    List<User> findByActiveTrue();

    List<User> findByRole(Role role);

    // Borrado
    void deleteByUsername(String username);

    boolean existsByUsername(String username);
}
