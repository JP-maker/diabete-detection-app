package com.mediscreen.diabete_detection_app.patient_service.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password"; // Le mot de passe que vous voulez utiliser
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("Mot de passe brut : " + rawPassword);
        System.out.println("Mot de passe encodé [À COPIER] : " + encodedPassword);
    }
}
