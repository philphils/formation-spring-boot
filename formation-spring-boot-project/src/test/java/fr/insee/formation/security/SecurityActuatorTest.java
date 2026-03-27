package fr.insee.formation.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityActuatorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    // Test pour vérifier que l'accès à GET /actuator/env est refusé pour un
    // utilisateur avec le rôle USER
    @Test
    @WithMockUser(roles = "USER")
    public void testGetActuatorEnvWithUserRole() throws Exception {
        mockMvc.perform(get("/actuator/env"))
                .andExpect(status().isForbidden());
    }

    // Test pour vérifier que l'accès à GET /actuator/env est autorisé pour un
    // utilisateur avec le rôle ADMIN
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetActuatorEnvWithAdminRole() throws Exception {
        mockMvc.perform(get("/actuator/env"))
                .andExpect(status().isOk());
    }
}
