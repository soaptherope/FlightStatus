package airAstana.flightStatus.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * a JWT authentication response.
 */
@Data
public class JwtAuthResponse {

    @Schema(description = "JWT token for authentication")
    private String token;
}
