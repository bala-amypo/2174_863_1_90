package com.example.demo.service.impl;

import com.example.demo.model.TicketCategory;
import com.example.demo.repository.TicketCategoryRepository;
import com.example.demo.service.TicketCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketCategoryServiceImpl implements TicketCategoryService {

    private final TicketCategoryRepository categoryRepository;

    public TicketCategoryServiceImpl(TicketCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public TicketCategory createCategory(TicketCategory category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new RuntimeException("Category already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    public TicketCategory getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<TicketCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}
