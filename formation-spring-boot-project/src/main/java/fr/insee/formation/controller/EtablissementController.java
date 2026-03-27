package fr.insee.formation.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.formation.controller.dto.ErrorResponse;
import fr.insee.formation.model.Etablissement;
import fr.insee.formation.service.EtablissementService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/etablissements")
@PreAuthorize("hasRole('USER')")
public class EtablissementController {

    private static final String ETAB_NOT_FOUND = "Aucun établissement correspondant à l'identifiant: ";
    private static final String ETAB_NOT_FOUND_SIRET = "Aucun établissement correspondant au SIRET: ";

    private final EtablissementService etablissementService;

    public EtablissementController(EtablissementService etablissementService) {
        this.etablissementService = etablissementService;
    }

    /**
     * Récupère tous les établissements
     * GET /api/etablissements
     * 
     * @return ResponseEntity avec la liste de tous les établissements (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<Etablissement>> getAllEtablissements() {
        List<Etablissement> etablissements = etablissementService.findAll();
        return ResponseEntity.ok(etablissements);
    }

    /**
     * Récupère un établissement par son identifiant
     * GET /api/etablissements/{id}
     * 
     * @param id l'identifiant de l'établissement
     * @return ResponseEntity avec l'établissement (200 OK) ou 404 NOT FOUND avec
     *         message d'erreur
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getEtablissementById(@PathVariable Long id) {
        Optional<Etablissement> etablissement = etablissementService.findById(id);
        if (etablissement.isPresent()) {
            return ResponseEntity.ok(etablissement.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ETAB_NOT_FOUND + id));
    }

    /**
     * Récupère un établissement par son SIRET
     * GET /api/etablissements/siret/{siret}
     * 
     * @param siret le numéro SIRET de l'établissement
     * @return ResponseEntity avec l'établissement (200 OK) ou 404 NOT FOUND avec
     *         message d'erreur
     */
    @GetMapping("/siret/{siret}")
    public ResponseEntity<Object> getEtablissementBySiret(@PathVariable String siret) {

        Optional<Etablissement> etablissement = etablissementService.findBySiret(siret);
        if (etablissement.isPresent()) {
            return ResponseEntity.ok(etablissement.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ETAB_NOT_FOUND_SIRET + siret));
    }

    /**
     * Met à jour un établissement existant
     * PUT /api/etablissements/{id}
     * 
     * @param id            l'identifiant de l'établissement à mettre à jour
     * @param etablissement les données mises à jour
     * @return ResponseEntity avec l'établissement mis à jour (200 OK) ou 404 NOT
     *         FOUND avec message d'erreur
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<Object> updateEtablissement(@PathVariable Long id,
            @Valid @RequestBody Etablissement etablissement) {
        Optional<Etablissement> existing = etablissementService.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, ETAB_NOT_FOUND + id));
        }

        Etablissement etab = existing.get();
        etab.setSiret(etablissement.getSiret());
        etab.setNic(etablissement.getNic());
        etab.setAdresse(etablissement.getAdresse());
        etab.setUniteLegale(etablissement.getUniteLegale());

        Etablissement updated = etablissementService.save(etab);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime un établissement
     * DELETE /api/etablissements/{id}
     * 
     * @param id l'identifiant de l'établissement à supprimer
     * @return ResponseEntity vide (204 NO CONTENT) ou 404 NOT FOUND avec message
     *         d'erreur
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<Object> deleteEtablissement(@PathVariable Long id) {
        Optional<Etablissement> existing = etablissementService.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, ETAB_NOT_FOUND + id));
        }

        etablissementService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}