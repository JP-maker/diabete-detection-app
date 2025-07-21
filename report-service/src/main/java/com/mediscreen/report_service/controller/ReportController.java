package com.mediscreen.report_service.controller;

import com.mediscreen.report_service.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<String> getDiabetesReport(@PathVariable Integer id) {
        String report = reportService.generateDiabetesReport(id);
        return ResponseEntity.ok(report);
    }
}