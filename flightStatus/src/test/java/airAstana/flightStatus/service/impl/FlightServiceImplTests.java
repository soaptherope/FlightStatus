package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.exception.*;
import airAstana.flightStatus.model.Flight;
import airAstana.flightStatus.model.Status;
import airAstana.flightStatus.model.dto.FlightDto;
import airAstana.flightStatus.repository.FlightRepository;
import airAstana.flightStatus.service.TimeZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FlightServiceImplTests {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private TimeZoneService timeZoneService;

    @InjectMocks
    private FlightServiceImpl flightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFlights_AllFlights_Success() {
        Flight flight1 = new Flight(1L, "Origin1", "Destination1", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.INTIME);
        Flight flight2 = new Flight(1L, "Origin2", "Destination2", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.DELAYED);
        List<Flight> expectedFlights = Arrays.asList(flight1, flight2);

        when(flightRepository.findAllByOrderByArrival()).thenReturn(expectedFlights);

        List<Flight> actualFlights = flightService.getFlights(null, null);

        assertEquals(expectedFlights.size(), actualFlights.size());
        assertEquals(expectedFlights, actualFlights);
    }

    @Test
    void testGetFlights_ByOriginAndDestination_Success() {
        String origin = "Origin";
        String destination = "Destination";
        Flight flight1 = new Flight(1L, origin, destination, OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.INTIME);
        Flight flight2 = new Flight(1L, origin, destination, OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.DELAYED);
        List<Flight> expectedFlights = Arrays.asList(flight1, flight2);

        when(flightRepository.existsByOriginIgnoreCaseAndDestinationIgnoreCase(origin, destination)).thenReturn(true);
        when(flightRepository.findByOriginIgnoreCaseAndDestinationIgnoreCaseOrderByArrival(origin, destination)).thenReturn(expectedFlights);

        List<Flight> actualFlights = flightService.getFlights(origin, destination);

        assertEquals(expectedFlights.size(), actualFlights.size());
        assertEquals(expectedFlights, actualFlights);
    }

    @Test
    void testGetFlights_ByOrigin_Success() {
        String origin = "Origin";
        Flight flight1 = new Flight(1L, origin, "Destination1", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.INTIME);
        Flight flight2 = new Flight(1L, origin, "Destination2", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.DELAYED);
        List<Flight> expectedFlights = Arrays.asList(flight1, flight2);

        when(flightRepository.existsByOriginIgnoreCase(origin)).thenReturn(true);
        when(flightRepository.findByOriginIgnoreCaseOrderByArrival(origin)).thenReturn(expectedFlights);

        List<Flight> actualFlights = flightService.getFlights(origin, null);

        assertEquals(expectedFlights.size(), actualFlights.size());
        assertEquals(expectedFlights, actualFlights);
    }

    @Test
    void testGetFlights_ByDestination_Success() {
        String destination = "Destination";
        Flight flight1 = new Flight(1L, "Origin1", destination, OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.INTIME);
        Flight flight2 = new Flight(1L, "Origin2", destination, OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.DELAYED);
        List<Flight> expectedFlights = Arrays.asList(flight1, flight2);

        when(flightRepository.existsByDestinationIgnoreCase(destination)).thenReturn(true);
        when(flightRepository.findByDestinationIgnoreCaseOrderByArrival(destination)).thenReturn(expectedFlights);

        List<Flight> actualFlights = flightService.getFlights(null, destination);

        assertEquals(expectedFlights.size(), actualFlights.size());
        assertEquals(expectedFlights, actualFlights);
    }

    @Test
    void testGetFlights_ByOriginAndDestination_NotFound_ExceptionThrown() {
        String origin = "Origin";
        String destination = "Destination";

        when(flightRepository.existsByOriginIgnoreCaseAndDestinationIgnoreCase(origin, destination)).thenReturn(false);

        assertThrows(FlightsWithOriginAndDestinationNotFoundException.class, () -> flightService.getFlights(origin, destination));
    }

    @Test
    void testGetFlights_ByOrigin_NotFound_ExceptionThrown() {
        String origin = "NonExistingOrigin";

        when(flightRepository.existsByOriginIgnoreCase(origin)).thenReturn(false);

        assertThrows(FlightsWithOriginNotFoundException.class, () -> flightService.getFlights(origin, null));
    }

    @Test
    void testGetFlights_ByDestination_NotFound_ExceptionThrown() {
        String destination = "NonExistingDestination";

        when(flightRepository.existsByDestinationIgnoreCase(destination)).thenReturn(false);

        assertThrows(FlightsWithDestinationNotFoundException.class, () -> flightService.getFlights(null, destination));
    }

    @Test
    void testAddFlight_ValidFlightDto_Success() {
        FlightDto flightDto = new FlightDto("Origin", "Destination", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.INTIME);
        Flight savedFlight = new Flight(1L, flightDto.getOrigin(), flightDto.getDestination(), flightDto.getDeparture(), flightDto.getArrival(), flightDto.getStatus());

        when(timeZoneService.getZonedDateTime(any(), anyString())).thenReturn(OffsetDateTime.now());
        when(flightRepository.save(any())).thenReturn(savedFlight);

        Flight createdFlight = flightService.addFlight(flightDto);

        assertNotNull(createdFlight);
        assertEquals(flightDto.getOrigin(), createdFlight.getOrigin());
        assertEquals(flightDto.getDestination(), createdFlight.getDestination());
        assertEquals(flightDto.getDeparture(), createdFlight.getDeparture());
        assertEquals(flightDto.getArrival(), createdFlight.getArrival());
        assertEquals(flightDto.getStatus(), createdFlight.getStatus());
    }

    @Test
    void testAddFlight_InvalidFlightDto_ExceptionThrown() {
        FlightDto flightDto = new FlightDto();

        assertThrows(IllegalArgumentException.class, () -> flightService.addFlight(flightDto));
        verify(flightRepository, never()).save(any());
    }

    @Test
    void testUpdateFlightStatus_ExistingFlightId_Success() {
        Long flightId = 1L;
        Status newStatus = Status.DELAYED;
        Flight existingFlight = new Flight(1L, "Origin", "Destination", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.INTIME);
        Flight updatedFlight = new Flight(1L, existingFlight.getOrigin(), existingFlight.getDestination(), existingFlight.getDeparture(), existingFlight.getArrival(), newStatus);

        when(flightRepository.findById(flightId)).thenReturn(java.util.Optional.of(existingFlight));
        when(flightRepository.save(any())).thenReturn(updatedFlight);

        Flight returnedFlight = flightService.updateFlightStatus(flightId, newStatus);

        assertEquals(updatedFlight.getStatus(), returnedFlight.getStatus());
        verify(flightRepository, times(1)).save(existingFlight);
    }

    @Test
    void testUpdateFlightStatus_NonExistingFlightId_ExceptionThrown() {
        Long nonExistingFlightId = 999L;
        Status newStatus = Status.DELAYED;

        when(flightRepository.findById(nonExistingFlightId)).thenReturn(java.util.Optional.empty());

        assertThrows(FlightWithIdNotFoundException.class, () -> flightService.updateFlightStatus(nonExistingFlightId, newStatus));
        verify(flightRepository, never()).save(any());
    }

    @Test
    void testUpdateFlightStatus_InvalidStatus_ExceptionThrown() {
        Long flightId = 1L;
        Status invalidStatus = null;
        Flight existingFlight = new Flight(1L,"Origin", "Destination", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), Status.INTIME);

        when(flightRepository.findById(flightId)).thenReturn(java.util.Optional.of(existingFlight));

        assertThrows(IllegalArgumentException.class, () -> flightService.updateFlightStatus(flightId, invalidStatus));
        verify(flightRepository, never()).save(any());
    }
}
