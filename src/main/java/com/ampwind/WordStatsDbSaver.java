package com.ampwind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class WordStatsDbSaver {
    private static final Logger logger = LogManager.getLogger(WordStatsDbSaver.class);
    private static final String DB_NAME = "results.db";

    private static final DbHelper db = new DbHelper();

    public static void save(Map<String, Integer> stats, String url) {
        db.connect();
        db.createTable();
        db.saveStatsToDb(stats, url);
        db.closeDb();
    }

    private static class DbHelper {
        private Connection connection;

        private void connect() {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            } catch (ClassNotFoundException e) {
                logger.fatal("Ошибка при загрузке драйвера БД", e);
                System.exit(1);
            } catch (SQLException e) {
                logger.fatal("Ошибка при подключении к БД", e);
                System.exit(1);
            }
        }

        private void createTable() {
            try {
                Statement statement = connection.createStatement();
                String query = "CREATE TABLE IF NOT EXISTS word_stats " +
                        "(id INTEGER NOT NULL," +
                        "link TEXT NOT NULL," +
                        "word TEXT NOT NULL, " +
                        "count INTEGER NOT NULL," +
                        "PRIMARY KEY(id));";
                statement.execute(query);
            } catch (SQLException e) {
                logger.fatal("Ошибка при создании таблицы в БД", e);
                System.exit(1);
            }
        }

        private void saveStatsToDb(Map<String, Integer> stats, String url) {
            // Если данные для ссылки сохранялись ранее - их необходимо удалить
            if (checkUrl(url)) {
                deleteStat(url);
            }

            String query = "INSERT INTO word_stats (link, word, count) VALUES (?, ?, ?);";
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                for (String word : stats.keySet()) {
                    preparedStatement.setString(1, url);
                    preparedStatement.setString(2, word);
                    preparedStatement.setInt(3, stats.get(word));
                    preparedStatement.execute();
                }
                connection.commit();
            } catch (SQLException e) {
                logger.fatal("Ошибка при сохранении данных в БД", e);
                System.exit(1);
            }
        }

        private boolean checkUrl(String url) {
            String query = "SELECT EXISTS(SELECT id FROM word_stats WHERE link = ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, url);
                ResultSet result = preparedStatement.executeQuery();
                if (result.next())
                    return (result.getInt(1) == 1);
            } catch (SQLException e) {
                logger.error("Ошибка при проверке URL на наличие в БД", e);
            }
            return false;
        }

        private void deleteStat(String url) {
            String query = "DELETE FROM word_stats WHERE link = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, url);
                preparedStatement.execute();
            } catch (SQLException e) {
                logger.error("Ошибка при удалении неактуальных результатов в БД", e);
            }
        }

        private void closeDb() {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Ошибка при закрытии БД", e);
            }
        }
    }
}
