package airAstana.flightStatus.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * represents a user entity with authentication details.
 */
@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "unique identifier for the user")
    private Long id;

    @Schema(description = "username of the user")
    private String username;

    @Schema(description = "password of the user (encrypted)")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @Schema(description = "role assigned to the user")
    private Role role;

    /**
     * constructor to initialize a user with username and password.
     *
     * @param username username of the user
     * @param password password of the user (encrypted)
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * returns the authorities granted to the user.
     *
     * @return Collection of authorities (roles)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getName().name()));
    }

    /**
     * checks if the user's account is non-expired.
     *
     * @return true if the user's account is non-expired false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * checks if the user's account is non-locked.
     *
     * @return true if the user's account is non-locked false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * checks if the user's credentials are non-expired.
     *
     * @return true if the user's credentials are non-expired false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * checks if the user is enabled.
     *
     * @return true if the user is enabled false otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
