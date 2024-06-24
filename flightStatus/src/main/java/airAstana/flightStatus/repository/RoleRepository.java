package airAstana.flightStatus.repository;

import airAstana.flightStatus.model.EnumRole;
import airAstana.flightStatus.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(EnumRole role);
}
