package au.edu.sydney.soft3202.task3.model;

import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.sql.*;

public class SQLDataBase {
    private static final String dbName = "test.db";
    private static final String dbURL = "jdbc:sqlite:" + dbName;
    public static int db_id = 1;

    public static void createDB() {
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            System.out.println("Database already created");
            return;
        }
        try (Connection ignored = DriverManager.getConnection(dbURL)) {
            // If we get here that means no exception raised from getConnection - meaning it worked
            System.out.println("A new database has been created.");
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
        String createUsersTableSQL =
                """
                        CREATE TABLE IF NOT EXISTS users (
                            id integer PRIMARY KEY,
                            users text NOT NULL,
                            game_state text NOT NULL
                        );
                        """;


        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement statement = conn.createStatement()) {
            statement.execute(createUsersTableSQL);
            System.out.println("Created tables");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void addStartingData() {
        String addUsersDataSQL =
                """
                        INSERT INTO users(id, users, game_state) VALUES
                            (1, "noname", "sdfsdfsdfsdf")
                           
                        """;
        db_id ++;


        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement statement = conn.createStatement()) {
            statement.execute(addUsersDataSQL);


            System.out.println("Added starting data");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void addDataFromQuestionableSource(String users, String game_state) {
        String addSingleStudentWithParametersSQL =
                """
                        INSERT INTO users(id, users, game_state) VALUES
                            (?, ?, ?)
                        """;

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement preparedStatement = conn.prepareStatement(addSingleStudentWithParametersSQL)) {
            preparedStatement.setInt(1, db_id);
            preparedStatement.setString(2, users);
            preparedStatement.setString(3, game_state);
            preparedStatement.executeUpdate();
            db_id++;

            System.out.println("Added questionable data");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}

//    public static void queryDataSimple(double minWAM, double maxWAM) {
//        String studentRangeSQL =
//                """
//                SELECT first_name, last_name
//                FROM students WHERE wam > ? AND wam < ?
//                """;
//
//        try (Connection conn = DriverManager.getConnection(dbURL);
//             PreparedStatement preparedStatement = conn.prepareStatement(studentRangeSQL)) {
//            preparedStatement.setDouble(1, minWAM);
//            preparedStatement.setDouble(2, maxWAM);
//            ResultSet results = preparedStatement.executeQuery();
//
//            while (results.next()) {
//                System.out.println(
//                        results.getString("first_name") + " " +
//                                results.getString("last_name"));
//            }
//
//            System.out.println("Finished simple query");
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//            System.exit(-1);
//        }
//    }

//    public static void queryDataWithJoin(String uos) {
//        String enrolmentSQL =
//                """
//                SELECT first_name, last_name
//                FROM students AS s
//                INNER JOIN student_units AS su ON s.id = su.student_id
//                INNER JOIN units as u ON su.unit_id = u.id
//                WHERE u.code = ?
//                """;
//
//        try (Connection conn = DriverManager.getConnection(dbURL);
//             PreparedStatement preparedStatement = conn.prepareStatement(enrolmentSQL)) {
//            preparedStatement.setString(1, uos);
//            ResultSet results = preparedStatement.executeQuery();
//
//            while (results.next()) {
//                System.out.println(
//                        results.getString("first_name") + " " +
//                                results.getString("last_name"));
//            }
//
//            System.out.println("Finished join query");
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//            System.exit(-1);
//        }
//    }

//    public static void main(String[] args) {
//        removeDB();
//        createDB();
//        setupDB();
//        addStartingData();
//        addDataFromQuestionableSource("2", "none", "1,2,3,4");
//        //queryDataSimple(65.0, 75.0);
//        //queryDataWithJoin("SOFT3202");
//    }
//}
