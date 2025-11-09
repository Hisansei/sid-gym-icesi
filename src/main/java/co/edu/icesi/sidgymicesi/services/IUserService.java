package co.edu.icesi.sidgymicesi.services;

import co.edu.icesi.sidgymicesi.model.Role;
import co.edu.icesi.sidgymicesi.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User save(User user);

    List<User> findAll();

    List<User> findByRole(Role role);

    Optional<User> findByUsername(String username);

    Optional<User> findByStudentId(String studentId);

    Optional<User> findByEmployeeId(String employeeId);

    void deleteByUsername(String username);
}