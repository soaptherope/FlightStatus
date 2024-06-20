package airAstana.flightStatus.service;

import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.repository.FlightRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public List<Flight> getFlights() {
        return flightRepository.findFlights(origin, destination, Sort.by(Sort.Direction.ASC, "arrival"));
    }

    @Override
    public Flight addFlight(Flight flight) {
        return null;
    }

    @Override
    public Flight updateFlightStatus(int id, Status status) {
        return null;
    }
}
