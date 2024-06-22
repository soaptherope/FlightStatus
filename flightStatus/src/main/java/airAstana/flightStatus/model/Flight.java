package airAstana.flightStatus.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.ZonedDateTime;


@Data
@Entity
@Table(name="flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;

    private String destination;

    private ZonedDateTime departure;

    private ZonedDateTime arrival;

    @Enumerated(EnumType.STRING)
    private Status status;
}
