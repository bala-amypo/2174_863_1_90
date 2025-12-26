package com.example.demo.service.impl;

import com.example.demo.service.RuleEngine;
import com.example.demo.service.RuleEvaluationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleEvaluationServiceImpl implements RuleEvaluationService {

    private final RuleEngine ruleEngine = new RuleEngine();

    @Override
    public boolean exactMatch(String text, String rule) {
        return ruleEngine.exactMatch(text, rule);
    }

    @Override
    public boolean keywordMatch(String text, List<String> keywords) {
        return ruleEngine.keywordMatch(text, keywords);
    }

    @Override
    public boolean keywordMatchWithThreshold(
            String text,
            List<String> keywords,
            double threshold) {

        return ruleEngine.keywordMatchWithThreshold(text, keywords, threshold);
    }
}
