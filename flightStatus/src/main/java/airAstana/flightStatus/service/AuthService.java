package airAstana.flightStatus.service;

import airAstana.flightStatus.model.dto.JwtAuthResponse;
import airAstana.flightStatus.model.dto.RegisterLoginRequest;
import airAstana.flightStatus.model.User;

public interface AuthService {
    User register(RegisterLoginRequest registerRequest);
    public JwtAuthResponse login(RegisterLoginRequest loginRequest);

}
