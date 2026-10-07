package com.fieldops.config;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.fieldops.repository.UserRepository;
import com.fieldops.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
@Configuration
@Profile("production")
public class ProductionSecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {

        return new JwtAuthenticationFilter(
                jwtService,
                userRepository
        );
    }

    @Bean
    public SecurityFilterChain productionSecurityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/health").permitAll()
                .requestMatchers("/api/auth/**").permitAll()

                .requestMatchers("/api/users/**")
                .hasRole("ADMIN")

                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/equipment")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/equipment/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/equipment/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/equipment/**")
                .hasAnyRole("ADMIN", "ENGINEER", "FIELD_ENGINEER", "OPERATIONS")

                .requestMatchers("/api/work-orders/**")
                .hasAnyRole("ADMIN", "ENGINEER", "FIELD_ENGINEER", "OPERATIONS")

                .requestMatchers("/api/analytics/**")
                .hasAnyRole("ADMIN", "ENGINEER", "FIELD_ENGINEER", "OPERATIONS")

                .anyRequest().authenticated()
            )
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(authenticationEntryPoint)
            )
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
   @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    String allowedOrigins = System.getenv("FIELDOPS_CORS_ORIGINS");

    if (allowedOrigins == null || allowedOrigins.isBlank()) {
        throw new IllegalStateException(
                "FIELDOPS_CORS_ORIGINS environment variable is not configured"
        );
    }

    configuration.setAllowedOriginPatterns(
            List.of(allowedOrigins.split(","))
    );

    configuration.setAllowedMethods(List.of(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "OPTIONS"
    ));

    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setAllowCredentials(false);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);

    return source;
}
}