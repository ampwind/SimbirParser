package com.ampwind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class HtmlParser {
    private static final Logger logger = LogManager.getLogger(HtmlParser.class);
    private static final String SEPS = " .,:;?!()[]{}'\"<>\n\r\t_|$*/\\";

    public static List<String> parse(String filePath) {
        String text = null;
        try {
            text = Jsoup.parse(new File(filePath), "UTF-8").text().toLowerCase();
        } catch (IOException e) {
            logger.fatal("Ошибка ввода-вывода при парсинге файла", e);
            System.exit(1);
        }
        List<String> words = new LinkedList<>();

        StringTokenizer tokenizer = new StringTokenizer(text, SEPS);
        while (tokenizer.hasMoreElements()) {
            words.add(tokenizer.nextToken());
        }

        return words;
    }
}
