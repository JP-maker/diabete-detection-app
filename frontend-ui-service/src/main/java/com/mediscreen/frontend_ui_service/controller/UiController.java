package com.mediscreen.frontend_ui_service.controller;


import com.mediscreen.frontend_ui_service.proxy.PatientServiceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
