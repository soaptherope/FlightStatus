package airAstana.flightStatus.service;

import airAstana.flightStatus.exception.InvalidCityException;
import airAstana.flightStatus.model.dto.FlightDto;

import java.time.ZonedDateTime;

public interface TimeZoneService {
    double[] getCoordinates(String city);
    int getUtcOffset(double[] coordinates);

    ZonedDateTime getZonedDateTime(ZonedDateTime zonedDateTime, String city);
}
