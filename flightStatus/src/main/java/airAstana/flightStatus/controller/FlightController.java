package airAstana.flightStatus.controller;

import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping("/add")
    public Flight addFlight(@RequestBody Flight flight) {
        return flightService.addFlight(flight);
    }

    @GetMapping("/arrival")
    public List<Flight> getFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination) {
        return flightService.getFlights(origin, destination);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<Flight> updateFlightStatus(@PathVariable int id, @RequestParam Status status) {
        Flight updatedFlight = flightService.updateFlightStatus(id, status);
        return ResponseEntity.ok(updatedFlight);
    }

}
