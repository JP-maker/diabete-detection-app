package com.mediscreen.diabete_detection_app.patient_service.mapper;

import com.mediscreen.diabete_detection_app.patient_service.dto.PatientDTO;
import com.mediscreen.diabete_detection_app.patient_service.model.Patient;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de test unitaire pour le mapper PatientMapper.
 * Utilise MapStruct pour la conversion entre Patient et PatientDTO.
 */
class PatientMapperTest {

    private final PatientMapper mapper = Mappers.getMapper(PatientMapper.class);

    /**
     * Test pour vérifier que le mapper PatientMapper convertit correctement
     * un objet Patient en PatientDTO et vice versa.
     */
    @Test
    void shouldMapPatientToPatientDTO() {
        // GIVEN
        Patient patient = new Patient(1, "Test", "User", LocalDate.of(2000, 1, 1), "M", "Addr", "Tel");

        // WHEN
        PatientDTO dto = mapper.toDto(patient);

        // THEN
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(patient.getId());
        assertThat(dto.getPrenom()).isEqualTo(patient.getPrenom());
        assertThat(dto.getNom()).isEqualTo(patient.getNom());
        assertThat(dto.getDateDeNaissance()).isEqualTo(patient.getDateDeNaissance());
        assertThat(dto.getGenre()).isEqualTo(patient.getGenre());
        assertThat(dto.getAdressePostale()).isEqualTo(patient.getAdressePostale());
        assertThat(dto.getNumeroDeTelephone()).isEqualTo(patient.getNumeroDeTelephone());
    }

    /**
     * Test pour vérifier que le mapper PatientMapper convertit correctement
     * un objet PatientDTO en Patient.
     */
    @Test
    void shouldMapPatientDTOToPatient() {
        // GIVEN
        PatientDTO dto = new PatientDTO();
        dto.setId(2);
        dto.setPrenom("Dto");
        dto.setNom("Test");

        // WHEN
        Patient entity = mapper.toEntity(dto);

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getPrenom()).isEqualTo(dto.getPrenom());
        assertThat(entity.getNom()).isEqualTo(dto.getNom());
    }

    /**
     * Test pour vérifier que le mapper PatientMapper gère correctement les valeurs nulles.
     * Il doit retourner null si l'entrée est null.
     */
    @Test
    void shouldHandleNullsGracefully() {
        assertThat(mapper.toDto(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
    }
}