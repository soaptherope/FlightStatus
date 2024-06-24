package airAstana.flightStatus.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * represents a user role entity.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "roles")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "unique identifier for the role")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    @Schema(description = "name of the role (USER, ADMIN)")
    private EnumRole name;

    /**
     * constructor to initialize a role with a specific name.
     *
     * @param name name of the role
     */
    public Role(EnumRole name) {
        this.name = name;
    }
}
