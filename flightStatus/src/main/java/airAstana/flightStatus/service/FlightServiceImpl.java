package airAstana.flightStatus.service;

import airAstana.flightStatus.exception.*;
import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.model.dto.FlightDto;
import airAstana.flightStatus.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
public class FlightServiceImpl implements FlightService {

    private static final String ORIGIN_DESTINATION_PATTERN = "^[a-zA-Z]+$";

    private final FlightRepository flightRepository;

    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    private void validateOrigin(String origin) {
        if (origin == null || !origin.matches(ORIGIN_DESTINATION_PATTERN)) {
            throw new IllegalArgumentException("invalid origin provided: " + origin);
        }
    }

    private void validateDestination(String destination) {
        if (destination == null || !destination.matches(ORIGIN_DESTINATION_PATTERN)) {
            throw new IllegalArgumentException("invalid destination provided: " + destination);
        }
    }

    private void validateStatus(Status status) {
        if (status == null || !EnumSet.of(Status.INTIME, Status.DELAYED, Status.CANCELLED).contains(status)) {
            throw new IllegalArgumentException("invalid status provided: " + status);
        }
    }

    private void validateDepartureAndArrival(ZonedDateTime departure, ZonedDateTime arrival) {
        if (departure == null || arrival == null ) {
            throw new IllegalArgumentException("invalid date and time provided: " + departure + ", " + arrival);
        }

        if (!arrival.isAfter(departure)) {
            throw new ArrivalIsBeforeDepartureException("arrival cannot be before departure");
        }
    }

    private void validateFlight(FlightDto flightDto) {
        validateStatus(flightDto.getStatus());
        validateOrigin(flightDto.getOrigin());
        validateDestination(flightDto.getDestination());
        validateDepartureAndArrival(flightDto.getDeparture(), flightDto.getArrival());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Flight> getFlights(Optional<String> origin, Optional<String> destination) {
        if (origin.isEmpty() && destination.isEmpty()) {

            return flightRepository.findAllByOrderByArrival();
        }

        else if (origin.isPresent() && destination.isPresent()) {
            validateOrigin(origin.get());
            validateDestination(destination.get());

            if (!flightRepository.existsByOriginIgnoreCaseAndDestinationIgnoreCase(origin.get(), destination.get())) {
                throw new FlightsWithOriginAndDestinationNotFoundException("flights with specified origin and destination not found: " + origin + ", " + destination);
            }

            return flightRepository.findByOriginAndDestinationOrderByArrival(origin.get(), destination.get());
        }

        else if (origin.isPresent()) {
            validateOrigin(origin.get());

            if (!flightRepository.existsByOriginIgnoreCase(origin.get())) {
                throw new FlightsWithOriginNotFoundException("flights with specified origin not found: " + origin);
            }

            return flightRepository.findByOriginOrderByArrival(origin.get());
        }

        else {
            validateDestination(destination.get());

            if (!flightRepository.existsByDestinationIgnoreCase(destination.get())) {
                throw new FlightsWithOriginNotFoundException("flights with specified destination not found: " + destination);
            }

            return flightRepository.findByDestinationOrderByArrival(destination.get());
        }
    }

    @Override
    @Transactional
    public Flight addFlight(FlightDto flightDto) {
        validateFlight(flightDto);

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
        validateStatus(status);
            Flight flight = flightRepository.findById(id)
                    .orElseThrow(() -> new FlightWithIdNotFoundException("flight with specified id not found: " + id));

            flight.setStatus(status);

            return flightRepository.save(flight);
    }
}
