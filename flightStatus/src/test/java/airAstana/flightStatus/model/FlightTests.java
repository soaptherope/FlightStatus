package airAstana.flightStatus.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

public class FlightTests {

    @Test
    public void testFlightEntityMapping() {
        Long id = 1L;
        String origin = "Origin City";
        String destination = "Destination City";
        OffsetDateTime departure = OffsetDateTime.now();
        OffsetDateTime arrival = departure.plusHours(3);
        Status status = Status.INTIME;

        Flight flight = new Flight();
        flight.setId(id);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setDeparture(departure);
        flight.setArrival(arrival);
        flight.setStatus(status);

        Assertions.assertEquals(id, flight.getId());
        Assertions.assertEquals(origin, flight.getOrigin());
        Assertions.assertEquals(destination, flight.getDestination());
        Assertions.assertEquals(departure, flight.getDeparture());
        Assertions.assertEquals(arrival, flight.getArrival());
        Assertions.assertEquals(status, flight.getStatus());
    }
}
