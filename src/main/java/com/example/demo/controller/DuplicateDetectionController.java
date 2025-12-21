package com.example.demo.controller;

import com.example.demo.model.DuplicateDetectionLog;
import com.example.demo.service.DuplicateDetectionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/detection")
@Tag(name = "Duplicate Detection Controller", description = "Run and view duplicate detection logs")
public class DuplicateDetectionController {

    private final DuplicateDetectionService duplicateDetectionService;

    public DuplicateDetectionController(DuplicateDetectionService duplicateDetectionService) {
        this.duplicateDetectionService = duplicateDetectionService;
    }

    @GetMapping("/run/{ticketId}")
    public ResponseEntity<List<DuplicateDetectionLog>> runDetection(@PathVariable Long ticketId) {
        return ResponseEntity.ok(duplicateDetectionService.detectDuplicates(ticketId));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<DuplicateDetectionLog>> getLogsForTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(duplicateDetectionService.getLogsForTicket(ticketId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DuplicateDetectionLog> getLog(@PathVariable Long id) {
        return ResponseEntity.ok(duplicateDetectionService.getLog(id));
    }
}
