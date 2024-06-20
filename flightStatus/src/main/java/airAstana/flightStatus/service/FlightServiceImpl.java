package airAstana.flightStatus.service;

import airAstana.flightStatus.exception.ArrivalIsBeforeDepartureException;
import airAstana.flightStatus.exception.FlightWithIdNotFoundException;
import airAstana.flightStatus.exception.FlightsWithDestinationNotFoundException;
import airAstana.flightStatus.exception.FlightsWithOriginNotFoundException;
import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.model.dto.FlightDto;
import airAstana.flightStatus.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Flight> getFlights(Optional<String> destination, Optional<String> origin) {
        if (origin.isEmpty() && destination.isEmpty()) {
            return flightRepository.findAllByOrderByArrival();
        } else if (origin.isPresent() && destination.isPresent()) {
            String originValue = origin.get();
            String destinationValue = destination.get();
            if (!flightRepository.existsByOrigin(originValue)) {
                throw new FlightsWithOriginNotFoundException("flight with origin not found'" + originValue);
            } else if (!flightRepository.existsByDestination(destinationValue)) {
                throw new FlightsWithDestinationNotFoundException("flight with destination not found" + destinationValue);
            }
            return flightRepository.findByOriginAndDestinationOrderByArrival(originValue, destinationValue);
        } else if (origin.isPresent()) {
            return flightRepository.findByOriginOrderByArrival(origin.get());
        } else {
            return flightRepository.findByDestinationOrderByArrival(destination.get());
        }
    }

    @Override
    @Transactional
    public Flight addFlight(FlightDto flightDto) {

        if (flightDto.getOrigin() == null || flightDto.getDestination() == null ||
                flightDto.getDeparture() == null || flightDto.getArrival() == null ||
                flightDto.getStatus() == null) {
            throw new IllegalArgumentException("one or more parameters are null");
        }

        if (flightDto.getArrival().isBefore(flightDto.getDeparture())) {
            throw new ArrivalIsBeforeDepartureException("arrival can't be before departure");
        }

        Flight flight = new Flight();
        flight.setOrigin(flightDto.getOrigin());
        flight.setDestination(flightDto.getDestination());
        flight.setDeparture(flightDto.getDeparture());
        flight.setArrival(flightDto.getArrival());
        flight.setStatus(flightDto.getStatus());

        return flightRepository.save(flight);
    }

    @Override
    @Transactional
    public Flight updateFlightStatus(Long id, Status status) {
        if (status == null || !EnumSet.of(Status.INTIME, Status.DELAYED, Status.CANCELLED).contains(status)) {
            throw new IllegalArgumentException("invalid status provided: " + status);
        }

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightWithIdNotFoundException("flight not found with id: " + id));

        flight.setStatus(status);

        return flightRepository.save(flight);
    }
}
