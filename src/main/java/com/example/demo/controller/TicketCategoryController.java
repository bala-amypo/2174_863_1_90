package com.example.demo.controller;

import com.example.demo.model.TicketCategory;
import com.example.demo.service.TicketCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Ticket Category Controller", description = "Manage ticket categories")
public class TicketCategoryController {

    private final TicketCategoryService ticketCategoryService;

    public TicketCategoryController(TicketCategoryService ticketCategoryService) {
        this.ticketCategoryService = ticketCategoryService;
    }

    @PostMapping("/")
    public ResponseEntity<TicketCategory> createCategory(@RequestBody TicketCategory category) {
        return ResponseEntity.ok(ticketCategoryService.createCategory(category));
    }

    @GetMapping("/")
    public ResponseEntity<List<TicketCategory>> getAllCategories() {
        return ResponseEntity.ok(ticketCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketCategory> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(ticketCategoryService.getCategory(id));
    }
}
