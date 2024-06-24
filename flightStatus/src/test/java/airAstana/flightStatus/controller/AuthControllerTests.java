package airAstana.flightStatus.controller;

import airAstana.flightStatus.exception.UsernameTakenException;
import airAstana.flightStatus.model.User;
import airAstana.flightStatus.model.dto.JwtAuthResponse;
import airAstana.flightStatus.model.dto.RegisterLoginRequest;
import airAstana.flightStatus.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Test
    public void testRegister_NewUser_Success() throws Exception {
        RegisterLoginRequest request = new RegisterLoginRequest("newuser", "password");

        User newUser = new User("newuser", "password");
        when(authService.register(any(RegisterLoginRequest.class))).thenReturn(newUser);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        User responseUser = objectMapper.readValue(jsonResponse, User.class);
        assert responseUser.getUsername().equals("newuser");
    }

    @Test
    public void testRegister_UsernameTaken_ReturnsBadRequest() throws Exception {
        RegisterLoginRequest request = new RegisterLoginRequest("alishersharipov", "airastana");

        when(authService.register(any(RegisterLoginRequest.class))).thenThrow(UsernameTakenException.class);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogin_ValidCredentials_Success() throws Exception {
        RegisterLoginRequest request = new RegisterLoginRequest("user", "password");

        JwtAuthResponse jwtResponse = new JwtAuthResponse();
        jwtResponse.setToken("mock_jwt_token");
        when(authService.login(any(RegisterLoginRequest.class))).thenReturn(jwtResponse);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JwtAuthResponse response = objectMapper.readValue(jsonResponse, JwtAuthResponse.class);
        assert response.getToken().equals("mock_jwt_token");
    }

    @Test
    public void testLogin_InvalidCredentials_ReturnsBadRequest() throws Exception {
        RegisterLoginRequest request = new RegisterLoginRequest("invaliduser", "invalidpassword");

        when(authService.login(any(RegisterLoginRequest.class))).thenThrow(IllegalArgumentException.class);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAdmin_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/admin"))
                .andExpect(status().isOk());

        verify(authService).getAdmin();
    }
}
