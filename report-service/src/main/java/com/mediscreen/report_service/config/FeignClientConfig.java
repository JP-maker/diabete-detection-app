package com.mediscreen.report_service.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Value("${feign.client.config.default.username}")
    private String username;

    @Value("${feign.client.config.default.password}")
    private String password;

    /**
     * Crée un intercepteur qui ajoute une authentification HTTP Basic à chaque requête Feign.
     * @return un BasicAuthRequestInterceptor configuré.
     */
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(username, password);
    }
}