package com.mediscreen.frontend_ui_service.controller;


import com.mediscreen.frontend_ui_service.dto.PatientDto;
import com.mediscreen.frontend_ui_service.proxy.PatientServiceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import javax.swing.*;

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
     * Gère l'appel pour la vue détail d'un patient.
     * @param model L'objet Model est utilisé pour passer des données à la vue Thymeleaf.
     * @return Le nom du fichier HTML (template) à afficher.
     */
    @GetMapping("/patients/{id}")
    public String showPatientDetailPage(Model model, @PathVariable("id") Integer id) {
        log.info("Requête reçue pour afficher la page de détail d'un patient.");
        // 1. Appeler le proxy pour obtenir les détails du patient
        // Notez que l'ID du patient doit être passé en paramètre de la méthode.
        // Ici, nous n'avons pas encore implémenté la logique pour récupérer l'ID.
        var patient = patientServiceProxy.getPatientById(id);

        // 2. Ajouter le patient au modèle
        model.addAttribute("patient", patient);

        // 3. Retourner le nom du template à afficher.
        return "patient/detail";
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
    public String processUpdatePatient(@PathVariable("id") Integer id, @ModelAttribute("patient") PatientDto patient) {
        log.info("Requête reçue pour sauvegarder les modifications du patient {}", id);
        patient.setId(id); // S'assurer que l'ID est bien dans l'objet patient
        log.info("Mise à jour du patient avec date {}", patient.getDateDeNaissance());
        patientServiceProxy.updatePatient(id, patient);
        // On redirige vers la page de détail pour voir les changements et éviter
        // la resoumission du formulaire si l'utilisateur rafraîchit la page.
        return "redirect:/patients/" + id;
    }
}
