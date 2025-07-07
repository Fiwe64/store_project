package storeproject.model;
/*
* SASA
* LELE
* */
public class SaleItem {
    private int saleId;
    private Product product;
    private int quantity;
    private double unitPrice;

    public SaleItem(int saleId, Product product, int quantity) {
        this.saleId = saleId;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getSalePrice();

        // Update stock
        product.setStockQuantity(product.getStockQuantity() - quantity);
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    // Getters
    public int getSaleId() {
        return saleId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}