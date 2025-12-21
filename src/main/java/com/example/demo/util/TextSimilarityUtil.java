package com.example.demo.util;

public class TextSimilarityUtil {

    private TextSimilarityUtil() {
    }

    public static double similarity(String s1, String s2) {

        if (s1 == null || s2 == null) return 0.0;

        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        String[] words1 = s1.split("\\s+");
        String[] words2 = s2.split("\\s+");

        int common = 0;
        for (String w1 : words1) {
            for (String w2 : words2) {
                if (w1.equals(w2)) {
                    common++;
                }
            }
        }

        int total = Math.max(words1.length, words2.length);
        if (total == 0) return 0.0;

        return (double) common / total;
    }
}
