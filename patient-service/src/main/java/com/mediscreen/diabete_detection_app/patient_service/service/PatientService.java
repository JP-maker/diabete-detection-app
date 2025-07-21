package com.mediscreen.diabete_detection_app.patient_service.service;


import com.mediscreen.diabete_detection_app.patient_service.dto.PatientDTO;
import com.mediscreen.diabete_detection_app.patient_service.exception.PatientNotFoundException;
import com.mediscreen.diabete_detection_app.patient_service.mapper.PatientMapper;
import com.mediscreen.diabete_detection_app.patient_service.model.Patient;
import com.mediscreen.diabete_detection_app.patient_service.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientMapper patientMapper;

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDto)
                .collect(Collectors.toList());
    }

    public PatientDTO addPatient(PatientDTO patientDTO) {
        log.info("Création d'un nouveau patient");
        // L'ID est null pour une création, Hibernate/JPA le générera.
        patientDTO.setId(null);
        Patient patientToCreate = patientMapper.toEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patientToCreate);
        log.info("Patient créé avec succès avec l'id : {}", savedPatient.getId());
        return patientMapper.toDto(savedPatient);
    }

    public PatientDTO getPatientById(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient non trouvé avec l'id : " + id));
        return patientMapper.toDto(patient);
    }

    public PatientDTO updatePatient(Integer id, PatientDTO patientDTO) {
        log.info("Mise à jour du patient {}", id);
        // Vérifier si le patient existe
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Impossible de mettre à jour. Patient non trouvé avec l'id : " + id);
        }
        Patient patientToUpdate = patientMapper.toEntity(patientDTO);
        patientToUpdate.setId(id); // Forcer l'ID pour être sûr de faire un update
        Patient updatedPatient = patientRepository.save(patientToUpdate);
        return patientMapper.toDto(updatedPatient);
    }

    public void deletePatient(Integer id) {
        log.info("Suppression du patient {}", id);
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Impossible de supprimer. Patient non trouvé avec l'id : " + id);
        }
        patientRepository.deleteById(id);
    }
}