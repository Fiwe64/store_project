package storeproject.mysql;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public abstract class DataBaseService {
    private static final Dotenv dotenv = Dotenv.configure().load();
    protected static final String DB_PORT = dotenv.get("DB_PORT", "3306");
    protected static final String DB_NAME = dotenv.get("DB_NAME", "store");
    protected static final String USER = dotenv.get("DB_USER", "root");
    protected static final String PASSWORD = dotenv.get("DB_PASSWORD", "");
    protected static final String BASE_URL = "jdbc:mysql://localhost:" + DB_PORT;
    protected static final String URL = BASE_URL + "/" + DB_NAME;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado", e);
        }
    }

    public DataBaseService() {
        createDatabase();
    }

    private void createDatabase() {
        try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao criar banco de dados", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}