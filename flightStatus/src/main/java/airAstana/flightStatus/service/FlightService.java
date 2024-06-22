package airAstana.flightStatus.service;

import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.model.dto.FlightDto;
import java.util.List;


public interface FlightService {

    List<Flight> getFlights(String origin, String destination);

    Flight addFlight(FlightDto flightDto);

    Flight updateFlightStatus(Long id, Status status);
}
