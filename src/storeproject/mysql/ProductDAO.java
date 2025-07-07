package storeproject.mysql;

import storeproject.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends DataBaseService {

    public ProductDAO() {
        super();
        createTable();
    }


    protected void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "description VARCHAR(100) NOT NULL, " +
                "barcode VARCHAR(50) UNIQUE, " +
                "unit_of_measurement VARCHAR(20) NOT NULL, " +
                "last_purchase_date DATE, " +
                "sale_price DECIMAL(10,2) NOT NULL, " +
                "stock_quantity DECIMAL(10,3) NOT NULL)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            insertInitialProducts();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create products table", e);
        }
    }

    private void insertInitialProducts() {
        // Sample products for testing
        Product[] initialProducts = {
                new Product("Rice 5kg", "789123450001", "unit", 22.90, 50),
                new Product("Beans 1kg", "789123450002", "unit", 8.90, 40),
                new Product("Soybean Oil 900ml", "789123450003", "unit", 7.50, 30),
                new Product("Sugar 1kg", "789123450004", "unit", 4.20, 60),
                new Product("Coffee 500g", "789123450005", "unit", 12.90, 25)
        };

        String sql = "INSERT IGNORE INTO products " +
                "(description, barcode, unit_of_measurement, sale_price, stock_quantity) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Product product : initialProducts) {
                stmt.setString(1, product.getDescription());
                stmt.setString(2, product.getBarCode());
                stmt.setString(3, product.getUnitOfMeasurement());
                stmt.setDouble(4, product.getSalePrice());
                stmt.setDouble(5, product.getStockQuantity());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert initial products", e);
        }
    }

    // CRUD Operations

    public boolean createProduct(Product product) {
        String sql = "INSERT INTO products " +
                "(description, barcode, unit_of_measurement, last_purchase_date, sale_price, stock_quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setProductParameters(stmt, product);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        product.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create product", e);
        }
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get product by ID", e);
        }
        return null;
    }

    public Product getProductByBarcode(String barcode) {
        String sql = "SELECT * FROM products WHERE barcode = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, barcode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get product by barcode", e);
        }
        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(resultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all products", e);
        }
        return products;
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET " +
                "description = ?, barcode = ?, unit_of_measurement = ?, " +
                "last_purchase_date = ?, sale_price = ?, stock_quantity = ? " +
                "WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setProductParameters(stmt, product);
            stmt.setInt(7, product.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    // Helper methods

    private void setProductParameters(PreparedStatement stmt, Product product) throws SQLException {
        stmt.setString(1, product.getDescription());
        stmt.setString(2, product.getBarCode());
        stmt.setString(3, product.getUnitOfMeasurement());

        if (product.getLastPurchaseDate() != null) {
            stmt.setDate(4, new java.sql.Date(product.getLastPurchaseDate().getTime()));
        } else {
            stmt.setNull(4, Types.DATE);
        }

        stmt.setDouble(5, product.getSalePrice());
        stmt.setDouble(6, product.getStockQuantity());
    }

    private Product resultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setDescription(rs.getString("description"));
        product.setBarCode(rs.getString("barcode"));
        product.setUnitOfMeasurement(rs.getString("unit_of_measurement"));
        product.setLastPurchaseDate(rs.getDate("last_purchase_date"));
        product.setSalePrice(rs.getDouble("sale_price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        return product;
    }
    public boolean updateProductStock(Connection conn, int productId, int newStock) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newStock);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }
}
