package com.example.demo.service.impl;

import com.example.demo.model.DuplicateRule;
import com.example.demo.repository.DuplicateRuleRepository;
import com.example.demo.service.DuplicateRuleService;
import com.example.demo.exception.NotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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
            throw new IllegalArgumentException("exists");
        }
        if (rule.getThreshold() == null || rule.getThreshold() < 0.0 || rule.getThreshold() > 1.0) {
            throw new IllegalArgumentException("Threshold must be between 0.0 and 1.0");
        }
        rule.setCreatedAt(LocalDateTime.now());
        return duplicateRuleRepository.save(rule);
    }

    @Override
    public List<DuplicateRule> getAllRules() {
        return duplicateRuleRepository.findAll();
    }

    @Override
    public DuplicateRule getRule(Long id) {
        return duplicateRuleRepository.findById(id).orElseThrow(() -> new NotFoundException("Rule not found"));
    }
}
