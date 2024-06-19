package airAstana.flightStatus.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private int id;

    @Column(name="Username")
    private String username;

    @Column(name="Password")
    private String password;

    @Column(name="RoleId")
    private int roleId;

}
