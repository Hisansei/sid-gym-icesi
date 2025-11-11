package co.edu.icesi.sidgymicesi.config;

import co.edu.icesi.sidgymicesi.security.CustomUserDetailsService;
import co.edu.icesi.sidgymicesi.security.filters.CustomAuthenticationFilter;
import co.edu.icesi.sidgymicesi.security.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // Encoder para contraseñas de USERS (PostgreSQL)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Declarado pero NO agregado a la cadena (seguimos con sesión/form-login)
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    // Autenticación con nuestro UserDetailsService (DaoAuthenticationProvider)
    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // SecurityContext en sesión (form-login)
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    // Filtro de logging que ya tienes en el proyecto
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() {
        return new CustomAuthenticationFilter();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                .authenticationProvider(authenticationProvider())
                .securityContext(ctx -> ctx.securityContextRepository(securityContextRepository()))

                .addFilterAfter(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        // Público y archivos estáticos
                        .requestMatchers("/", "/public/**", "/mvc/public/auth/login", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Áreas por rol (MVC y API)
                        .requestMatchers("/mvc/admin/**", "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/mvc/trainer/**", "/api/trainer/**").hasAnyRole("EMPLOYEE","ADMIN")
                        .requestMatchers("/mvc/student/**", "/api/student/**").hasAnyRole("STUDENT","ADMIN")

                        // *** Fase 3: RUTINAS + PROGRESO ***
                        // Cualquier usuario autenticado del sistema (STUDENT/EMPLOYEE/ADMIN) puede entrar:
                        .requestMatchers("/mvc/routines/**").hasAnyRole("STUDENT","EMPLOYEE","ADMIN")
                        .requestMatchers("/mvc/progress/**").hasAnyRole("STUDENT","EMPLOYEE","ADMIN")

                        // Catálogo de ejercicios: lectura autenticada; altas/edición solo entrenador o admin
                        .requestMatchers(HttpMethod.GET, "/mvc/exercises/**").authenticated()
                        .requestMatchers("/mvc/exercises/add/**", "/mvc/exercises/edit/**").hasAnyRole("EMPLOYEE","ADMIN")

                        // Plantillas de rutina: lectura autenticada; CRUD solo entrenador/admin
                        .requestMatchers(HttpMethod.GET, "/mvc/routine-templates/**").authenticated()
                        .requestMatchers("/mvc/routine-templates/create/**",
                                "/mvc/routine-templates/edit/**",
                                "/mvc/routine-templates/*/delete").hasAnyRole("EMPLOYEE","ADMIN")

                        // Home y resto autenticado
                        .requestMatchers("/mvc/home").authenticated()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex.accessDeniedPage("/mvc/public/auth/access-denied"))

                .formLogin(form -> form
                        .loginPage("/mvc/public/auth/login")
                        .loginProcessingUrl("/mvc/auth/login")
                        .defaultSuccessUrl("/mvc/home", true)
                        .failureUrl("/mvc/public/auth/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )

                .logout(log -> log
                        .logoutUrl("/mvc/auth/logout")
                        .logoutSuccessUrl("/mvc/public/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
