package fr.insee.formation.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import fr.insee.formation.model.CategorieJuridique;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.service.EtablissementService;
import fr.insee.formation.service.UniteLegaleService;

@WebMvcTest(UniteLegaleController.class)
public class UniteLegaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UniteLegaleService uniteLegaleService;

    @MockBean
    private EtablissementService etablissementService;

    @Test
    public void getUniteLegaleBySiren_WithValidSiren_Returns200() throws Exception {
        // Given
        String validSiren = "123456789";
        UniteLegale mockUniteLegale = new UniteLegale();
        mockUniteLegale.setId(1L);
        mockUniteLegale.setSiren(validSiren);
        mockUniteLegale.setDenomination("Test Company");
        mockUniteLegale.setCategorieJuridique(CategorieJuridique.SA);

        given(uniteLegaleService.findBySiren(validSiren)).willReturn(Optional.of(mockUniteLegale));

        // When & Then
        mockMvc.perform(get("/api/unites-legales/siren/{siren}", validSiren)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.siren").value(validSiren))
                .andExpect(jsonPath("$.denomination").value("Test Company"));
    }

    @Test
    public void getUniteLegaleBySiren_WithNonExistentSiren_Returns404() throws Exception {
        // Given
        String nonExistentSiren = "999999999";
        given(uniteLegaleService.findBySiren(nonExistentSiren)).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/unites-legales/siren/{siren}", nonExistentSiren)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Aucune unité légale correspondant au SIREN: " + nonExistentSiren));
    }
}