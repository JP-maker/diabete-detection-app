package com.mediscreen.diabete_detection_app.patient_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception personnalisée levée lorsqu'un patient n'est pas trouvé.
 * L'annotation @ResponseStatus provoque le retour d'un code HTTP 404 Not Found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String message) {
        super(message);
    }
}
