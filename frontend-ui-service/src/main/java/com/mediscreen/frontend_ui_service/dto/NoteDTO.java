package com.mediscreen.frontend_ui_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteDTO {
    private String id;
    @NotNull(message = "L'ID du patient est requis.")
    private Integer patientId;
    private LocalDateTime date;
    @NotBlank(message = "Le contenu de la note ne peut pas être vide.")
    private String note;
}
