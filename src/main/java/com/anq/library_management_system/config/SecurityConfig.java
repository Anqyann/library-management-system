package com.anq.library_management_system.config;

import com.anq.library_management_system.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED,
                                                "Unauthorized"
                                        )
                        )

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_FORBIDDEN,
                                                "Forbidden"
                                        )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users",
                                "/api/auth/login"
                        ).permitAll()

                        .requestMatchers("/error").permitAll()


                        // BOOKS

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/books/**"
                        ).hasAnyRole("USER", "LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/books"
                        ).hasRole("LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/books/**"
                        ).hasRole("LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/books/**"
                        ).hasRole("LIBRARIAN")


                        // AUTHORS

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/authors/**"
                        ).hasAnyRole("USER", "LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/authors"
                        ).hasRole("LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/authors/**"
                        ).hasRole("LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/authors/**"
                        ).hasRole("LIBRARIAN")


                        // GENRES

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/genres/**"
                        ).hasAnyRole("USER", "LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/genres"
                        ).hasRole("LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/genres/**"
                        ).hasRole("LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/genres/**"
                        ).hasRole("LIBRARIAN")


                        // USERS

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/**"
                        ).hasRole("LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/**"
                        ).hasRole("LIBRARIAN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/borrow-records/me",
                                "/api/borrow-records/me/active",
                                "/api/borrow-records/me/overdue"
                        ).hasAnyRole("USER", "LIBRARIAN")

                        // BORROW RECORDS

                        .requestMatchers(
                                "/api/borrow-records/**"
                        ).hasRole("LIBRARIAN")


                        // EVERYTHING ELSE

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
}