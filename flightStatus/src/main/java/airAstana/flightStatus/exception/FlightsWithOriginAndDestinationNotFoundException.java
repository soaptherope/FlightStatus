package airAstana.flightStatus.exception;


public class FlightsWithOriginAndDestinationNotFoundException extends RuntimeException {
    public FlightsWithOriginAndDestinationNotFoundException(String message) {
        super(message);
    }
}
