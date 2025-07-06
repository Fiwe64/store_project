package storeproject.test;

import storeproject.model.Product;
import storeproject.mysql.ProductDAO;

import java.util.List;

public class TestProductTableCreation {
    public static void main(String[] args) {
        // Create DAO instance
        ProductDAO productDAO = new ProductDAO();

// Add new product
        Product newProduct = new Product("Milk 1L", "789123450006", "unit", 5.50, 30);
        productDAO.createProduct(newProduct);

// Get product by barcode
        Product found = productDAO.getProductByBarcode("789123450001");
        System.out.println(found.getDescription()); // Prints "Rice 5kg"

// Update stock
        found.setStockQuantity(45);
        productDAO.updateProduct(found);

// List all products
        List<Product> allProducts = productDAO.getAllProducts();
    }
}
