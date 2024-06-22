package airAstana.flightStatus.repository;

import airAstana.flightStatus.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByOriginIgnoreCaseOrderByArrival(String origin);

    List<Flight> findByDestinationIgnoreCaseOrderByArrival(String destination);

    List<Flight> findAllByOrderByArrival();

    List<Flight> findByOriginIgnoreCaseAndDestinationIgnoreCaseOrderByArrival(String origin, String destination);

    boolean existsByOriginIgnoreCase(String origin);

    boolean existsByDestinationIgnoreCase(String destination);

    boolean existsByOriginIgnoreCaseAndDestinationIgnoreCase(String origin, String destination);
}
