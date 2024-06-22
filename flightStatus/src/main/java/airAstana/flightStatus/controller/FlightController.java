package airAstana.flightStatus.controller;

import airAstana.flightStatus.exception.*;
import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.model.dto.FlightDto;
import airAstana.flightStatus.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping("/add")
    public ResponseEntity<Flight> addFlight(@RequestBody FlightDto flightDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(flightService.addFlight(flightDto));
        } catch (ArrivalIsBeforeDepartureException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/arrivals")
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

    @PutMapping("/{id}")
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
