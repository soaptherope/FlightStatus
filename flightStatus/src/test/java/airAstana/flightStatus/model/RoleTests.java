package airAstana.flightStatus.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoleTests {
    @Test
    public void testRoleEntityMapping() {
        Long id = 1L;
        EnumRole roleName = EnumRole.USER;

        Role role = new Role(roleName);
        role.setId(id);

        Assertions.assertEquals(id, role.getId());
        Assertions.assertEquals(roleName, role.getName());
    }
}
