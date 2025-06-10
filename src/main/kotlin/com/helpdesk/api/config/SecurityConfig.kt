package com.helpdesk.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Spring Security configuration for the Help Desk REST API.
 *
 * - Enables web security and method-level security (@PreAuthorize).
 * - Configures password encoding using BCrypt.
 * - Sets up DaoAuthenticationProvider to use our custom UserDetailsService.
 * - Configures HTTP security rules, disables CSRF (for stateless REST), and enables CORS.
 * - Uses stateless sessions, suitable for token-based authentication (like JWT, or HTTP Basic for testing).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enables @PreAuthorize and @PostAuthorize annotations
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService // Inject our custom UserDetailsService
) {

    /**
     * Defines the password encoder bean. BCrypt is recommended for secure password hashing.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * Configures the authentication provider that uses our CustomUserDetailsService and PasswordEncoder.
     */
    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(customUserDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    /**
     * Configures the security filter chain. This is the main configuration for HTTP security.
     *
     * @param http The HttpSecurity object to configure.
     * @return A configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Disable CSRF for stateless REST APIs. CSRF tokens are typically for session-based UIs.
            .csrf { csrf -> csrf.disable() }
            // Enable CORS using the configuration defined in corsConfigurationSource()
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            // Define authorization rules for HTTP requests
            .authorizeHttpRequests { authorize ->
                authorize
                    // Publicly accessible endpoints (no authentication required)
                    .requestMatchers("/api/users/register").permitAll() // Allow new users to register
                    .requestMatchers("/api/auth/login").permitAll() // If you have a specific login endpoint (e.g., for JWT)
                    .requestMatchers("/error").permitAll() // Spring Boot's default error page
                    // Paths for OpenAPI/Swagger UI (if you include them like Springdoc OpenAPI)
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    // Allow access to Spring Boot Actuator endpoints for health checks etc. (adjust in production!)
                    .requestMatchers("/actuator/**").permitAll()

                    // Role-based access for specific API paths
                    .requestMatchers("/api/admin/**").hasRole("ADMIN") // Only ADMINs can access /api/admin paths

                    // All other API paths under /api/ require authentication
                    .requestMatchers("/api/**").authenticated()

                    // Deny all other requests not explicitly permitted or authenticated.
                    // This is a good default security practice for an API.
                    .anyRequest().denyAll()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .httpBasic() { }

        return http.build()
    }

    /**
     * Defines the global CORS (Cross-Origin Resource Sharing) configuration.
     * This bean allows your frontend application (running on a different origin) to make requests to your API.
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}