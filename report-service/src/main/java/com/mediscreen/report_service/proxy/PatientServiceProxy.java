package com.mediscreen.report_service.proxy;


import com.mediscreen.report_service.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client Feign pour communiquer avec le microservice patient.
 * 'name' est un nom logique, 'url' pointe vers l'adresse du service.
 */
@FeignClient(name = "patient-service", url = "${patient-service.url}")
public interface PatientServiceProxy {
    @GetMapping("/patients/{id}")
    PatientDTO getPatientById(@PathVariable("id") Integer id);
}
