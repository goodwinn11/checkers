package au.edu.sydney.soft3202.task3.model;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLDataBase {
    private static final String dbName = "checkers.db";
    public static final String dbURL = "jdbc:sqlite:" + dbName;
    public static int savedGameId = 0;
    public static int userNameId = 0;

    public static void createDB() {
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            System.out.println("Database already created");
            return;
        }
        try (Connection ignored = DriverManager.getConnection(dbURL)) {

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void removeDB() {
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            boolean result = dbFile.delete();
            if (!result) {
                System.out.println("Couldn't delete existing db file");
                System.exit(-1);
            } else {
                System.out.println("Removed existing DB file.");
            }
        } else {
            System.out.println("No existing DB file.");
        }
    }

    public static void setupDB() {
        String createUsersTableSQL2 =
                """
                        CREATE TABLE IF NOT EXISTS user_names (
                            id integer PRIMARY KEY,
                            user_name text NOT NULL
                        );
                        """;
        String createUsersTableSQL =
                """
                        CREATE TABLE IF NOT EXISTS saved_games (
                            id integer PRIMARY KEY,
                            name text NOT NULL,
                            game_state text NOT NULL,
                            user_id integer NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES user_names (id) 
                        );
                        
                        """;


        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement statement = conn.createStatement()) {
            statement.execute(createUsersTableSQL);
            statement.execute(createUsersTableSQL2);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }



    public static void addDataSavedGame(String name, String game_state) {
        String addDataToSQL =
                """
                        INSERT INTO saved_games(id, name, game_state, user_id) VALUES
                            (?, ?, ?, ?)
                        """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(addDataToSQL)) {
            preparedStatement.setInt(1, ++savedGameId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, game_state);
            preparedStatement.setInt(4, userNameId);
            preparedStatement.executeUpdate();



        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    public static void rewriteSavedGame(int id, String name, String game_state) {
        String addDataToSQL =
                """
                        UPDATE saved_games SET id = ?, name = ?, game_state = ?, user_id = ? 
                        WHERE id = ?
                        """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(addDataToSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, game_state);
            preparedStatement.setInt(4, userNameId);
            preparedStatement.setInt(5, id);
            preparedStatement.executeUpdate();



        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    public static void addDataUserName(String name) {
        String addDataToSQL =
                """
                        INSERT INTO user_names(id, user_name) VALUES
                            (?, ?)
                        """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(addDataToSQL)) {
            preparedStatement.setInt(1, ++userNameId);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();



        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    public static Map<String, String> getAllSavedGames() {
        Map<String, String> usersList = new HashMap<String, String>();
        String resultString =
                """
                SELECT *
                FROM saved_games
                WHERE user_id = (?)
                """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(resultString)) {
            preparedStatement.setInt(1, userNameId);
            ResultSet results = preparedStatement.executeQuery();
            int i = 0;
            while (results.next()) {
                usersList.put(results.getString("name"),results.getString("game_state"));
            }



        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    return usersList;
    }


}
