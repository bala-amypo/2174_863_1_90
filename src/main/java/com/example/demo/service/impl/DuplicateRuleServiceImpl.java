package com.example.demo.service.impl;

import com.example.demo.model.DuplicateRule;
import com.example.demo.repository.DuplicateRuleRepository;
import com.example.demo.service.DuplicateRuleService;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ValidationException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DuplicateRuleServiceImpl implements DuplicateRuleService {

    private final DuplicateRuleRepository duplicateRuleRepository;

    public DuplicateRuleServiceImpl(DuplicateRuleRepository duplicateRuleRepository) {
        this.duplicateRuleRepository = duplicateRuleRepository;
    }

    @Override
    public DuplicateRule createRule(DuplicateRule rule) {
        if (duplicateRuleRepository.findByRuleName(rule.getRuleName()).isPresent()) {
            throw new DuplicateResourceException("exists");
        }
        if (rule.getThreshold() == null || rule.getThreshold() < 0.0 || rule.getThreshold() > 1.0) {
            throw new ValidationException("Threshold must be between 0.0 and 1.0");
        }
        // Removed manual createdAt setting
        return duplicateRuleRepository.save(rule);
    }

    @Override
    public List<DuplicateRule> getAllRules() {
        return duplicateRuleRepository.findAll();
    }

    @Override
    public DuplicateRule getRule(Long id) {
        return duplicateRuleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
    }
}
