package storeproject.mysql;

import storeproject.model.User;
import storeproject.model.UserRole;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DataBaseService {

    public UserDAO() {
        super(); // Chama o construtor da superclasse (cria o banco se necessário)
        createTable(); // Implementação do método abstrato
        insertInitialUsers(); // Insere os usuários iniciais
    }

    protected void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "cpf VARCHAR(14) UNIQUE NOT NULL, " +
                "password VARCHAR(100) NOT NULL, " +
                "role ENUM('admin','cashier','client') NOT NULL, " +
                "address VARCHAR(255), " +
                "city VARCHAR(50), " +
                "state CHAR(2), " +
                "zip CHAR(8))";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao criar tabela de usuários", e);
        }
    }

    // Método para inserir usuários iniciais
    private void insertInitialUsers() {
        String sql = "INSERT IGNORE INTO user (nome, cpf, password, role, address, city, state, zip) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        User[] initialUsers = {
                new User("Admin Geral", "123.456.789-00", "admin123", UserRole.admin,
                        "Rua Principal, 100", "Manaus", "AM", "69000000"),
                new User("Caixa 01", "987.654.321-00", "caixa123", UserRole.cashier,
                        "Av. Comercial, 200", "Manaus", "AM", "69001000"),
                new User("Caixa 02", "456.789.123-00", "caixa456", UserRole.cashier,
                        "Travessa dos Funcionários, 300", "Manaus", "AM", "69002000"),
                new User("Cliente Genérico", "000.000.000-00", "cliente", UserRole.client,
                        null, null, null, null),
                new User("João Silva", "111.222.333-44", "cliente123", UserRole.client,
                        "Rua A, 123", "Manaus", "AM", "69000001")
        };

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (User user : initialUsers) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getCpf());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getRole().name());
                stmt.setString(5, user.getAddress());
                stmt.setString(6, user.getCity());
                stmt.setString(7, user.getState());
                stmt.setString(8, user.getZip());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao inserir usuários iniciais", e);
        }
    }

    // Autenticação de usuário
    public User authenticate(String cpf, String password) {
        String sql = "SELECT * FROM users WHERE cpf = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return resultSetToUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha na autenticação", e);
        }
        return null;
    }

    // Criação de novo usuário
    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO user (nome, cpf, password, role, address, city, state, zip) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setUserParameters(stmt, user);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao criar usuário", e);
        }
        return false;
    }

    // Buscar usuário por ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM user WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return resultSetToUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar usuário por ID", e);
        }
        return null;
    }

    // Buscar usuário por CPF
    public User getUserByCpf(String cpf) {
        String sql = "SELECT * FROM user WHERE cpf = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return resultSetToUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar usuário por CPF", e);
        }
        return null;
    }

    // Atualizar usuário
    public boolean updateUser(User user) {
        String sql = "UPDATE user SET nome = ?, cpf = ?, password = ?, role = ?, " +
                "address = ?, city = ?, state = ?, zip = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setUserParameters(stmt, user);
            stmt.setInt(9, user.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar usuário", e);
        }
    }

    // Deletar usuário
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao deletar usuário", e);
        }
    }

    // Listar todos os usuários
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(resultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar usuários", e);
        }
        return users;
    }

    // Listar usuários por tipo
    public List<User> getUsersByRole(UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE role = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(resultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar usuários por tipo", e);
        }
        return users;
    }

    // Método auxiliar para converter ResultSet em User
    private User resultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("nome"));
        user.setCpf(rs.getString("cpf"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setAddress(rs.getString("address"));
        user.setCity(rs.getString("city"));
        user.setState(rs.getString("state"));
        user.setZip(rs.getString("zip"));
        return user;
    }

    // Método auxiliar para configurar parâmetros do PreparedStatement
    private void setUserParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getCpf());
        stmt.setString(3, user.getPassword());
        stmt.setString(4, user.getRole().name());
        stmt.setString(5, user.getAddress());
        stmt.setString(6, user.getCity());
        stmt.setString(7, user.getState());
        stmt.setString(8, user.getZip());
    }
}