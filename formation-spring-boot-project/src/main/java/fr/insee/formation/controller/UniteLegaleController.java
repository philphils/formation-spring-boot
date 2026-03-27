package fr.insee.formation.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import fr.insee.formation.controller.dto.ErrorResponse;
import fr.insee.formation.model.Etablissement;
import fr.insee.formation.model.UniteLegale;
import fr.insee.formation.service.EtablissementService;
import fr.insee.formation.service.UniteLegaleService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/unites-legales")
@PreAuthorize("hasRole('USER')")
public class UniteLegaleController {

    private static final String UNITE_NOT_FOUND = "Aucune unité légale correspondant à l'identifiant: ";
    private static final String UNITE_NOT_FOUND_SIREN = "Aucune unité légale correspondant au SIREN: ";

    private final UniteLegaleService uniteLegaleService;
    private final EtablissementService etablissementService;

    public UniteLegaleController(UniteLegaleService uniteLegaleService,
            EtablissementService etablissementService) {
        this.uniteLegaleService = uniteLegaleService;
        this.etablissementService = etablissementService;
    }

    /**
     * Récupère toutes les unités légales
     * GET /api/unites-legales
     *
     * @return ResponseEntity avec la liste de toutes les unités légales (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<UniteLegale>> getAllUnitesList() {
        List<UniteLegale> unites = uniteLegaleService.findAll();
        return ResponseEntity.ok(unites);
    }

    /**
     * Récupère une unité légale par son identifiant
     * GET /api/unites-legales/{id}
     *
     * @param id l'identifiant de l'unité légale
     * @return ResponseEntity avec l'unité légale (200 OK) ou 404 NOT FOUND avec
     *         message d'erreur
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUniteLegaleById(@PathVariable Long id) {
        Optional<UniteLegale> unite = uniteLegaleService.findById(id);
        if (unite.isPresent()) {
            return ResponseEntity.ok(unite.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, UNITE_NOT_FOUND + id));
    }

    /**
     * Récupère une unité légale par son SIREN
     * GET /api/unites-legales/siren/{siren}
     *
     * @param siren le numéro SIREN de l'unité légale
     * @return ResponseEntity avec l'unité légale (200 OK) ou 404 NOT FOUND avec
     *         message d'erreur
     */
    @GetMapping("/siren/{siren}")
    public ResponseEntity<Object> getUniteLegaleBySiren(@PathVariable String siren) {

        Optional<UniteLegale> unite = uniteLegaleService.findBySiren(siren);
        if (unite.isPresent()) {
            return ResponseEntity.ok(unite.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, UNITE_NOT_FOUND_SIREN + siren));
    }

    /**
     * Crée une nouvelle unité légale
     * POST /api/unites-legales
     *
     * @param uniteLegale l'unité légale à créer
     * @return ResponseEntity avec l'unité légale créée (201 CREATED)
     */
    @PostMapping
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<UniteLegale> createUniteLegale(@Valid @RequestBody UniteLegale uniteLegale) {
        UniteLegale created = uniteLegaleService.save(uniteLegale);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Met à jour une unité légale existante
     * PUT /api/unites-legales/{id}
     *
     * @param id          l'identifiant de l'unité légale à mettre à jour
     * @param uniteLegale les données mises à jour
     * @return ResponseEntity avec l'unité légale mise à jour (200 OK) ou 404 NOT
     *         FOUND avec message d'erreur
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<Object> updateUniteLegale(@PathVariable Long id,
            @Valid @RequestBody UniteLegale uniteLegale) {
        Optional<UniteLegale> existing = uniteLegaleService.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, UNITE_NOT_FOUND + id));
        }

        UniteLegale unite = existing.get();
        unite.setSiren(uniteLegale.getSiren());
        unite.setDenomination(uniteLegale.getDenomination());
        unite.setCategorieJuridique(uniteLegale.getCategorieJuridique());

        UniteLegale updated = uniteLegaleService.save(unite);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime une unité légale
     * DELETE /api/unites-legales/{id}
     *
     * @param id l'identifiant de l'unité légale à supprimer
     * @return ResponseEntity vide (204 NO CONTENT) ou 404 NOT FOUND avec message
     *         d'erreur
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<Object> deleteUniteLegale(@PathVariable Long id) {
        Optional<UniteLegale> existing = uniteLegaleService.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, UNITE_NOT_FOUND + id));
        }

        uniteLegaleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère les établissements d'une unité légale
     * GET /api/unites-legales/{uniteLegaleId}/etablissements
     *
     * @param uniteLegaleId l'identifiant de l'unité légale
     * @return ResponseEntity avec la liste des établissements (200 OK) ou 404 NOT
     *         FOUND si l'unité légale n'existe pas
     */
    @GetMapping("/{uniteLegaleId}/etablissements")
    public ResponseEntity<Object> getEtablissementsByUniteLegale(@PathVariable Long uniteLegaleId) {
        Optional<UniteLegale> uniteLegale = uniteLegaleService.findById(uniteLegaleId);

        if (uniteLegale.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, UNITE_NOT_FOUND + uniteLegaleId));
        }

        List<Etablissement> etablissements = etablissementService.findByUniteLegale(uniteLegale.get());
        return ResponseEntity.ok(etablissements);
    }

    /**
     * Crée un nouvel établissement pour une unité légale
     * POST /api/unites-legales/{uniteLegaleId}/etablissements
     *
     * @param uniteLegaleId      l'identifiant de l'unité légale
     * @param etablissementInput les données de l'établissement à créer
     * @return ResponseEntity avec l'établissement créé (201 CREATED) ou 404 NOT
     *         FOUND si l'unité légale n'existe pas
     */
    @PostMapping("/{uniteLegaleId}/etablissements")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<Object> createEtablissementForUniteLegale(
            @PathVariable Long uniteLegaleId,
            @Valid @RequestBody Etablissement etablissementInput) {

        // Récupérer l'unité légale à partir de son ID
        Optional<UniteLegale> uniteLegaleOptional = uniteLegaleService.findById(uniteLegaleId);

        if (uniteLegaleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, UNITE_NOT_FOUND + uniteLegaleId));
        }

        // Créer un nouvel établissement
        Etablissement etablissement = new Etablissement();
        etablissement.setSiret(etablissementInput.getSiret());
        etablissement.setNic(etablissementInput.getNic());
        etablissement.setAdresse(etablissementInput.getAdresse());
        etablissement.setUniteLegale(uniteLegaleOptional.get());

        // Sauvegarder l'établissement
        etablissementService.save(etablissement);
        return ResponseEntity.status(HttpStatus.CREATED).body(etablissement);
    }

    /**
     * Endpoint pour exporter la liste des unités légales au format CSV.
     *
     * @return ResponseEntity contenant le fichier CSV en tant que tableau d'octets,
     *         avec les en-têtes HTTP appropriés pour déclencher le téléchargement.
     */
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportUnitesLegalesToCsv() {
        // 1. Récupération de toutes les unités légales depuis la base de données
        List<UniteLegale> unitesLegales = uniteLegaleService.findAll();

        // 2. Génération du contenu CSV à partir de la liste des unités légales
        // La méthode generateCsv() formate les données au format CSV
        String csv = uniteLegaleService.generateCsv(unitesLegales);

        // 3. Conversion de la chaîne CSV en tableau d'octets (byte array)
        // Utilisation de l'encodage UTF-8 pour garantir la compatibilité avec les
        // caractères spéciaux
        byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);

        // 4. Configuration des en-têtes HTTP pour la réponse
        HttpHeaders headers = new HttpHeaders();

        // 4.1. Définition du type de contenu comme texte brut (pour les fichiers CSV)
        headers.setContentType(MediaType.TEXT_PLAIN);

        // 4.2. Configuration du nom de fichier et du type de contenu pour le
        // téléchargement
        // ContentDisposition.builder("attachment") indique au navigateur de télécharger
        // le fichier
        // plutôt que de l'afficher directement
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("unites-legales.csv") // Nom du fichier qui sera proposé pour le téléchargement
                .build());

        // 5. Retourne une ResponseEntity contenant :
        // - Le tableau d'octets du fichier CSV
        // - Les en-têtes HTTP configurés
        // - Le code de statut HTTP 200 (OK)
        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/stream")
    public ResponseEntity<StreamingResponseBody> streamUnitesLegales() {
        StreamingResponseBody responseBody = outputStream -> {
            try (Stream<UniteLegale> stream = uniteLegaleService.streamAllUnitesLegales()) {
                // Écrire l'en-tête du CSV
                outputStream.write("ID,SIREN,Dénomination,Catégorie Juridique\n".getBytes(StandardCharsets.UTF_8));

                // Parcourir le flux et écrire chaque ligne
                stream.forEach(unite -> {
                    try {
                        String line = String.format("%d,%s,%s,%s\n",
                                unite.getId(),
                                unite.getSiren(),
                                unite.getDenomination(),
                                unite.getCategorieJuridique());
                        outputStream.write(line.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush(); // Forcer l'envoi des données au client
                    } catch (IOException e) {
                        throw new RuntimeException("Erreur lors de l'écriture du flux", e);
                    }
                });
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBody);
    }
}