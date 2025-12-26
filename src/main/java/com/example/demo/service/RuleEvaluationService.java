package com.example.demo.service;

import java.util.List;

public interface RuleEvaluationService {

    boolean exactMatch(String text, String rule);

    boolean keywordMatch(String text, List<String> keywords);

    boolean keywordMatchWithThreshold(
            String text,
            List<String> keywords,
            double threshold);
}
