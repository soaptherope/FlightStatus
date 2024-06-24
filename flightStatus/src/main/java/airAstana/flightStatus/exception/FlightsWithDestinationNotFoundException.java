package airAstana.flightStatus.exception;


public class FlightsWithDestinationNotFoundException extends RuntimeException {
    public FlightsWithDestinationNotFoundException(String message) {
        super(message);
    }
}
