package com.example.demo.service;

import com.example.demo.model.DuplicateRule;

public interface DuplicateRuleService {
    DuplicateRule createRule(DuplicateRule rule);
    DuplicateRule getRule(Long id);
}
