package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.configuration.LoggerManager;
import airAstana.flightStatus.exception.UsernameTakenException;
import airAstana.flightStatus.model.EnumRole;
import airAstana.flightStatus.model.Role;
import airAstana.flightStatus.model.User;
import airAstana.flightStatus.model.dto.JwtAuthResponse;
import airAstana.flightStatus.model.dto.RegisterLoginRequest;
import airAstana.flightStatus.repository.RoleRepository;
import airAstana.flightStatus.repository.UserRepository;
import airAstana.flightStatus.service.AuthService;
import airAstana.flightStatus.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.logging.Logger;

/**
 * Implementation of AuthService providing user authentication and registration operations.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerManager.getLogger();

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{3,}$";

    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]{8,}$";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Validates the username against a regex pattern.
     *
     * @param request RegisterLoginRequest object containing username
     * @throws IllegalArgumentException if username is null or does not match the expected pattern
     */
    private void validateUsername(RegisterLoginRequest request) {
        if (request.getUsername() == null || !request.getUsername().matches(USERNAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid username provided: " + request.getUsername());
        }
    }

    /**
     * Validates the password against a regex pattern.
     *
     * @param request RegisterLoginRequest object containing password
     * @throws IllegalArgumentException if password is null or does not match the expected pattern
     */
    private void validatePassword(RegisterLoginRequest request) {
        if (request.getPassword() == null || !request.getPassword().matches(PASSWORD_PATTERN)) {
            throw new IllegalArgumentException("Invalid password provided: " + request.getPassword());
        }
    }

    /**
     * Registers a new user with the provided registration request.
     *
     * @param registerRequest RegisterLoginRequest object containing username and password
     * @return User object representing the registered user
     * @throws UsernameTakenException if the username is already taken
     * @throws IllegalStateException if the user role (EnumRole.USER) is not found
     */
    @Override
    @Transactional
    @Operation(summary = "Register User", description = "Registers a new user with the provided username and password")
    public User register(@Parameter(description = "RegisterLoginRequest object containing username and password") RegisterLoginRequest registerRequest) {
        validateUsername(registerRequest);
        validatePassword(registerRequest);

        User user = new User();

        if (userRepository.existsByUsernameIgnoreCase(registerRequest.getUsername())) {
            throw new UsernameTakenException("Username is already taken: " + registerRequest.getUsername());
        }

        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Role userRole = roleRepository.findByName(EnumRole.USER)
                .orElseThrow(() -> new IllegalStateException("User role not found"));
        user.setRole(userRole);

        logger.info("A new user has been created with username: " + registerRequest.getUsername());
        return userRepository.save(user);
    }

    /**
     * Logs in a user with the provided login request.
     *
     * @param loginRequest RegisterLoginRequest object containing username and password
     * @return JwtAuthResponse containing the generated JWT token
     * @throws IllegalArgumentException if the username or password is invalid
     */
    @Override
    @Transactional
    @Operation(summary = "Login User", description = "Logs in a user with the provided username and password")
    public JwtAuthResponse login(@Parameter(description = "RegisterLoginRequest object containing username and password") RegisterLoginRequest loginRequest) {
        validateUsername(loginRequest);
        validatePassword(loginRequest);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword())
        );

        User user = userRepository.findByUsernameIgnoreCase(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        String jwt = jwtService.generateToken(user);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setToken(jwt);
        return jwtAuthResponse;
    }

    /**
     * Retrieves the current authenticated user.
     *
     * @return User object representing the current authenticated user
     * @throws UsernameNotFoundException if the authenticated username is not found in the database
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Grants admin role to the current authenticated user.
     *
     * @throws IllegalStateException if the user role (EnumRole.ADMIN) is not found
     */
    @Override
    @Transactional
    @Operation(summary = "Grant Admin Role", description = "Grants admin role to the current authenticated user")
    public void getAdmin() {
        User user = getCurrentUser();
        Role adminRole = roleRepository.findByName(EnumRole.ADMIN)
                .orElseThrow(() -> new IllegalStateException("Admin role not found"));
        user.setRole(adminRole);

        logger.info("User " + getCurrentUser().getUsername() + " has become an admin");
        userRepository.save(user);
    }
}
