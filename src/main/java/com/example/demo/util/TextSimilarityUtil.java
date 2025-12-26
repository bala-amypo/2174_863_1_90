package com.example.demo.util;

import java.util.*;
import java.util.stream.Collectors;

public class TextSimilarityUtil {

    public static double similarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        if (s1.equals(s2)) return 1.0;
        
        // Simple Jaccard similarity
        Set<String> set1 = Arrays.stream(s1.toLowerCase().split("\\s+"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
        Set<String> set2 = Arrays.stream(s2.toLowerCase().split("\\s+"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        if (set1.isEmpty() && set2.isEmpty()) return 1.0; // Both empty?
        if (set1.isEmpty() || set2.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }
}
