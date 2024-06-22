package airAstana.flightStatus.exception;


public class FlightsWithOriginNotFoundException extends RuntimeException {
    public FlightsWithOriginNotFoundException(String message) {
        super(message);
    }
}
