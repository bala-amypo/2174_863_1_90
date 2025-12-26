package com.example.demo.service;

import com.example.demo.util.TextSimilarityUtil;
import java.util.List;

public class RuleEngine {

    public boolean exactMatch(String input, String rule) {
        if (input == null || rule == null) return false;
        return TextSimilarityUtil.similarity(input, rule) == 1.0;
    }

    public boolean keywordMatch(String text, List<String> keywords) {
        if (text == null || keywords == null || keywords.isEmpty()) return false;

        for (String keyword : keywords) {
            if (TextSimilarityUtil.similarity(text, keyword) > 0.0) {
                return true;
            }
        }
        return false;
    }

    public boolean keywordMatchWithThreshold(
            String text,
            List<String> keywords,
            double threshold) {

        if (text == null || keywords == null || keywords.isEmpty()) return false;

        int matched = 0;
        for (String keyword : keywords) {
            if (TextSimilarityUtil.similarity(text, keyword) > 0.0) {
                matched++;
            }
        }

        double score = (double) matched / keywords.size();
        return score >= threshold;
    }
}
