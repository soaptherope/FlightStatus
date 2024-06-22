package airAstana.flightStatus.exception;

import jakarta.persistence.criteria.CriteriaBuilder;

public class InvalidCityException extends RuntimeException {
    public InvalidCityException(String message) {
        super(message);
    }
}
