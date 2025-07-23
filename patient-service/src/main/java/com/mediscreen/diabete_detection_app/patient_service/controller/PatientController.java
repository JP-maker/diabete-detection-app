package com.mediscreen.diabete_detection_app.patient_service.controller;


import com.mediscreen.diabete_detection_app.patient_service.dto.PatientDTO;
import com.mediscreen.diabete_detection_app.patient_service.exception.PatientNotFoundException;
import com.mediscreen.diabete_detection_app.patient_service.model.Patient;
import com.mediscreen.diabete_detection_app.patient_service.repository.PatientRepository;


import com.mediscreen.diabete_detection_app.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les opérations CRUD sur les patients.
 * Ce contrôleur expose les endpoints de l'API patient. Il communique avec la couche
 * service pour la logique métier et utilise des DTO (Data Transfer Objects) pour
 * les échanges de données avec les clients, assurant une séparation claire des préoccupations.
 */
@Slf4j
@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientService patientService;

    /**
     * Récupère la liste de tous les patients.
     *
     * @return une liste de tous les patients et un statut HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        log.info("Récupération de la liste de tous les patients");
        List<PatientDTO> patients = patientService.getAllPatients();
        log.info("Nombre de patients trouvés : {}", patients.size());
        return ResponseEntity.ok(patients);

    }

    /**
     * Récupère un patient spécifique par son identifiant unique.
     *
     * @param id L'identifiant du patient à récupérer.
     * @return Une {@link ResponseEntity} contenant le {@link PatientDTO} trouvé et un statut HTTP 200 (OK).
     * @throws PatientNotFoundException si aucun patient ne correspond à l'ID, résultant en un statut 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Integer id) {
        log.info("Requête reçue pour récupérer le patient avec l'ID : {}", id);
        PatientDTO patient = patientService.getPatientById(id);
        log.info("Patient avec l'ID {} trouvé avec succès.", id);
        return ResponseEntity.ok(patient);
    }

    /**
     * Crée un nouveau patient.
     * Les données du patient sont validées avant la création.
     *
     * @param patientDTO Le DTO contenant les informations du patient à créer. Doit être valide.
     * @return Une {@link ResponseEntity} contenant le {@link PatientDTO} créé (avec son nouvel ID) et un statut HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<PatientDTO> addPatient(@Valid @RequestBody PatientDTO patientDTO) {
        log.info("Requête reçue pour créer un nouveau patient pour : {} {}", patientDTO.getPrenom(), patientDTO.getNom());
        PatientDTO savedPatient = patientService.addPatient(patientDTO);
        log.info("Patient créé avec succès avec l'ID : {}", savedPatient.getId());
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    /**
     * Met à jour les informations d'un patient existant.
     * Les nouvelles données sont validées avant la mise à jour.
     *
     * @param id L'identifiant du patient à mettre à jour.
     * @param patientDTO Le DTO contenant les nouvelles informations pour le patient.
     * @return Une {@link ResponseEntity} contenant le {@link PatientDTO} mis à jour et un statut HTTP 200 (OK).
     * @throws PatientNotFoundException si aucun patient ne correspond à l'ID, résultant en un statut 404.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable("id") Integer id, @Valid @RequestBody PatientDTO patientDTO) {
        log.info("Requête reçue pour mettre à jour le patient avec l'ID : {}", id);
        PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);
        log.info("Patient avec l'ID {} mis à jour avec succès.", id);
        return ResponseEntity.ok(updatedPatient);
    }

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id L'identifiant du patient à supprimer.
     * @return Une {@link ResponseEntity} vide avec un statut HTTP 204 (No Content) pour indiquer le succès.
     * @throws PatientNotFoundException si aucun patient ne correspond à l'ID, résultant en un statut 404.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable("id") Integer id) {
        log.info("Requête reçue pour supprimer le patient avec l'ID : {}", id);
        patientService.deletePatient(id);
        log.info("Patient avec l'ID {} supprimé avec succès.", id);
        return ResponseEntity.noContent().build();
    }
}
