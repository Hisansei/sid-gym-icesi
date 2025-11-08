package co.edu.icesi.sidgymicesi.util;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class DemoCurrentUser {
    private static HttpServletRequest request = null;
    public DemoCurrentUser(HttpServletRequest request) { this.request = request; }

    public static String username() {
        String u = request.getParameter("u");
        return (u != null && !u.isBlank()) ? u : "laura.h";
    }
}
