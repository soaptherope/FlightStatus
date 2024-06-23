package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.exception.UsernameTakenException;
import airAstana.flightStatus.model.EnumRole;
import airAstana.flightStatus.model.Role;
import airAstana.flightStatus.model.dto.JwtAuthResponse;
import airAstana.flightStatus.model.dto.RegisterLoginRequest;
import airAstana.flightStatus.repository.RoleRepository;
import airAstana.flightStatus.repository.UserRepository;
import airAstana.flightStatus.model.User;
import airAstana.flightStatus.service.AuthService;
import airAstana.flightStatus.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthServiceImpl implements AuthService {

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

    private void validateUsername(RegisterLoginRequest request) {
        if (request.getUsername() == null || !request.getUsername().matches(USERNAME_PATTERN)) {
            throw new IllegalArgumentException("invalid username provided: " + request.getUsername());
        }
    }

    private void validatePassword(RegisterLoginRequest request) {
        if (request.getPassword() == null || !request.getPassword().matches(PASSWORD_PATTERN)) {
            throw new IllegalArgumentException("invalid password provided: " + request.getPassword());
        }
    }

    @Transactional
    public User register(RegisterLoginRequest registerRequest) {
        validateUsername(registerRequest);
        validatePassword(registerRequest);

        User user = new User();

        if (userRepository.existsByUsernameIgnoreCase(registerRequest.getUsername())) {
            throw new UsernameTakenException("username is already taken:" + registerRequest.getUsername());
        }

        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Role userRole = roleRepository.findByName(EnumRole.USER).orElseThrow(() -> new IllegalStateException("user role not found"));
        user.setRole(userRole);

        return userRepository.save(user);
    }

    public JwtAuthResponse login(RegisterLoginRequest loginRequest) {
        validateUsername(loginRequest);
        validatePassword(loginRequest);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword())
        );

        User user = userRepository.findByUsernameIgnoreCase(loginRequest.getUsername()).orElseThrow(() -> new IllegalArgumentException("invalid username or password"));
        String jwt = jwtService.generateToken(user);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setToken(jwt);
        return jwtAuthResponse;
    }
}
