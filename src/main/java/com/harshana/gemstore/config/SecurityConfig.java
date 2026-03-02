package com.harshana.gemstore.config;

import com.harshana.gemstore.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // Make sure Spring Security uses your DB users
                .userDetailsService(userDetailsService)

                .authorizeHttpRequests(auth -> auth
                        // Auth pages
                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers("/", "/gem/**", "/uploads/**", "/cart/**", "/checkout/**", "/order/**", "/payhere/**").permitAll()

                        // Static resources (important when you add Bootstrap)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico")
                        .permitAll()

                        // Public pages (you can change later)
                        .requestMatchers("/", "/gems/**").permitAll()

                        // Admin area
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers("/", "/gem/**", "/uploads/**").permitAll()

                        // Buyer area (keep for later; for now it's okay to protect)
                        .requestMatchers("/buyer/**").hasRole("BUYER")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        // This is the page you will create: GET /auth/login
                        .loginPage("/auth/login")

                        // This is where the login form POSTS to (Spring handles it)
                        .loginProcessingUrl("/login")

                        // After successful login
                        .defaultSuccessUrl("/admin/dashboard", true)

                        // If login fails
                        .failureUrl("/auth/login?error=true")

                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
