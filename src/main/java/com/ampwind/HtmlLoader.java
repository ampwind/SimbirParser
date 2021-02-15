package com.ampwind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HtmlLoader {
    private static final Logger logger = LogManager.getLogger(HtmlLoader.class);
    private static final String DIR = "pages/";

    public static String load(String url) {
        File dir = new File(DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }

        HttpURLConnection connection = getConnection(url);
        File outFile = new File(DIR + url.hashCode() + ".html");
        saveToFile(connection, outFile);

        return outFile.getAbsolutePath();
    }

    private static HttpURLConnection getConnection(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            if (connection.getResponseCode() != 200) {
                logger.error("Неверный код ответа на http запрос");
                throw new IOException();
            }
        } catch (MalformedURLException e) {
            logger.error("Некорректный URL:", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error("Ошибка ввода-вывода при попытке установить соедение с URL", e);
            System.exit(1);
        }
        return connection;
    }

    private static void saveToFile(HttpURLConnection connection, File outFile) {
        try (BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile))) {
            int c;
            while ((c = inputStream.read()) != -1) {
                outputStream.write(c);
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            logger.fatal("Файл для записи не найден", e);
            System.exit(1);
        } catch (IOException e) {
            logger.fatal("Ошибка ввода-вывода при попытке установить канал связи с URL", e);
            System.exit(1);
        }
    }
}
