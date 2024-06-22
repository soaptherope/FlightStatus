package airAstana.flightStatus.service;

import airAstana.flightStatus.model.dto.FlightDto;

public interface TimeZoneService {

    int getTimeZoneDifference(FlightDto flightDto);
}
