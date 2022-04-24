package au.edu.sydney.soft3202.task3.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

public class SQLDataBase {
    private String dbName;
    // Should be something like "jdbc:sqlite:"
    private String dbURL;

    public void initDB() {
        dbName = "sqlite_db.db";
        File db = new File(dbName);
        dbURL = "Chess:sqlite";
        if (db.exists()) {
            System.err.println("Invalid, already exists");
            return; }
        try (Connection ignored = DriverManager.getConnection(dbURL)) {
            System.out.println("Database has been created.");}
        catch (SQLException e) {
            System.err.println(e);
            System.exit(-1);
        } }
}
