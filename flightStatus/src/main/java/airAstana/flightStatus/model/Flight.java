package airAstana.flightStatus.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;


@Data
@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;

    private String destination;

    private OffsetDateTime departure;

    private OffsetDateTime arrival;

    @Enumerated(EnumType.STRING)
    private Status status;
}
