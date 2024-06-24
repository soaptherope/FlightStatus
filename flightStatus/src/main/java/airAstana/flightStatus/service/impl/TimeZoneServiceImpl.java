package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.exception.InvalidCityException;
import airAstana.flightStatus.service.TimeZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * implementation of TimeZoneService providing operations related to time zones and city coordinates.
 */
@Service
public class TimeZoneServiceImpl implements TimeZoneService {

    @Value("${google.apikey}")
    String API_KEY;

    /**
     * retrieves coordinates (latitude and longitude) for a given city using Google Maps Geocoding API.
     *
     * @param city Name of the city
     * @return Array containing latitude and longitude coordinates
     * @throws InvalidCityException If the city provided is invalid or not found
     */
    @Override
    @Operation(summary = "get coordinates for city")
    public double[] getCoordinates(@Parameter(description = "city name for which coordinates are requested") String city) throws InvalidCityException {
        double[] coordinates = new double[2];

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("https://maps.googleapis.com/maps/api/geocode/json?address=" + city + "&key=" + API_KEY))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonObject = new JSONObject(response.body());
            String status = jsonObject.getString("status");

            if ("OK".equals(status)) {
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                if (resultsArray.length() > 0) {
                    JSONObject firstResult = resultsArray.getJSONObject(0);
                    JSONObject geometry = firstResult.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");

                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    coordinates[0] = lat;
                    coordinates[1] = lng;

                    return coordinates;
                }
            } else {
                throw new InvalidCityException("Invalid city specified: " + city);
            }
        } catch (IOException | InterruptedException | JSONException e) {
            throw new InvalidCityException("Error retrieving coordinates for city: " + city);
        }
        return coordinates;
    }

    /**
     * retrieves the UTC offset in hours for a given geographical coordinates using Google Maps Time Zone API.
     *
     * @param coordinates Array containing latitude and longitude coordinates
     * @return UTC offset in hours
     */
    @Override
    @Operation(summary = "Get UTC Offset for Coordinates")
    public int getUtcOffset(@Parameter(description = "array containing latitude and longitude coordinates") double[] coordinates) {
        double lat = coordinates[0];
        double lng = coordinates[1];

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("https://maps.googleapis.com/maps/api/timezone/json?location=" + lat + "%2C" + lng + "&timestamp=1577836800&key=" + API_KEY))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonObject = new JSONObject(response.body());
            String status = jsonObject.getString("status");
            if ("OK".equals(status)) {
                return jsonObject.getInt("rawOffset") / 3600;
            } else {
                throw new JSONException("Error while getting UTC offset");
            }
        } catch (IOException | InterruptedException | JSONException e) {
            throw new RuntimeException("Error retrieving UTC offset for coordinates");
        }
    }

    /**
     * Retrieves OffsetDateTime adjusted to the time zone of a given city.
     *
     * @param offsetDateTime Original OffsetDateTime
     * @param city           Name of the city
     * @return OffsetDateTime adjusted to the city's time zone
     */
    @Override
    @Operation(summary = "Get Zoned Date Time for City")
    public OffsetDateTime getZonedDateTime(@Parameter(description = "Original OffsetDateTime") OffsetDateTime offsetDateTime,
                                           @Parameter(description = "City name for which time zone is requested") String city) {
        int utcOffset = getUtcOffset(getCoordinates(city));

        return OffsetDateTime.of(offsetDateTime.toLocalDateTime(), ZoneOffset.ofHours(utcOffset));
    }
}
