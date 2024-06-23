package airAstana.flightStatus.service;

import java.time.OffsetDateTime;


public interface TimeZoneService {
    double[] getCoordinates(String city);
    int getUtcOffset(double[] coordinates);

    OffsetDateTime getZonedDateTime(OffsetDateTime offsetDateTime, String city);
}
