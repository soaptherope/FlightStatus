package airAstana.flightStatus.model.dto;

import airAstana.flightStatus.model.Status;
import lombok.Data;

import java.time.ZonedDateTime;


@Data
public class FlightDto {

    private String origin;

    private String destination;

    private ZonedDateTime departure;

    private ZonedDateTime arrival;

    private Status status;
}
