package com.sangamesh.Fitsphere.config;


import com.sangamesh.Fitsphere.ratelimit.RateLimitFilter;
import com.sangamesh.Fitsphere.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final RateLimitFilter rateLimitFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(auth -> auth
                        //Admin login
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        //Swagger api doc
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        //profile
                        .requestMatchers("/api/profile/**").authenticated()
                        // Authentication
                        .requestMatchers("/api/auth/**").permitAll()
                        //foodlog
                        .requestMatchers("/api/foodlogs/**").authenticated()
                        // Public Exercise APIs
                        .requestMatchers("/api/exercises/**").permitAll()
                        // Public YouTube APIs
                        .requestMatchers("/api/youtube/**").permitAll()
                        // Public Nutrition APIs
                        .requestMatchers(HttpMethod.GET, "/api/nutrition/search", "/api/nutrition/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/nutrition/calculate").permitAll()
                        // Public Health Assessment
                        .requestMatchers(HttpMethod.POST, "/api/health/assessment").permitAll()
                        //contact admin
                        .requestMatchers("/api/contact").permitAll()
                        // Private Nutrition APIs
                        .requestMatchers("/api/nutrition/**").authenticated()
                        // Private Health APIs (future)
                        .requestMatchers("/api/health/**").authenticated()
                        //account update
                        .requestMatchers("/api/account/**").authenticated()
                        //body measuremnts
                        .requestMatchers("/api/measurements/**").authenticated()
                        // Everything else
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or valid authentication token");
                        })
                )
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, RateLimitFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173","https://fitspherebysangamesh.vercel.app"));
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
