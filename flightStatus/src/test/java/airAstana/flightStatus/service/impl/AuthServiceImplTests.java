package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.exception.UsernameTakenException;
import airAstana.flightStatus.model.EnumRole;
import airAstana.flightStatus.model.Role;
import airAstana.flightStatus.model.User;
import airAstana.flightStatus.model.dto.JwtAuthResponse;
import airAstana.flightStatus.model.dto.RegisterLoginRequest;
import airAstana.flightStatus.repository.RoleRepository;
import airAstana.flightStatus.repository.UserRepository;
import airAstana.flightStatus.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_NewUser_Success() {
        RegisterLoginRequest request = new RegisterLoginRequest("newuser", "password123");

        Role userRole = new Role(EnumRole.USER);
        User newUser = new User(request.getUsername(), request.getPassword());
        newUser.setRole(userRole);

        when(userRepository.existsByUsernameIgnoreCase(request.getUsername())).thenReturn(false);
        when(roleRepository.findByName(EnumRole.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(newUser);

        User registeredUser = authService.register(request);

        assertNotNull(registeredUser);
        assertEquals(request.getUsername(), registeredUser.getUsername());
        assertEquals(userRole, registeredUser.getRole());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testRegister_UsernameTaken_ExceptionThrown() {
        RegisterLoginRequest request = new RegisterLoginRequest("existingUser", "password123");

        when(userRepository.existsByUsernameIgnoreCase(request.getUsername())).thenReturn(true);

        assertThrows(UsernameTakenException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLogin_ValidCredentials_Success() {
        RegisterLoginRequest request = new RegisterLoginRequest("existingUser", "password123");

        User existingUser = new User(request.getUsername(), "encodedPassword");
        when(userRepository.findByUsernameIgnoreCase(request.getUsername())).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(existingUser)).thenReturn("generatedToken");

        JwtAuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("generatedToken", response.getToken());
    }

    @Test
    void testLogin_InvalidCredentials_ExceptionThrown() {
        RegisterLoginRequest request = new RegisterLoginRequest("nonExistingUser", "invalidPassword");

        when(userRepository.findByUsernameIgnoreCase(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }
}
