//package co.edu.icesi.sidgymicesi.security;
//
//import co.edu.icesi.sidgymicesi.model.User;
//import co.edu.icesi.sidgymicesi.repository.IUserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Transactional
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
//
//    private final IUserRepository userRepository;
//
//    public CustomUserDetailsService(IUserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        logger.info("Auth attempt username={}", username);
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> {
//                    logger.warn("User not found username={}", username);
//                    return new UsernameNotFoundException("Usuario con username " + username + " no encontrado");
//                });
//
//        String roleStr = (user.getRole() != null) ? user.getRole().name() : "NONE";
//        logger.info("User found username={}, role={}, active={}",
//                user.getUsername(), roleStr, user.isActive());
//
//        if (!user.isActive()) {
//            logger.warn("Inactive user username={}", username);
//            throw new UsernameNotFoundException("Usuario inactivo");
//        }
//
//        CustomUserDetails details = new CustomUserDetails(user);
//        logger.debug("Authorities count={}", details.getAuthorities().size());
//        return details;
//    }
//}
