package airAstana.flightStatus.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTests {

    @Test
    public void testUserEntityMapping() {
        Long id = 1L;
        String username = "testuser";
        String password = "password";
        Role role = new Role();
        role.setId(1L);
        role.setName(EnumRole.USER);

        User user = new User(username, password);
        user.setId(id);
        user.setRole(role);

        Assertions.assertEquals(id, user.getId());
        Assertions.assertEquals(username, user.getUsername());
        Assertions.assertEquals(password, user.getPassword());
        Assertions.assertEquals(role, user.getRole());
    }
}
