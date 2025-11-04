package co.edu.icesi.sidgymicesi.services.impl;


import co.edu.icesi.sidgymicesi.model.Role;
import co.edu.icesi.sidgymicesi.model.User;
import co.edu.icesi.sidgymicesi.repository.IUserRepository;
import co.edu.icesi.sidgymicesi.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public User save(User user, String rawPassword) {
        if (user == null) {
            throw new IllegalArgumentException("User no puede ser null");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("username es obligatorio");
        }

        if (!userRepository.existsByUsername(user.getUsername())) {
            if (rawPassword == null || rawPassword.isBlank()) {
                throw new IllegalArgumentException("La contraseña no puede estar vacía al crear el usuario");
            }
            user.setPasswordHash(passwordEncoder.encode(rawPassword));

            if (user.getRole() == null) user.setRole(Role.STUDENT); // default
            if (user.getCreatedAt() == null) user.setCreatedAt(LocalDateTime.now());
            if (!user.isActive()) user.setActive(true);

        } else {
            if (rawPassword != null && !rawPassword.isBlank()) {
                user.setPasswordHash(passwordEncoder.encode(rawPassword));
            } else {
                userRepository.findByUsername(user.getUsername())
                        .ifPresent(existing -> user.setPasswordHash(existing.getPasswordHash()));
            }
            if (user.getCreatedAt() == null) {
                userRepository.findByUsername(user.getUsername())
                        .ifPresent(existing -> user.setCreatedAt(existing.getCreatedAt()));
            }
        }

        logger.info("Guardando usuario username=" + user.getUsername() + ", role=" + (user.getRole() != null ? user.getRole().name() : "NULL") + ", active=" + user.isActive());
        return userRepository.save(user);
    }

    // ===== Lecturas =====
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findActive() {
        return userRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    // ===== Cambios puntuales =====
    @Override
    public User updatePassword(String username, String newRawPassword) {
        if (newRawPassword == null || newRawPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }
        User user = getByUsernameOrThrow(username);
        user.setPasswordHash(passwordEncoder.encode(newRawPassword));
        return userRepository.save(user);
    }

    @Override
    public User updateRole(String username, Role newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("El rol no puede ser null");
        }
        User user = getByUsernameOrThrow(username);
        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Override
    public User activate(String username) {
        User user = getByUsernameOrThrow(username);
        if (!user.isActive()) {
            user.setActive(true);
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public User deactivate(String username) {
        User user = getByUsernameOrThrow(username);
        if (user.isActive()) {
            user.setActive(false);
            user = userRepository.save(user);
        }
        return user;
    }

    // ===== Borrado =====
    @Override
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }
}
