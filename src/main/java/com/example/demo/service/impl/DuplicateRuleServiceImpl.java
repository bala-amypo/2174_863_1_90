package com.example.demo.service.impl;

import com.example.demo.model.DuplicateRule;
import com.example.demo.repository.DuplicateRuleRepository;
import com.example.demo.service.DuplicateRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DuplicateRuleServiceImpl implements DuplicateRuleService {

    private final DuplicateRuleRepository ruleRepository;

    public DuplicateRuleServiceImpl(DuplicateRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    public DuplicateRule createRule(DuplicateRule rule) {
        if (ruleRepository.findByRuleName(rule.getRuleName()).isPresent()) {
            throw new RuntimeException("Rule already exists");
        }
        return ruleRepository.save(rule);
    }

    @Override
    public DuplicateRule getRule(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
    }

    @Override
    public List<DuplicateRule> getAllRules() {
        return ruleRepository.findAll();
    }
}
