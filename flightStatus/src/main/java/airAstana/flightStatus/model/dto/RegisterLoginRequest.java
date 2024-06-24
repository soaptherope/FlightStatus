package airAstana.flightStatus.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * a register or login request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterLoginRequest {

    @Schema(description = "username for registration or login")
    private String username;

    @Schema(description = "password for registration or login")
    private String password;
}
