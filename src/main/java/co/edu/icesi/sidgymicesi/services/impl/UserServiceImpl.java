//package co.edu.icesi.sidgymicesi.services.impl;
//
//
//import co.edu.icesi.sidgymicesi.model.Role;
//import co.edu.icesi.sidgymicesi.model.User;
//import co.edu.icesi.sidgymicesi.repository.IUserRepository;
//import co.edu.icesi.sidgymicesi.services.IUserService;
//import lombok.RequiredArgsConstructor;
////import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.logging.Logger;
//import java.util.regex.Pattern;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class UserServiceImpl implements IUserService {
//
//    @Autowired
//    private final PasswordEncoder passwordEncoder;
//
//    private final IUserRepository userRepository;
//
//    private static final Pattern NAME_PATTERN  = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(\\s[A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$");
//    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9]+@u\\.icesi\\.edu\\.co$");
//
//    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
//
//    @Override
//    public User save(User user) {
//        // Validaciones básicas:
//        String firstName = user.getName();
//
//        if (firstName == null || !NAME_PATTERN.matcher(firstName).matches()) {
//            throw new IllegalArgumentException("El nombre solo puede contener letras.");
//        }
//
//        String lastName = user.getLastName();
//
//        if (lastName == null || !NAME_PATTERN.matcher(lastName).matches()) {
//            throw new IllegalArgumentException("El apellido solo puede contener letras.");
//        }
//
//        String email = user.getEmail();
//
//        userRepository.findByEmail(email).ifPresent(existing -> {
//            if (user.getId() == null || !existing.getId().equals(user.getId())) {
//                throw new IllegalArgumentException("Ya existe un usuario con el correo: " + email);
//            }
//        });
//
//        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
//            throw new IllegalArgumentException("El correo debe ser institucional (@u.icesi.edu.co).");
//        }
//
//        String password = user.getPassword();
//
//        if (password == null || password.isBlank()) {
//            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
//        }
//
//        if (user.getRole() == null || user.getRole().getId() == null) {
//            throw new IllegalArgumentException("El usuario debe tener un rol asignado.");
//        }
//
//        Long roleId = user.getRole().getId();
//
//        roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("No se encontró el rol con id = " + roleId));
//
//        if (user.getId() == null) {
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//        } else {
//            userRepository.findById(user.getId()).ifPresent(existing -> {
//                String newPass = user.getPassword();
//
//                if (newPass == null || newPass.isBlank()) {
//                    user.setPassword(existing.getPassword());
//                } else if (!newPass.equals(existing.getPassword())) {
//                    user.setPassword(passwordEncoder.encode(newPass));
//                } else {
//                    user.setPassword(existing.getPassword());
//                }
//            });
//        }
//
//        return userRepository.save(user);
//    }
//
//    @Override
//    public List<User> findAll() {
//        logger.info("Fetching all users:");
//        return userRepository.findAll();
//    }
//
//    @Override
//    public User findByUsername(String username) {
//        return userRepository.findByName(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
//    }
//
//    @Override
//    public Optional<User> findById(Long id) {
//        return userRepository.findById(id);
//    }
//
//    @Override
//    public Optional<User> findByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//
//    @Override
//    public void deleteByEmail(String email) {
//        userRepository.deleteByEmail(email);
//    }
//
//    @Override
//    public void deleteById(Long id) {
//        if (studentRepository.existsById(id)) {
//            studentRepository.deleteById(id);
//        }
//        userRepository.deleteById(id);
//    }
//}
