package airAstana.flightStatus.configuration;

import airAstana.flightStatus.model.EnumRole;
import airAstana.flightStatus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * configuration class for Spring Security setup.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserService userService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userService = userService;
    }

    /**
     * configures security filter chain for HTTP requests.
     *
     * @param http HttpSecurity object to configure
     * @return SecurityFilterChain configured with specified rules
     * @throws Exception if configuration fails
     */
    @Bean
    @Operation(summary = "configures security filter chain for HTTP requests")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/auth/login", "POST")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/auth/register", "POST")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/**")).permitAll()
                        .requestMatchers("/flights/add", "/flights/edit").hasAuthority(EnumRole.ADMIN.name())
                        .requestMatchers("/flights/arrivals").hasAnyAuthority(EnumRole.USER.name(), EnumRole.ADMIN.name())
                        .requestMatchers("/auth/admin").hasAuthority(EnumRole.USER.name())
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * provides an AuthenticationProvider using DaoAuthenticationProvider.
     *
     * @return AuthenticationProvider instance
     */
    @Bean
    @Operation(summary = "Provides an AuthenticationProvider using DaoAuthenticationProvider")
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * provides a PasswordEncoder instance using BCryptPasswordEncoder.
     *
     * @return PasswordEncoder instance
     */
    @Bean
    @Operation(summary = "Provides a PasswordEncoder instance using BCryptPasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * provides an AuthenticationManager instance.
     *
     * @param config AuthenticationConfiguration object
     * @return AuthenticationManager instance
     * @throws Exception if configuration fails
     */
    @Bean
    @Operation(summary = "Provides an AuthenticationManager instance")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
