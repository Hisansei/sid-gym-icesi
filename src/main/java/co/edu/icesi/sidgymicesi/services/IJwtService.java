package co.edu.icesi.sidgymicesi.services;

import java.util.List;
import java.util.function.Function;

import co.edu.icesi.sidgymicesi.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import io.jsonwebtoken.Claims;

public interface IJwtService {
    String generateToken(User user, Authentication authentication);
    String extractUsername(String token);
    List<SimpleGrantedAuthority> extractAuthorities(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    UserDetails getUserDetailsFromToken(String token);
    boolean isTokenExpired(String token);
    boolean isTokenValid(String token);
}