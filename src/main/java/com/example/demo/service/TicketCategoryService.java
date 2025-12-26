package com.example.demo.service;

import com.example.demo.model.TicketCategory;

public interface TicketCategoryService {
    TicketCategory createCategory(TicketCategory category);
    TicketCategory getCategory(Long id);
}
