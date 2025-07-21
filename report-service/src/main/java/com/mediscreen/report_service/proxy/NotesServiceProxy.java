package com.mediscreen.report_service.proxy;

import com.mediscreen.report_service.dto.NoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "notes-service", url = "${notes-service.url}")
public interface NotesServiceProxy {
    @GetMapping("/notes/patient/{patientId}")
    List<NoteDTO> getNotesForPatient(@PathVariable("patientId") Integer patientId);
}