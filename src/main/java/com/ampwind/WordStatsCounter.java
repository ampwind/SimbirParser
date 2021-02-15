package com.ampwind;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WordStatsCounter {
    public static Map<String, Integer> count(List<String> words) {
        Map<String, Integer> stats = new LinkedHashMap<>();

        for (String word : words) {
            if (isCorrectWord(word)) {
                if (stats.containsKey(word)) {
                    stats.put(word, stats.get(word) + 1);
                } else  {
                    stats.put(word, 1);
                }
            }
        }

        return stats;
    }

    private static boolean isCorrectWord(String word) {
        boolean withDelim = false;

        for (int i = 0; i < word.length(); i++) {
            if (Character.isAlphabetic(word.charAt(i))) {
                continue;
            } else if (!withDelim && word.charAt(i) == '-' && (i > 0 && i < word.length() - 1)) {
                withDelim = true;
            } else {
                return false;
            }
        }

        return true;
    }
}
