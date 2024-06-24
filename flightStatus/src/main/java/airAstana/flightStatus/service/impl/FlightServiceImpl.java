package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.exception.FlightWithIdNotFoundException;
import airAstana.flightStatus.exception.FlightsWithDestinationNotFoundException;
import airAstana.flightStatus.exception.FlightsWithOriginAndDestinationNotFoundException;
import airAstana.flightStatus.exception.FlightsWithOriginNotFoundException;
import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.model.dto.FlightDto;
import airAstana.flightStatus.repository.FlightRepository;
import airAstana.flightStatus.service.FlightService;
import airAstana.flightStatus.service.TimeZoneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;


@Service
public class FlightServiceImpl implements FlightService {

    private static final String ORIGIN_DESTINATION_PATTERN = "^[a-zA-Z]+$";

    private final FlightRepository flightRepository;

    private final TimeZoneService timeZoneService;

    public FlightServiceImpl(FlightRepository flightRepository, TimeZoneService timeZoneService) {
        this.flightRepository = flightRepository;
        this.timeZoneService = timeZoneService;
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

    private void validateDepartureAndArrival(FlightDto flightDto) {
        OffsetDateTime departure = flightDto.getDeparture();
        OffsetDateTime arrival = flightDto.getArrival();

        if (departure == null || arrival == null) {
            throw new IllegalArgumentException("invalid date and time provided: " + departure + ", " + arrival);
        }

    }

    @Transactional
    private void validateFlight(FlightDto flightDto) {
        validateStatus(flightDto.getStatus());
        validateOrigin(flightDto.getOrigin());
        validateDestination(flightDto.getDestination());
        validateDepartureAndArrival(flightDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Flight> getFlights(String origin, String destination) {
        if (origin == null && destination == null) {
            return flightRepository.findAllByOrderByArrival();
        }

        if (origin != null && destination != null) {
            validateOrigin(origin);
            validateDestination(destination);

            if (!flightRepository.existsByOriginIgnoreCaseAndDestinationIgnoreCase(origin, destination)) {
                throw new FlightsWithOriginAndDestinationNotFoundException("Flights with specified origin and destination not found: " + origin + ", " + destination);
            }

            return flightRepository.findByOriginIgnoreCaseAndDestinationIgnoreCaseOrderByArrival(origin, destination);
        }

        if (origin != null) {
            validateOrigin(origin);

            if (!flightRepository.existsByOriginIgnoreCase(origin)) {
                throw new FlightsWithOriginNotFoundException("flights with specified origin not found: " + origin);
            }

            return flightRepository.findByOriginIgnoreCaseOrderByArrival(origin);
        }

        validateDestination(destination);

        if (!flightRepository.existsByDestinationIgnoreCase(destination)) {
            throw new FlightsWithDestinationNotFoundException("flights with specified destination not found: " + destination);
        }

        return flightRepository.findByDestinationIgnoreCaseOrderByArrival(destination);
    }

    @Override
    @Transactional
    public Flight addFlight(FlightDto flightDto) {
        validateFlight(flightDto);

        Flight flight = new Flight();
        flight.setOrigin(flightDto.getOrigin());
        flight.setDestination(flightDto.getDestination());

        flight.setDeparture(timeZoneService.getZonedDateTime(flightDto.getDeparture(), flightDto.getOrigin()));
        flight.setArrival(timeZoneService.getZonedDateTime(flightDto.getArrival(), flightDto.getDestination()));

        flight.setStatus(flightDto.getStatus());

        return flightRepository.save(flight);
    }

    @Override
    @Transactional
    public Flight updateFlightStatus(Long id, Status status) {
        validateStatus(status);
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightWithIdNotFoundException("flights with specified id not found: " + id));

        flight.setStatus(status);

        return flightRepository.save(flight);
    }
}
