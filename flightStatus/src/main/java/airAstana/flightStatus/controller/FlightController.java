package airAstana.flightStatus.controller;

import airAstana.flightStatus.exception.FlightWithIdNotFoundException;
import airAstana.flightStatus.exception.FlightsWithDestinationNotFoundException;
import airAstana.flightStatus.exception.FlightsWithOriginAndDestinationNotFoundException;
import airAstana.flightStatus.exception.FlightsWithOriginNotFoundException;
import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.model.dto.FlightDto;
import airAstana.flightStatus.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller class for managing flights.
 */
@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * endpoint to add a new flight.
     *
     * @param flightDto FlightDto containing flight details
     * @return ResponseEntity with added Flight object if successful else returns bad request
     */
    @PostMapping("/add")
    @Operation(summary = "add a new flight")
    public ResponseEntity<Flight> addFlight(@RequestBody FlightDto flightDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(flightService.addFlight(flightDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * endpoint to retrieve flights based on origin and/or destination.
     *
     * @param origin      Optional parameter for flight origin
     * @param destination Optional parameter for flight destination
     * @return ResponseEntity with list of Flight objects matching criteria if found else returns not found
     */
    @GetMapping("/arrivals")
    @Operation(summary = "retrieve flights based on origin and/or destination sorted by arrival")
    public ResponseEntity<List<Flight>> getFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination) {
        try {
            return ResponseEntity.ok(flightService.getFlights(origin, destination));
        } catch (FlightsWithOriginNotFoundException | FlightsWithDestinationNotFoundException |
                 FlightsWithOriginAndDestinationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * endpoint to update the status of a flight.
     *
     * @param id     Path variable for flight ID
     * @param status Request parameter for new flight status
     * @return ResponseEntity with updated Flight object if successful else returns not found or bad request
     */
    @PutMapping("/edit/{id}")
    @Operation(summary = "update the status of a flight")
    public ResponseEntity<Flight> updateFlightStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Status flightStatus = Status.valueOf(status.toUpperCase());
            return ResponseEntity.ok(flightService.updateFlightStatus(id, flightStatus));
        } catch (FlightWithIdNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
