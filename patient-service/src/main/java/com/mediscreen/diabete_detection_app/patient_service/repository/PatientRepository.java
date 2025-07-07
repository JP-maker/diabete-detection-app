package com.mediscreen.diabete_detection_app.patient_service.repository;

import com.mediscreen.diabete_detection_app.patient_service.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entité Patient.
 * Fournit les méthodes CRUD de base (Create, Read, Update, Delete) pour les patients.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
}
