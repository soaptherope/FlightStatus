package airAstana.flightStatus.service;

import airAstana.flightStatus.model.User;
import airAstana.flightStatus.model.dto.JwtAuthResponse;
import airAstana.flightStatus.model.dto.RegisterLoginRequest;

public interface AuthService {
    User register(RegisterLoginRequest registerRequest);

    JwtAuthResponse login(RegisterLoginRequest loginRequest);

    void getAdmin();
}
