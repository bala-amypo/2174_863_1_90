package com.example.demo.service.impl;

import com.example.demo.model.TicketCategory;
import com.example.demo.repository.TicketCategoryRepository;
import com.example.demo.service.TicketCategoryService;
import com.example.demo.exception.NotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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
            throw new IllegalArgumentException("category exists");
        }
        category.setCreatedAt(LocalDateTime.now());
        return ticketCategoryRepository.save(category);
    }

    @Override
    public List<TicketCategory> getAllCategories() {
        return ticketCategoryRepository.findAll();
    }

    @Override
    public TicketCategory getCategory(Long id) {
        return ticketCategoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }
}
