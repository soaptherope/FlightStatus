package airAstana.flightStatus.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Column(name="Origin")
    private String origin;

    @Column(name="Destination")
    private String destination;

    @Column(name="Departure")
    private LocalDateTime departure;

    @Column(name="Arrival")
    private LocalDateTime arrival;

    @Column(name="Status")
    private STATUS status;

}
