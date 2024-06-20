package airAstana.flightStatus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;

    private String destination;

    private LocalDateTime departure;

    private LocalDateTime arrival;

    private Status status;
}
