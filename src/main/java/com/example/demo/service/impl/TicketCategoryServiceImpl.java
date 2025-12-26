package com.example.demo.service.impl;

import com.example.demo.model.TicketCategory;
import com.example.demo.repository.TicketCategoryRepository;
import com.example.demo.service.TicketCategoryService;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TicketCategoryServiceImpl implements TicketCategoryService {

    private final TicketCategoryRepository ticketCategoryRepository;

    public TicketCategoryServiceImpl(TicketCategoryRepository ticketCategoryRepository) {
        this.ticketCategoryRepository = ticketCategoryRepository;
    }

    @Override
    public TicketCategory createCategory(TicketCategory category) {
        if (ticketCategoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new DuplicateResourceException("category exists");
        }
        // Removed manual createdAt setting
        return ticketCategoryRepository.save(category);
    }

    @Override
    public List<TicketCategory> getAllCategories() {
        return ticketCategoryRepository.findAll();
    }

    @Override
    public TicketCategory getCategory(Long id) {
        return ticketCategoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
