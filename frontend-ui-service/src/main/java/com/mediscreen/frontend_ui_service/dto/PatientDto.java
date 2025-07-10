package com.mediscreen.frontend_ui_service.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) représentant un patient pour l'interface utilisateur.
 * Ses champs correspondent au JSON renvoyé par le patient-service.
 */
@Data
public class PatientDto {
    private Integer id;
    private String prenom;
    private String nom;
    private LocalDate dateDeNaissance;
    private String genre;
    private String adressePostale;
    private String numeroDeTelephone;
}
