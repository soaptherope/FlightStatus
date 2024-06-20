package airAstana.flightStatus.controller;

import airAstana.flightStatus.exception.ArrivalIsBeforeDepartureException;
import airAstana.flightStatus.exception.FlightWithIdNotFoundException;
import airAstana.flightStatus.exception.FlightsWithDestinationNotFoundException;
import airAstana.flightStatus.exception.FlightsWithOriginNotFoundException;
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
            return ResponseEntity.ok(flightService.getFlights(Optional.ofNullable(origin), Optional.ofNullable(destination)));
        } catch (FlightsWithOriginNotFoundException | FlightsWithDestinationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlightStatus(@PathVariable Long id, @RequestParam Status status) {
        try {
            return ResponseEntity.ok(flightService.updateFlightStatus(id, status));
        } catch (FlightWithIdNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
