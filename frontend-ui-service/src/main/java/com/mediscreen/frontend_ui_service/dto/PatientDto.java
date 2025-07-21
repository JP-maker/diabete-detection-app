package com.mediscreen.frontend_ui_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) représentant un patient pour l'interface utilisateur.
 * Ses champs correspondent au JSON renvoyé par le patient-service.
 */
@Data
public class PatientDto {
    /**
     * Identifiant unique du patient.
     */
    private Integer id;

    @NotBlank(message = "Le prénom ne peut pas être vide.")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères.")
    private String prenom;

    @NotBlank(message = "Le nom ne peut pas être vide.")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères.")
    private String nom;

    @NotNull(message = "La date de naissance doit être spécifiée.")
    @Past(message = "La date de naissance doit être dans le passé.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDeNaissance;

    @NotBlank(message = "Le genre doit être spécifié ('M' ou 'F').")
    private String genre;
    private String adressePostale;
    private String numeroDeTelephone;
}
