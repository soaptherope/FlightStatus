package airAstana.flightStatus.model.dto;

import airAstana.flightStatus.model.Status;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;


@Data
public class FlightDto {

    private String origin;

    private String destination;

    private OffsetDateTime departure;

    private OffsetDateTime arrival;

    private Status status;
}
