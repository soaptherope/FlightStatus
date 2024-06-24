package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.exception.InvalidCityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

public class TimeZoneServiceImplTests {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private TimeZoneServiceImpl timeZoneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCoordinates_InvalidCity_ExceptionThrown() throws Exception {
        String city = "InvalidCity";
        String responseBody = "{\"status\":\"ZERO_RESULTS\",\"results\":[]}";

        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(responseBody);

        assertThrows(InvalidCityException.class, () -> timeZoneService.getCoordinates(city));
    }
}
