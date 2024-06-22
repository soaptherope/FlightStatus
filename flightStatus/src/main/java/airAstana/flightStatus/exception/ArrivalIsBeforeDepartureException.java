package airAstana.flightStatus.exception;


public class ArrivalIsBeforeDepartureException extends RuntimeException {
    public ArrivalIsBeforeDepartureException(String message) {
        super(message);
    }
}
