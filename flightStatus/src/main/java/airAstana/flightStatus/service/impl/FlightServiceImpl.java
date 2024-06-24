package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.configuration.LoggerManager;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of FlightService providing operations for managing flights.
 */
@Service
public class FlightServiceImpl implements FlightService {

    private static final Logger logger = LoggerManager.getLogger();

    private static final String ORIGIN_DESTINATION_PATTERN = "^[a-zA-Z]+$";

    private final FlightRepository flightRepository;

    private final TimeZoneService timeZoneService;

    public FlightServiceImpl(FlightRepository flightRepository, TimeZoneService timeZoneService) {
        this.flightRepository = flightRepository;
        this.timeZoneService = timeZoneService;
    }

    /**
     * Validates the origin string against a regex pattern.
     *
     * @param origin Origin to validate
     * @throws IllegalArgumentException if origin is null or does not match the expected pattern
     */
    private void validateOrigin(String origin) {
        if (origin == null || !origin.matches(ORIGIN_DESTINATION_PATTERN)) {
            throw new IllegalArgumentException("Invalid origin provided: " + origin);
        }
    }

    /**
     * Validates the destination string against a regex pattern.
     *
     * @param destination Destination to validate
     * @throws IllegalArgumentException if destination is null or does not match the expected pattern
     */
    private void validateDestination(String destination) {
        if (destination == null || !destination.matches(ORIGIN_DESTINATION_PATTERN)) {
            throw new IllegalArgumentException("Invalid destination provided: " + destination);
        }
    }

    /**
     * Validates the status against a predefined set of values.
     *
     * @param status Status to validate
     * @throws IllegalArgumentException if status is null or not one of INTIME, DELAYED, or CANCELLED
     */
    private void validateStatus(Status status) {
        if (status == null || !EnumSet.of(Status.INTIME, Status.DELAYED, Status.CANCELLED).contains(status)) {
            throw new IllegalArgumentException("Invalid status provided: " + status);
        }
    }

    /**
     * Validates the departure and arrival date-time in the flight DTO.
     *
     * @param flightDto Flight DTO containing departure and arrival date-time
     * @throws IllegalArgumentException if departure or arrival date-time is null
     */
    private void validateDepartureAndArrival(FlightDto flightDto) {
        OffsetDateTime departure = flightDto.getDeparture();
        OffsetDateTime arrival = flightDto.getArrival();

        if (departure == null || arrival == null) {
            throw new IllegalArgumentException("Invalid date and time provided: " + departure + ", " + arrival);
        }
    }

    /**
     * Validates the entire flight DTO object.
     *
     * @param flightDto Flight DTO to validate
     */
    @Transactional
    private void validateFlight(FlightDto flightDto) {
        validateStatus(flightDto.getStatus());
        validateOrigin(flightDto.getOrigin());
        validateDestination(flightDto.getDestination());
        validateDepartureAndArrival(flightDto);
    }

    /**
     * Retrieves flights based on origin and destination.
     *
     * @param origin      Origin of the flights (optional)
     * @param destination Destination of the flights (optional)
     * @return List of flights matching the origin and destination criteria
     * @throws FlightsWithOriginNotFoundException            if flights with the specified origin are not found
     * @throws FlightsWithDestinationNotFoundException       if flights with the specified destination are not found
     * @throws FlightsWithOriginAndDestinationNotFoundException if flights with the specified origin and destination are not found
     */
    @Override
    @Transactional(readOnly = true)
    @Operation(summary = "Get Flights", description = "Retrieves flights based on origin and destination")
    public List<Flight> getFlights(@Parameter(description = "Origin of the flights") String origin,
                                   @Parameter(description = "Destination of the flights") String destination) {
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
                throw new FlightsWithOriginNotFoundException("Flights with specified origin not found: " + origin);
            }

            return flightRepository.findByOriginIgnoreCaseOrderByArrival(origin);
        }

        validateDestination(destination);

        if (!flightRepository.existsByDestinationIgnoreCase(destination)) {
            throw new FlightsWithDestinationNotFoundException("Flights with specified destination not found: " + destination);
        }

        return flightRepository.findByDestinationIgnoreCaseOrderByArrival(destination);
    }

    /**
     * Adds a new flight based on the provided flight DTO.
     *
     * @param flightDto Flight DTO containing flight details
     * @return Created flight entity
     * @throws IllegalArgumentException if any validation on the flight DTO fails
     */
    @Override
    @Transactional
    @Operation(summary = "Add Flight", description = "Adds a new flight based on the provided flight DTO")
    public Flight addFlight(@Parameter(description = "Flight DTO containing flight details") FlightDto flightDto) {
        validateFlight(flightDto);

        Flight flight = new Flight();
        flight.setOrigin(flightDto.getOrigin());
        flight.setDestination(flightDto.getDestination());

        flight.setDeparture(timeZoneService.getZonedDateTime(flightDto.getDeparture(), flightDto.getOrigin()));
        flight.setArrival(timeZoneService.getZonedDateTime(flightDto.getArrival(), flightDto.getDestination()));

        flight.setStatus(flightDto.getStatus());

        logger.info("A new flight has been created");
        return flightRepository.save(flight);
    }

    /**
     * Updates the status of a flight identified by its ID.
     *
     * @param id     ID of the flight to update
     * @param status New status to set for the flight
     * @return Updated flight entity
     * @throws FlightWithIdNotFoundException if no flight with the specified ID is found
     * @throws IllegalArgumentException     if the status is invalid
     */
    @Override
    @Transactional
    @Operation(summary = "Update Flight Status", description = "Updates the status of a flight identified by its ID")
    public Flight updateFlightStatus(@Parameter(description = "ID of the flight to update") Long id,
                                     @Parameter(description = "New status to set for the flight") Status status) {
        validateStatus(status);
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightWithIdNotFoundException("Flight with specified ID not found: " + id));

        flight.setStatus(status);

        logger.info("Flight status with ID " + flight.getId() + " has been changed to " + status);
        return flightRepository.save(flight);
    }
}
