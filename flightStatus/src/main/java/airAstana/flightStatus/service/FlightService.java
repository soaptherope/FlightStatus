package airAstana.flightStatus.service;

import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.STATUS;
import java.util.List;

public interface FlightService {
    List<Flight> getFlights();

    Flight addFlight(Flight flight);

    Flight updateFlightStatus(int id, STATUS status);
}
