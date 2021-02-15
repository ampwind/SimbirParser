package com.ampwind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class WordStatsFileSaver {
    private static final Logger logger = LogManager.getLogger(WordStatsFileSaver.class);
    private static final String DIR = "results/";

    public static void save(Map<String, Integer> stats, String url) {
        File dir = new File(DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File outputFile = new File(DIR + url.hashCode() + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String word : stats.keySet()) {
                writer.write(word + " - " + stats.get(word) + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            logger.fatal("Ошибка ввода-вывода при сохранении результатов в файл", e);
            System.exit(1);
        }
    }
}
