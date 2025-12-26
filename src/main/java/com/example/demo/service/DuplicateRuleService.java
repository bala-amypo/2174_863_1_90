package com.example.demo.service;

import com.example.demo.model.DuplicateRule;
import java.util.List;

public interface DuplicateRuleService {
    DuplicateRule createRule(DuplicateRule rule);
    DuplicateRule getRule(Long id);
    List<DuplicateRule> getAllRules();
}
