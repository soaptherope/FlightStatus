package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.exception.InvalidCityException;
import airAstana.flightStatus.service.TimeZoneService;
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


@Service
public class TimeZoneServiceImpl implements TimeZoneService {

    @Value("${google.apikey}")
    String API_KEY;

    public double[] getCoordinates(String city) throws InvalidCityException {
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
                throw new InvalidCityException("invalid city specified");
            }
        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return coordinates;
    }

    @Override
    public int getUtcOffset(double[] coordinates) {
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
                throw new JSONException("error while getting data");
            }
        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public OffsetDateTime getZonedDateTime(OffsetDateTime offsetDateTime, String city) {
        int utcOffset = getUtcOffset(getCoordinates(city));

        return OffsetDateTime.of(offsetDateTime.toLocalDateTime(), ZoneOffset.ofHours(utcOffset));
    }
}


