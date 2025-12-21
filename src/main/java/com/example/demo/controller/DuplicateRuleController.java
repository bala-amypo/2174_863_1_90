package com.example.demo.controller;

import com.example.demo.model.DuplicateRule;
import com.example.demo.service.DuplicateRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rules")
@Tag(name = "Duplicate Rule Controller", description = "Manage duplicate detection rules")
public class DuplicateRuleController {

    private final DuplicateRuleService duplicateRuleService;

    public DuplicateRuleController(DuplicateRuleService duplicateRuleService) {
        this.duplicateRuleService = duplicateRuleService;
    }

    @PostMapping("/")
    public ResponseEntity<DuplicateRule> createRule(@RequestBody DuplicateRule rule) {
        return ResponseEntity.ok(duplicateRuleService.createRule(rule));
    }

    @GetMapping("/")
    public ResponseEntity<List<DuplicateRule>> getAllRules() {
        return ResponseEntity.ok(duplicateRuleService.getAllRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DuplicateRule> getRule(@PathVariable Long id) {
        return ResponseEntity.ok(duplicateRuleService.getRule(id));
    }
}
