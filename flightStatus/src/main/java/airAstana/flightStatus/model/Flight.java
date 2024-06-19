package airAstana.flightStatus.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;

public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private int id;

    @Column(name="Origin")
    private String origin;

    @Column(name="Destination")
    private String destination;

    @Column(name="Arrival")
    private DateTimeFormat arrival;

    @Column(name="Status")
    private Status status;

}
