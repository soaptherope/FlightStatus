package airAstana.flightStatus.controller;

import airAstana.flightStatus.exception.UsernameTakenException;
import airAstana.flightStatus.model.dto.JwtAuthResponse;
import airAstana.flightStatus.model.dto.RegisterLoginRequest;
import airAstana.flightStatus.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import airAstana.flightStatus.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService =authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterLoginRequest registerRequest) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequest));
        } catch (UsernameTakenException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody RegisterLoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authService.login(loginRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
