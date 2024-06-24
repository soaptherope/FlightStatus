package airAstana.flightStatus.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * represents a flight entity.
 */
@Data
@Entity
@Table(name = "flights")
@AllArgsConstructor
@NoArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "unique identifier for the flight")
    private Long id;

    @Schema(description = "origin airport city")
    private String origin;

    @Schema(description = "destination airport city")
    private String destination;

    @Schema(description = "departure date and time in UTC")
    private OffsetDateTime departure;

    @Schema(description = "arrival date and time in UTC")
    private OffsetDateTime arrival;

    @Enumerated(EnumType.STRING)
    @Schema(description = "status of the flight (SCHEDULED, DELAYED, CANCELLED)")
    private Status status;
}
