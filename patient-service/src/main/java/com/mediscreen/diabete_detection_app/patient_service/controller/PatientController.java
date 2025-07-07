package com.mediscreen.diabete_detection_app.patient_service.controller;


import com.mediscreen.diabete_detection_app.patient_service.exception.PatientNotFoundException;
import com.mediscreen.diabete_detection_app.patient_service.model.Patient;
import com.mediscreen.diabete_detection_app.patient_service.repository.PatientRepository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des patients.
 * Expose les endpoints pour les opérations CRUD sur les patients.
 */
@Slf4j
@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    /**
     * Récupère la liste de tous les patients.
     *
     * @return une liste de tous les patients et un statut HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        log.info("Récupération de la liste de tous les patients");
        List<Patient> patients = patientRepository.findAll();
        log.info("Nombre de patients trouvés : {}", patients.size());
        return ResponseEntity.ok(patients);

    }

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id L'identifiant du patient à récupérer.
     * @return le patient trouvé et un statut HTTP 200 OK.
     * @throws PatientNotFoundException si aucun patient avec cet id n'est trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") Integer id) {
        log.info("Récupération du patient avec l'id : {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Patient non trouvé avec l'id : {}", id);
                    return new PatientNotFoundException("Patient non trouvé avec l'id : " + id);
                });
        log.info("Patient trouvé : {}", patient);
        return ResponseEntity.ok(patient);
    }

    /**
     * Ajoute un nouveau patient.
     *
     * @param patient L'objet patient à ajouter.
     * @return le patient créé et un statut HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        log.info("Ajout d'un nouveau patient : {}", patient);
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient ajouté avec succès : {}", savedPatient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    /**
     * Met à jour les informations d'un patient existant.
     *
     * @param id L'identifiant du patient à mettre à jour.
     * @param patientDetails Les nouvelles informations du patient.
     * @return le patient mis à jour et un statut HTTP 200 OK.
     * @throws PatientNotFoundException si aucun patient avec cet id n'est trouvé.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("id") Integer id, @RequestBody Patient patientDetails) {
        log.info("Requête reçue pour mettre à jour le patient avec l'id : {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Patient non trouvé avec l'id : {}", id);
                    return new PatientNotFoundException("Patient non trouvé avec l'id : " + id);
                });

        patient.setPrenom(patientDetails.getPrenom());
        patient.setNom(patientDetails.getNom());
        patient.setDateDeNaissance(patientDetails.getDateDeNaissance());
        patient.setGenre(patientDetails.getGenre());
        patient.setAdressePostale(patientDetails.getAdressePostale());
        patient.setNumeroDeTelephone(patientDetails.getNumeroDeTelephone());

        Patient updatedPatient = patientRepository.save(patient);
        log.info("Patient mis à jour avec succès : {}", updatedPatient);
        return ResponseEntity.ok(updatedPatient);
    }

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id L'identifiant du patient à supprimer.
     * @return Un statut HTTP 204 No Content si la suppression réussit.
     * @throws PatientNotFoundException si aucun patient avec cet id n'est trouvé.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable("id") Integer id) {
        log.info("Requête reçue pour supprimer le patient avec l'id : {}", id);
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient non trouvé avec l'id : " + id);
        }
        patientRepository.deleteById(id);
        log.info("Patient avec l'id {} supprimé avec succès", id);
        return ResponseEntity.noContent().build();
    }
}
