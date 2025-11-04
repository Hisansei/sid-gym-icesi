package co.edu.icesi.sidgymicesi.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface IJwtService {

    String generateToken(UserDetails userDetails);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenValid(String token);

    UserDetails getUserDetailsFromToken(String token);
}