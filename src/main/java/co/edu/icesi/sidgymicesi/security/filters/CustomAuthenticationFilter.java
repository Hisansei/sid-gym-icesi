//package co.edu.icesi.sidgymicesi.security.filters;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.io.IOException;
//
//public class CustomAuthenticationFilter extends OncePerRequestFilter {
//    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        final String requestURI = request.getRequestURI();
//        final String method = request.getMethod();
//
//        logger.info("Request recibido: {} {} desde IP: {}", method, requestURI, getClientIpAddress(request));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            String username = authentication.getName();
//            String authorities = authentication.getAuthorities().toString();
//            logger.info("Usuario autenticado: {} - Authorities: {} - URI: {}", username, authorities, requestURI);
//        } else {
//            logger.info("Acceso anónimo - URI: {}", requestURI);
//        }
//
//        long startTime = System.currentTimeMillis();
//        try {
//            filterChain.doFilter(request, response);
//            long duration = System.currentTimeMillis() - startTime;
//            logger.info("Response enviado: {} {} - Status: {} - Duración: {}ms",
//                    method, requestURI, response.getStatus(), duration);
//        } catch (Exception e) {
//            long duration = System.currentTimeMillis() - startTime;
//            logger.error("Error en request: {} {} - Error: {} - Duración: {}ms",
//                    method, requestURI, e.getMessage(), duration);
//            throw e;
//        }
//    }
//
//    private String getClientIpAddress(HttpServletRequest request) {
//        String xfHeader = request.getHeader("X-Forwarded-For");
//        if (xfHeader != null && !xfHeader.isBlank()) {
//            return xfHeader.split(",")[0].trim();
//        }
//        return request.getRemoteAddr();
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        return path.startsWith("/css/") ||
//                path.startsWith("/js/") ||
//                path.startsWith("/images/") ||
//                path.startsWith("/webjars/") ||
//                path.startsWith("/assets/") ||
//                path.equals("/auth/login") ||
//                path.startsWith("/auth/");
//
//    }
//}
