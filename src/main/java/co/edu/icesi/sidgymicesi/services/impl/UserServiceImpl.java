package co.edu.icesi.sidgymicesi.services.impl;

import co.edu.icesi.sidgymicesi.model.User;
import co.edu.icesi.sidgymicesi.repository.IUserRepository;
import co.edu.icesi.sidgymicesi.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findById(username);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        userRepository.deleteById(username);
    }
}
