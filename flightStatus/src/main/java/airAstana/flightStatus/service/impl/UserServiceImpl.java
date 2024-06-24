package airAstana.flightStatus.service.impl;

import airAstana.flightStatus.repository.UserRepository;
import airAstana.flightStatus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * implementation of UserService providing user details service.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * retrieves a UserDetailsService for the specified username.
     *
     * @return UserDetailsService containing user details
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    @Operation(summary = "get User Details Service")
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
