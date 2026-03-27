package fr.insee.formation.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.insee.formation.controller.UniteLegaleController;
import fr.insee.formation.model.CategorieJuridique;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.service.EtablissementService;
import fr.insee.formation.service.UniteLegaleService;

@WebMvcTest(UniteLegaleController.class)
public class SecurityUniteLegaleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UniteLegaleService uniteLegaleService;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper; // Spring l'injecte automatiquement dans @WebMvcTest

    // Test pour vérifier que l'accès à GET /unite-legales est autorisé pour un
    // utilisateur authentifié
    @Test
    @WithMockUser(roles = "USER")
    public void testGetUniteLegalesWithUserRole() throws Exception {
        mockMvc.perform(get("/api/unites-legales"))
                .andExpect(status().isOk());
    }

    // Test pour vérifier que l'accès à POST /api/unites-legales est refusé pour un
    // utilisateur avec le rôle USER
    @Test
    @WithMockUser(roles = "USER")
    public void testPostUniteLegalesWithUserRole() throws Exception {
        UniteLegale unite = new UniteLegale();
        unite.setSiren("123456789");
        unite.setDenomination("Ma Super Entreprise");
        unite.setCategorieJuridique(CategorieJuridique.SA);

        mockMvc.perform(post("/api/unites-legales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unite))) // On envoie le JSON ici
                .andExpect(status().isForbidden());
    }

    // Test pour vérifier que l'accès à POST /api/unites-legales est autorisé pour
    // un
    // utilisateur avec le rôle GESTIONNAIRE
    @Test
    @WithMockUser(roles = "GESTIONNAIRE")
    public void testPostUniteLegalesWithGestionnaireRole() throws Exception {
        UniteLegale unite = new UniteLegale();
        unite.setSiren("123456789");
        unite.setDenomination("Ma Super Entreprise");
        unite.setCategorieJuridique(CategorieJuridique.SA);

        mockMvc.perform(post("/api/unites-legales")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unite))) // On envoie le JSON ici
                .andExpect(status().isCreated());
    }

}