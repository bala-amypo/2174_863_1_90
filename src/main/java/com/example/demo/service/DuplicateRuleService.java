package com.example.demo.service;

import com.example.demo.model.DuplicateRule;
import java.util.List;

public interface DuplicateRuleService {
    DuplicateRule createRule(DuplicateRule rule);
    List<DuplicateRule> getAllRules();
    DuplicateRule getRule(Long id);
}