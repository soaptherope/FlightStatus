package airAstana.flightStatus.controller;

import airAstana.flightStatus.exception.FlightWithIdNotFoundException;
import airAstana.flightStatus.exception.FlightsWithOriginAndDestinationNotFoundException;
import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.model.dto.FlightDto;
import airAstana.flightStatus.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightControllerTests {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightController flightController;

    private FlightDto testFlightDto;
    private Flight testFlight;

    @BeforeEach
    void setUp() {
        testFlightDto = new FlightDto();
        testFlightDto.setOrigin("Origin");
        testFlightDto.setDestination("Destination");
        testFlightDto.setDeparture(OffsetDateTime.now());
        testFlightDto.setArrival(OffsetDateTime.now().plusHours(2));
        testFlightDto.setStatus(Status.INTIME);

        testFlight = new Flight();
        testFlight.setId(1L);
        testFlight.setOrigin("Origin");
        testFlight.setDestination("Destination");
        testFlight.setDeparture(OffsetDateTime.now());
        testFlight.setArrival(OffsetDateTime.now().plusHours(2));
        testFlight.setStatus(Status.INTIME);
    }

    @Test
    void testAddFlight_Success() {
        when(flightService.addFlight(any(FlightDto.class))).thenReturn(testFlight);

        ResponseEntity<Flight> response = flightController.addFlight(testFlightDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testFlight.getId(), response.getBody().getId());
    }

    @Test
    void testAddFlight_InvalidInput_ReturnsBadRequest() {
        when(flightService.addFlight(any(FlightDto.class))).thenThrow(IllegalArgumentException.class);

        ResponseEntity<Flight> response = flightController.addFlight(testFlightDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetFlights_WithOriginAndDestination_Success() {
        when(flightService.getFlights(eq("Origin"), eq("Destination"))).thenReturn(Collections.singletonList(testFlight));

        ResponseEntity<List<Flight>> response = flightController.getFlights("Origin", "Destination");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testFlight.getId(), response.getBody().get(0).getId());
    }

    @Test
    void testGetFlights_WithOrigin_Success() {
        when(flightService.getFlights(eq("Origin"), isNull())).thenReturn(Collections.singletonList(testFlight));

        ResponseEntity<List<Flight>> response = flightController.getFlights("Origin", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testFlight.getId(), response.getBody().get(0).getId());
    }

    @Test
    void testGetFlights_WithDestination_Success() {
        when(flightService.getFlights(isNull(), eq("Destination"))).thenReturn(Collections.singletonList(testFlight));

        ResponseEntity<List<Flight>> response = flightController.getFlights(null, "Destination");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testFlight.getId(), response.getBody().get(0).getId());
    }

    @Test
    void testGetFlights_NoFlightsFound_ReturnsNotFound() {
        when(flightService.getFlights(anyString(), anyString())).thenThrow(FlightsWithOriginAndDestinationNotFoundException.class);

        ResponseEntity<List<Flight>> response = flightController.getFlights("Origin", "Destination");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateFlightStatus_Success() {
        when(flightService.updateFlightStatus(eq(1L), any(Status.class))).thenReturn(testFlight);

        ResponseEntity<Flight> response = flightController.updateFlightStatus(1L, "INTIME");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testFlight.getId(), response.getBody().getId());
    }

    @Test
    void testUpdateFlightStatus_FlightNotFound_ReturnsNotFound() {
        when(flightService.updateFlightStatus(anyLong(), any(Status.class))).thenThrow(FlightWithIdNotFoundException.class);

        ResponseEntity<Flight> response = flightController.updateFlightStatus(1L, "INTIME");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
