package co.edu.icesi.sidgymicesi.repository;

import co.edu.icesi.sidgymicesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    // Consultas:
    List<User> findAll();

    Optional<User> findById(String username);


    Optional<User> findByUsername(String username);

    // Borrado:
    void deleteById(String username);

}