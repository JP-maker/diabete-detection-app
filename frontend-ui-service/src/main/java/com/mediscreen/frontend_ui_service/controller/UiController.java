package com.mediscreen.frontend_ui_service.controller;


import com.mediscreen.frontend_ui_service.dto.NoteDTO;
import com.mediscreen.frontend_ui_service.dto.PatientDto;
import com.mediscreen.frontend_ui_service.proxy.NotesServiceProxy;
import com.mediscreen.frontend_ui_service.proxy.PatientServiceProxy;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import javax.swing.*;
import java.util.List;

/**
 * Contrôleur pour gérer les requêtes de l'interface utilisateur.
 * Notez l'annotation @Controller, et non @RestController.
 * Cela indique à Spring que les méthodes retournent des noms de vues, pas du JSON.
 */
@Controller
@Slf4j
public class UiController {

    @Autowired
    private PatientServiceProxy patientServiceProxy;

    @Autowired
    private NotesServiceProxy notesServiceProxy;

    /**
     * Gère les requêtes vers la page d'accueil de la gestion des patients.
     *
     * @param model L'objet Model est utilisé pour passer des données à la vue Thymeleaf.
     * @return Le nom du fichier HTML (template) à afficher.
     */
    @GetMapping("/patients")
    public String showPatientListPage(Model model) {
        log.info("Requête reçue pour afficher la page de la liste des patients.");
        // 1. Appeler le proxy pour obtenir la liste des patients
        var patients = patientServiceProxy.getAllPatients();

        // 2. Ajouter cette liste au modèle sous le nom "patients"
        // La vue pourra accéder à cette variable.
        model.addAttribute("patients", patients);

        // 3. Retourner le nom du template à afficher.
        // Spring va chercher le fichier "src/main/resources/templates/patient/list.html"
        return "patient/list";
    }

    /**
     * Affiche la page de détail d'un patient spécifique, y compris ses notes.
     * Cette méthode est appelée lorsque l'utilisateur accède à l'URL /patients/{id}.
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/patients/{id}")
    public String showPatientDetailPage(@PathVariable("id") Integer id, Model model) {
        log.info("Requête pour afficher les détails du patient {}", id);
        PatientDto patient = patientServiceProxy.getPatientById(id);

        // AJOUT : Récupérer les notes du patient
        List<NoteDTO> notes = notesServiceProxy.getNotesByPatientId(id);

        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes); // Ajouter les notes au modèle
        model.addAttribute("newNote", new NoteDTO()); // Ajouter un DTO vide pour le formulaire d'ajout
        return "patient/detail";
    }

    /**
     * Affiche le formulaire de création pour un nouveau patient.
     *
     * @param model Le modèle pour passer un objet patient vide à la vue.
     * @return Le nom de la vue du formulaire de création.
     */
    @GetMapping("/patients/add")
    public String showAddForm(Model model) {
        log.info("Requête reçue pour afficher le formulaire de création de patient.");
        // On passe un objet DTO vide pour que le formulaire puisse s'y lier.
        model.addAttribute("patient", new PatientDto());
        return "patient/add"; // Pointeur vers la nouvelle vue add.html
    }

    /**
     * Traite la soumission du formulaire de création d'un patient.
     *
     * @param patient Le DTO du patient rempli avec les données du formulaire.
     * @param result Contient les résultats de la validation.
     * @param model Le modèle pour renvoyer des données à la vue en cas d'erreur.
     * @return Une redirection vers la liste des patients si la création réussit,
     *         ou retourne à la page du formulaire en cas d'erreur de validation.
     */
    @PostMapping("/patients/save")
    public String processAddPatient(@Valid @ModelAttribute("patient") PatientDto patient, BindingResult result, Model model) {
        log.info("Requête reçue pour sauvegarder un nouveau patient.");

        if (result.hasErrors()) {
            log.error("Erreurs de validation trouvées : {}", result.getAllErrors());
            // Si des erreurs de validation sont présentes, on retourne à la page du formulaire
            // pour les afficher. Thymeleaf se chargera d'afficher les messages d'erreur.
            return "patient/add";
        }

        patientServiceProxy.addPatient(patient);
        log.info("Patient {} {} créé avec succès.", patient.getPrenom(), patient.getNom());

        // On redirige vers la liste des patients pour voir le nouveau patient ajouté.
        return "redirect:/patients";
    }

    /**
     * Affiche le formulaire d'édition pour un patient donné.
     * Récupère les données du patient et les passe à la vue pour pré-remplir le formulaire.
     *
     * @param id L'identifiant du patient à modifier.
     * @param model Le modèle pour passer le patient à la vue.
     * @return Le nom de la vue du formulaire d'édition.
     */
    @GetMapping("/patients/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        log.info("Requête reçue pour afficher le formulaire d'édition du patient {}", id);
        PatientDto patient = patientServiceProxy.getPatientById(id);
        model.addAttribute("patient", patient);
        return "patient/edit"; // Pointeur vers la nouvelle vue edit.html
    }

    /**
     * Traite la soumission du formulaire de mise à jour d'un patient.
     *
     * @param id L'identifiant du patient mis à jour.
     * @param patient L'objet patient contenant les données du formulaire,
     *                automatiquement lié par Spring grâce à @ModelAttribute.
     * @return Une redirection vers la page de détail du patient mis à jour.
     */
    @PostMapping("/patients/update/{id}")
    public String processUpdatePatient(@PathVariable("id") Integer id,
                                       @ModelAttribute("patient") PatientDto patient,
                                       BindingResult result, Model model) {
        log.info("Requête reçue pour sauvegarder les modifications du patient {}", id);

        if (result.hasErrors()) {
            log.error("Erreurs de validation trouvées pour la mise à jour : {}", result.getAllErrors());
            // Il faut s'assurer que l'ID est toujours dans l'objet pour que les liens du formulaire fonctionnent
            patient.setId(id);
            // On retourne à la vue d'édition pour afficher les erreurs.
            return "patient/edit";
        }

        patientServiceProxy.updatePatient(id, patient);
        log.info("Validation réussie. Patient {} mis à jour.", id);
        return "redirect:/patients/" + id;
    }



    /**
     * Traite la soumission du formulaire pour ajouter une nouvelle note à un patient.
     * @param id L'identifiant du patient auquel la note est ajoutée.
     * @param newNote L'objet NoteDTO contenant les données de la nouvelle note.
     * @return Une redirection vers la page de détail du patient après l'ajout de la note.
     */
    @PostMapping("/patients/{id}/notes/add")
    public String processAddNote(@PathVariable("id") Integer id, @ModelAttribute NoteDTO newNote) {
        log.info("Requête pour ajouter une note au patient {}", id);
        newNote.setPatientId(id); // S'assurer que l'ID du patient est bien défini
        notesServiceProxy.addNote(newNote);
        return "redirect:/patients/" + id;
    }
}
