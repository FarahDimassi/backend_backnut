package com.example.backnut.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Enable CORS with defaults.
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/uploads/**", "/ws/**").permitAll()
                        .requestMatchers("/auth/register", "/auth/login" ,"/auth/forgot-password",
                                "/auth/verify-otp").permitAll()
                        .requestMatchers("/api/protected").hasAuthority("ROLE_User")
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_User")
                        .requestMatchers("/api/coach/**").hasAuthority("ROLE_Coach")
                        .requestMatchers(HttpMethod.GET, "/api/chat/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/chat/audio/**").permitAll()
                        .requestMatchers("/api/chat/**").authenticated()
                        .requestMatchers("/api/reviews/coach/**").hasAuthority("ROLE_Coach")
                        .requestMatchers("/api/reviews/**").hasAuthority("ROLE_User")
                        .requestMatchers("/api/stat/**").hasAuthority("ROLE_User")
                        .requestMatchers("/api/meals/user/**").hasAuthority("ROLE_User")
                        .requestMatchers("/api/meals/plan/**").hasAuthority("ROLE_User")
                        .requestMatchers("/api/meals/calend/**").hasAuthority("ROLE_User")
                        .requestMatchers("/api/meals/plann/**").hasAuthority("ROLE_Coach")
                        .requestMatchers("/api/meals/**").hasAuthority("ROLE_Coach")

                        .requestMatchers("/api/invitations/**").hasAuthority("ROLE_User")
                        .requestMatchers("/api/invitations/plann/**").hasAuthority("ROLE_User")
                        .requestMatchers("/api/friends/coach-invitations/**").hasAuthority("ROLE_Coach")
                        .requestMatchers("/api/calendar/**").hasAuthority("ROLE_Coach")
                        .requestMatchers("/api/scannedproducts/**").hasAuthority("ROLE_User")
                        .requestMatchers("/api/progress/**").hasAuthority("ROLE_User")


                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
