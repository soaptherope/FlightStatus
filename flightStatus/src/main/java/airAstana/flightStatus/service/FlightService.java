package airAstana.flightStatus.service;

import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.model.dto.FlightDto;

import java.util.List;
import java.util.Optional;

public interface FlightService {
    List<Flight> getFlights(Optional<String> destination, Optional<String> origin);

    Flight addFlight(FlightDto flightDto);

    Flight updateFlightStatus(Long id, Status status);
}
