package airAstana.flightStatus.exception;

public class FlightWithIdNotFoundException extends RuntimeException {
    public FlightWithIdNotFoundException(String message) {
        super(message);
    }
}
