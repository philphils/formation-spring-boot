package fr.insee.formation.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import fr.insee.formation.model.CategorieJuridique;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.repository.UniteLegaleRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-integration")
public class UniteLegaleControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UniteLegaleRepository uniteLegaleRepository;

    @BeforeEach
    public void setup() {
        // Nettoyer la base de données avant chaque test
        uniteLegaleRepository.deleteAll();

        // Créer quelques unités légales pour les tests
        UniteLegale uniteLegale1 = new UniteLegale();
        uniteLegale1.setSiren("123456789");
        uniteLegale1.setDenomination("Test Company 1");
        uniteLegale1.setCategorieJuridique(CategorieJuridique.SA);

        UniteLegale uniteLegale2 = new UniteLegale();
        uniteLegale2.setSiren("987654321");
        uniteLegale2.setDenomination("Test Company 2");
        uniteLegale2.setCategorieJuridique(CategorieJuridique.SARL);

        // Sauvegarder les unités légales en base de données
        uniteLegaleRepository.saveAll(List.of(uniteLegale1, uniteLegale2));
    }

    @Test
    public void testStreamUnitesLegales() throws IOException {
        // Appeler l'endpoint GET /api/unites-legales/stream
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/unites-legales/stream",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<String>() {
                });

        // Vérifier que la réponse est OK
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Vérifier le contenu de la réponse
        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        // Vérifier que le flux contient les unités légales créées
        assertThat(responseBody).contains("123456789");
        assertThat(responseBody).contains("Test Company 1");
        assertThat(responseBody).contains("SA");
        assertThat(responseBody).contains("987654321");
        assertThat(responseBody).contains("Test Company 2");
        assertThat(responseBody).contains("SARL");
    }

}