/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeproject.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Aluno
 */
public abstract class DataBaseService {
    protected static final String BASE_URL = "jdbc:mysql://localhost:3306";
    protected static final String URL = "jdbc:mysql://localhost:3306/store";
    protected static final String USER = "root";
    protected static final String PASSWORD = "root";

    public DataBaseService() {
        this.createDatabase();
    }
    
    private void createDatabase() {
        String sql = "CREATE DATABASE IF NOT EXISTS store";
        try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Database creation failed. Check MySQL server.", e);
        }
    }
}
