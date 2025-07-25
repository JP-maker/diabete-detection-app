package com.mediscreen.report_service.service;

import com.mediscreen.report_service.dto.NoteDTO;
import com.mediscreen.report_service.dto.PatientDTO;
import com.mediscreen.report_service.proxy.NotesServiceProxy;
import com.mediscreen.report_service.proxy.PatientServiceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportService {

    private final PatientServiceProxy patientProxy;
    private final NotesServiceProxy notesProxy;

    // Définition des termes déclencheurs
    private static final List<String> TRIGGER_PATTERNS = Arrays.asList(
            "Hémoglobine A1C",
            "Microalbumine",
            "Taille",
            "Poids",
            "Fumeu(r|se)",     // Correspond à "Fumeur" ou "Fumeuse"
            "Anormal",
            "Cholestérol",
            "Vertige(s)?",     // Correspond à "Vertige" ou "Vertiges"
            "Rechute",
            "Réaction",
            "Anticorps"
    );

    // On compile chaque motif une seule fois pour de meilleures performances.
    // On ajoute des "word boundaries" (\b) pour ne matcher que des mots entiers.
    // On rend la recherche insensible à la casse et compatible Unicode.
    private static final List<Pattern> COMPILED_TRIGGERS = TRIGGER_PATTERNS.stream()
            .map(pattern -> Pattern.compile("\\b" + pattern + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))
            .collect(Collectors.toList());

    public ReportService(PatientServiceProxy patientProxy, NotesServiceProxy notesProxy) {
        this.patientProxy = patientProxy;
        this.notesProxy = notesProxy;
    }

    /**
     * Génère un rapport d'évaluation du risque de diabète pour un patient.
     * @param patientId L'ID du patient.
     * @return Une chaîne de caractères décrivant le niveau de risque.
     */
    public String generateDiabetesReport(Integer patientId) {
        log.info("Génération du rapport pour le patient ID : {}", patientId);

        // 1. Récupérer les données
        PatientDTO patient = patientProxy.getPatientById(patientId);
        List<NoteDTO> notes = notesProxy.getNotesForPatient(patientId);

        // 2. Calculer les informations clés
        int age = calculateAge(patient.getDateDeNaissance());
        long triggerCount = countTriggersInNotes(notes);
        String riskLevel = determineRiskLevel(patient, age, triggerCount);

        // 3. Formater le rapport
        String report = String.format("Patient: %s %s (age %d) son risque de diabète: %s",
                patient.getPrenom(), patient.getNom(), age, riskLevel);

        log.info("Rapport généré pour le patient {}: {}", patientId, report);
        return report;
    }

    /**
     * Calcule l'âge du patient à partir de sa date de naissance.
     * @param birthDate La date de naissance du patient.
     * @return L'âge en années.
     */
    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Compte le nombre de déclencheurs uniques présents dans une liste de notes.
     * La recherche est insensible à la casse et gère les variations (pluriel, féminin)
     * grâce à des expressions régulières pré-compilées.
     *
     * @param notes La liste des notes du patient.
     * @return Le nombre de concepts déclencheurs uniques trouvés.
     */
    private long countTriggersInNotes(List<NoteDTO> notes) {
        log.info("Comptage des déclencheurs dans les notes du patient");
        if (notes == null || notes.isEmpty()) {
            return 0;
        }

        String allNotesText = notes.stream()
                .map(NoteDTO::getNote) // Récupère le contenu de chaque note
                .collect(Collectors.joining(" ")); // Concatène toutes les notes en une seule chaîne de caractères

        // On compte les occurrences uniques, insensible à la casse
        log.info("Nombre de déclencheurs trouvés dans les notes : {}", COMPILED_TRIGGERS.stream()
                .filter(pattern -> pattern.matcher(allNotesText).find())
                .count());
        return COMPILED_TRIGGERS.stream()
                .filter(pattern -> pattern.matcher(allNotesText).find())
                .count();
    }

    /**
     * Détermine le niveau de risque de diabète en fonction de l'âge et du nombre de déclencheurs.
     * @param patient Le patient pour lequel évaluer le risque.
     * @param age L'âge du patient.
     * @param triggerCount Le nombre de déclencheurs détectés dans les notes.
     * @return Le niveau de risque ("None", "Borderline", "In Danger", "Early onset").
     */
    private String determineRiskLevel(PatientDTO patient, int age, long triggerCount) {
        boolean isMale = "M".equalsIgnoreCase(patient.getGenre());

        if (triggerCount == 0) return "Aucun risque";
        if (age >= 30) {
            // Le dossier du patient contient entre deux et cinq déclencheurs et le patient est âgé de plus de 30 ans
            if (triggerCount >= 2 && triggerCount <= 5) return "Risque limité";
            //  Si le patient a plus de 30 ans, alors il en faudra six ou sept ;
            if (triggerCount == 6 || triggerCount == 7) return "Danger";
            //  Si le patient a plus de 30 ans, alors il en faudra huit ou plus
            if (triggerCount >= 8) return "Apparition précoce";
        } else { // moins de 30 ans
            if (isMale) {
                // Si le patient est un homme de moins de 30 ans, alors trois termes déclencheurs doivent être présents
                if (triggerCount == 3) return "Danger";
                //Si le patient est un homme de moins de 30 ans, alors au moins cinq termes déclencheurs
                if (triggerCount >= 5) return "Apparition précoce";
            } else {
                // Si le patient est une femme et a moins de 30 ans, il faudra quatre termes déclencheurs
                if (triggerCount == 4) return "Danger";
                //  Si le patient est une femme et a moins de 30 ans, il faudra au moins sept termes déclencheurs
                if (triggerCount >= 7) return "Apparition précoce";
            }
        }

        // Cas par défaut si aucune règle "élevée" ne matche (ex: 1 trigger, ou 2 triggers pour < 30 ans)
        return "Aucun risque";
    }
}