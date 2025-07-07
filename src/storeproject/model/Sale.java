package storeproject.model;


import java.util.Date;
import java.util.ArrayList;
import java.util.List;


/*
* SASA
* LELE
*
* */
public class Sale {
    private int id;
    private Date saleDate;
    private int customerId;
    private PaymentMethod paymentMethod; // Enum: CASH, PIX, CREDIT_CARD, DEBIT_CARD
    private double totalAmount;
    private List<SaleItem> items;

    public Sale(int customerId, PaymentMethod paymentMethod) {
        this.saleDate = new Date();
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) {
        SaleItem item = new SaleItem(this.id, product, quantity);
        items.add(item);
        calculateTotal();
    }

    private void calculateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(SaleItem::getSubtotal)
                .sum();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    public int getCustomerId() {
        return customerId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<SaleItem> getItems() {
        return new ArrayList<>(items); // Returns a copy for immutability
    }

}