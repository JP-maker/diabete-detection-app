package com.mediscreen.diabete_detection_app.patient_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Entité représentant un patient dans la base de données.
 * Cette classe est mappée à la table 'patient'.
 */
@Entity
@Table(name = "patient")
@Data // Génère getters, setters, toString, equals, hashCode
@NoArgsConstructor // Génère un constructeur sans arguments
@AllArgsConstructor // Génère un constructeur avec tous les arguments
public class Patient {

    /**
     * Identifiant unique du patient, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Prénom du patient.
     */
    @Column(name = "prenom")
    private String prenom;

    /**
     * Nom de famille du patient.
     */
    @Column(name = "nom")
    private String nom;

    /**
     * Date de naissance du patient.
     */
    @Column(name = "date_de_naissance")
    private LocalDate dateDeNaissance;

    /**
     * Genre du patient ('M' pour Masculin, 'F' pour Féminin).
     */
    @Column(name = "genre")
    private String genre;

    /**
     * Adresse postale du patient (optionnelle).
     */
    @Column(name = "adresse_postale")
    private String adressePostale;

    /**
     * Numéro de téléphone du patient (optionnel).
     */
    @Column(name = "numero_de_telephone")
    private String numeroDeTelephone;
}