package airAstana.flightStatus.model.dto;

import airAstana.flightStatus.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object representing a flight.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDto {

    @Schema(description = "origin airport city")
    private String origin;

    @Schema(description = "destination airport city")
    private String destination;

    @Schema(description = "Departure date and time in ISO-8601 format with offset")
    private OffsetDateTime departure;

    @Schema(description = "Arrival date and time in ISO-8601 format with offset")
    private OffsetDateTime arrival;

    @Schema(description = "status of the flight")
    private Status status;
}
