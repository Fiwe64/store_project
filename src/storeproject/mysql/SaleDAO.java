package storeproject.mysql;

import storeproject.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SaleDAO extends DataBaseService {

    private ProductDAO productDAO;

    public SaleDAO() {
        super();
        this.productDAO = new ProductDAO();
        createTable();
    }

    protected void createTable() {
        createSalesTable();
        createSaleItemsTable();
    }

    private void createSalesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS sales (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "sale_date DATETIME NOT NULL, " +
                "customer_id INT, " +
                "payment_method ENUM('CASH','PIX','CREDIT_CARD','DEBIT_CARD') NOT NULL, " +
                "total_amount DECIMAL(10,2) NOT NULL)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create sales table", e);
        }
    }

    private void createSaleItemsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS sale_items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "sale_id INT NOT NULL, " +
                "product_id INT NOT NULL, " +
                "quantity INT NOT NULL, " +
                "unit_price DECIMAL(10,2) NOT NULL, " +
                "FOREIGN KEY (sale_id) REFERENCES sales(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id))";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create sale items table", e);
        }
    }

    // Register a sale with transaction management
    public boolean registerSale(Sale sale) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert sale
            int saleId = insertSale(conn, sale);
            if (saleId <= 0) {
                conn.rollback();
                return false;
            }
            sale.setId(saleId);

            // 2. Insert sale items and update stock
            for (SaleItem item : sale.getItems()) {
                if (!insertSaleItem(conn, saleId, item)) {
                    conn.rollback();
                    return false;
                }

                // Update product stock
                Product product = item.getProduct();
                int newStock = product.getStockQuantity() - item.getQuantity();
                product.setStockQuantity(newStock);
                if (!productDAO.updateProductStock(conn, product.getId(), newStock)) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Failed to rollback transaction", ex);
            }
            throw new RuntimeException("Failed to register sale", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                // Log error
            }
        }
    }

    private int insertSale(Connection conn, Sale sale) throws SQLException {
        String sql = "INSERT INTO sales (sale_date, customer_id, payment_method, total_amount) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, new Timestamp(sale.getSaleDate().getTime()));
            stmt.setInt(2, sale.getCustomerId());
            stmt.setString(3, sale.getPaymentMethod().name());
            stmt.setDouble(4, sale.getTotalAmount());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    private boolean insertSaleItem(Connection conn, int saleId, SaleItem item) throws SQLException {
        String sql = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, saleId);
            stmt.setInt(2, item.getProduct().getId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getUnitPrice());

            return stmt.executeUpdate() > 0;
        }
    }

    // Generate daily sales summary
    public List<SaleSummary> generateDailySummary(Date date) {
        // Convert to SQL date (without time)
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        String sql = "SELECT payment_method, SUM(total_amount) AS total, COUNT(id) AS sales_count " +
                "FROM sales " +
                "WHERE DATE(sale_date) = ? " +
                "GROUP BY payment_method";

        List<SaleSummary> summaries = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, sqlDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMethod method = PaymentMethod.valueOf(
                            rs.getString("payment_method")
                    );
                    double total = rs.getDouble("total");
                    int count = rs.getInt("sales_count");

                    summaries.add(new SaleSummary(date, method, total, count));
                }
            }
            return summaries;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate daily summary", e);
        }
    }

    // Get all sales for a given date
    public List<Sale> getSalesByDate(Date date) {
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        String sql = "SELECT * FROM sales WHERE DATE(sale_date) = ?";

        List<Sale> sales = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, sqlDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sale sale = new Sale(
                            rs.getInt("customer_id"),
                            PaymentMethod.valueOf(rs.getString("payment_method"))
                    );
                    sale.setId(rs.getInt("id"));
                    sale.setSaleDate(new Date(rs.getTimestamp("sale_date").getTime()));
                    sale.setTotalAmount(rs.getDouble("total_amount"));

                    // Load sale items
                    sale.getItems().addAll(getSaleItems(sale.getId()));
                    sales.add(sale);
                }
            }
            return sales;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get sales by date", e);
        }
    }

    private List<SaleItem> getSaleItems(int saleId) {
        String sql = "SELECT si.*, p.description, p.barcode " +
                "FROM sale_items si " +
                "JOIN products p ON si.product_id = p.id " +
                "WHERE si.sale_id = ?";

        List<SaleItem> items = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setDescription(rs.getString("description"));
                    product.setBarCode(rs.getString("barcode"));

                    SaleItem item = new SaleItem(
                            saleId,
                            product,
                            rs.getInt("quantity")
                    );
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    items.add(item);
                }
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get sale items", e);
        }
    }
}