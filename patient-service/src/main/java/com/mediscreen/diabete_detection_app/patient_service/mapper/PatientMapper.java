package com.mediscreen.diabete_detection_app.patient_service.mapper;


import com.mediscreen.diabete_detection_app.patient_service.dto.PatientDto;
import com.mediscreen.diabete_detection_app.patient_service.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Interface de mapping entre l'entité Patient et son DTO, gérée par MapStruct.
 * L'implémentation de cette interface sera générée automatiquement à la compilation.
 */
@Mapper(componentModel = "spring") // componentModel = "spring" demande à MapStruct de générer un bean Spring
public interface PatientMapper {

    // Crée une instance accessible statiquement, bien que non nécessaire avec componentModel="spring"
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    /**
     * Convertit une entité Patient en un PatientDTO.
     * MapStruct va automatiquement mapper les champs qui ont le même nom.
     *
     * @param patient L'entité source.
     * @return Le DTO résultant.
     */
    PatientDto toDto(Patient patient);

    /**
     * Convertit un PatientDTO en une entité Patient.
     *
     * @param patientDto Le DTO source.
     * @return L'entité résultante.
     */
    Patient toEntity(PatientDto patientDto);
}