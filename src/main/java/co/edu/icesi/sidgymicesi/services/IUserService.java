package co.edu.icesi.sidgymicesi.services;

import co.edu.icesi.sidgymicesi.model.Role;
import co.edu.icesi.sidgymicesi.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User save(User user, String rawPassword);

    // Lecturas
    List<User> findAll();
    Optional<User> findByUsername(String username);
    User getByUsernameOrThrow(String username);
    List<User> findActive();
    List<User> findByRole(Role role);

    // Cambios puntuales
    User updatePassword(String username, String newRawPassword);
    User updateRole(String username, Role newRole);
    User activate(String username);
    User deactivate(String username);

    // Borrado
    void deleteByUsername(String username);
}
