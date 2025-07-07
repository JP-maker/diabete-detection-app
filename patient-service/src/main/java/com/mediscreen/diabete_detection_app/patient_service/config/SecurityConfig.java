package com.mediscreen.diabete_detection_app.patient_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuration de la sécurité pour le microservice.
 * Adopte l'approche moderne à base de composants (SecurityFilterChain)
 * au lieu de l'ancienne classe WebSecurityConfigurerAdapter.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité qui s'applique à toutes les requêtes HTTP.
     * C'est ici que l'on configure la protection des endpoints.
     *
     * @param http L'objet HttpSecurity pour construire la configuration de sécurité.
     * @return une instance de SecurityFilterChain.
     * @throws Exception si une erreur de configuration se produit.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver la protection CSRF car nous exposons une API REST sans état
                // et nous n'utilisons pas de formulaires basés sur des sessions.
                .csrf(csrf -> csrf.disable())

                // Configurer les règles d'autorisation pour les requêtes HTTP.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Exige que toute requête soit authentifiée.
                )

                // Activer l'authentification HTTP Basic avec les paramètres par défaut.
                .httpBasic(withDefaults());

        return http.build();
    }

    /**
     * Définit le bean pour l'encodeur de mot de passe.
     * Utilise BCrypt, qui est une méthode de hachage forte et recommandée.
     * Ce bean sera automatiquement utilisé par Spring Security pour comparer les mots de passe.
     *
     * @return une instance de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
