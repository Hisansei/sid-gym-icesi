package co.edu.icesi.sidgymicesi.services;

import co.edu.icesi.sidgymicesi.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User save(User user);

    List<User> findAll();

    Optional<User> findByUsername(String username);

    void deleteByUsername(String username);
}
