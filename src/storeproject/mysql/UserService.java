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
public class UserService extends DataBaseService {
    public UserService() {
        super();
        System.out.println("Debug 1");
        
        String sql = "CREATE TABLE IF NOT EXISTS User (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255) NOT NULL, " +
                     "cpf_cnpj VARCHAR(14) UNIQUE NOT NULL, " +
                     "password VARCHAR(100) NOT NULL, " +
                     "type ENUM('admin','cashier','client') NOT NULL, " +
                     "address VARCHAR(255), " +
                     "city VARCHAR(50), " +
                     "state CHAR(2), " +
                     "zip CHAR(8))";
        
//        try {
//            conn = DriverManager.getConnection(URL, USER, PASSWORD);
//            Statement stmt = conn.createStatement();
//            stmt.execute(sql);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Database creation failed. Check MySQL server.", e);
        }
    }
}
