package com.ampwind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class SimbirParser {
    private static final Logger logger = LogManager.getLogger(SimbirParser.class);

    public void run(String url, boolean toDb) {
        String filePath = HtmlLoader.load(url);
        List<String> words = HtmlParser.parse(filePath);
        Map<String, Integer> stats = WordStatsCounter.count(words);
        if (!toDb) {
            WordStatsFileSaver.save(stats, url);
        } else {
            WordStatsDbSaver.save(stats, url);
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            new SimbirParser().run(args[0], false);
        } else if (args.length == 2 && args[1].equals("--db")) {
            new SimbirParser().run(args[0], true);
        } else {
            logger.fatal("Программа не запущена: некорректные аргументы.");
            System.out.println("Как использовать программу:\n" +
                    "    SimbirSoft https://yandex.ru      - для сохранения результатов в файл\n" +
                    "    SimbirSoft https://yandex.ru --db - для сохранения результатов в БД");
            System.exit(1);
        }
    }
}
