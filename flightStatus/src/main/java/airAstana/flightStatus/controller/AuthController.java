package airAstana.flightStatus.controller;

import airAstana.flightStatus.exception.UsernameTakenException;
import airAstana.flightStatus.model.User;
import airAstana.flightStatus.model.dto.JwtAuthResponse;
import airAstana.flightStatus.model.dto.RegisterLoginRequest;
import airAstana.flightStatus.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * controller class for authentication operations.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * registers a new user.
     *
     * @param registerRequest registerLoginRequest containing user registration details
     * @return responseEntity with user information if registration is successful else returns bad request
     */
    @PostMapping("/register")
    @Operation(summary = "registers a new user")
    public ResponseEntity<User> register(@RequestBody RegisterLoginRequest registerRequest) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequest));
        } catch (UsernameTakenException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * logs in a user and generates JWT authentication token.
     *
     * @param loginRequest registerLoginRequest containing user login details
     * @return responseEntity with JwtAuthResponse containing JWT token if login is successful else returns bad request
     */
    @PostMapping("/login")
    @Operation(summary = "logs in a user and generates JWT authentication token")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody RegisterLoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authService.login(loginRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * endpoint for getting the admin role
     */
    @GetMapping("/admin")
    @Operation(summary = "endpoint for getting the admin role")
    public void getAdmin() {
        authService.getAdmin();
    }
}
