package storeproject.model;

import java.util.Date;

public class SaleSummary {
    private Date saleDate;
    private PaymentMethod paymentMethod;
    private double totalAmount;
    private int saleCount; // Optional: number of sales for this payment method

    public SaleSummary(Date saleDate, PaymentMethod paymentMethod, double totalAmount) {
        this.saleDate = saleDate;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.saleCount = 1;
    }

    // Constructor for daily summaries
    public SaleSummary(Date saleDate, PaymentMethod paymentMethod, double totalAmount, int saleCount) {
        this.saleDate = saleDate;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.saleCount = saleCount;
    }

    // Getters
    public Date getSaleDate() {
        return saleDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getSaleCount() {
        return saleCount;
    }

    // Method to add amounts when consolidating
    public void addToSummary(double amount) {
        this.totalAmount += amount;
        this.saleCount++;
    }

    @Override
    public String toString() {
        return String.format("%tF - %s: $%,.2f (%d sales)",
                saleDate,
                paymentMethod,
                totalAmount,
                saleCount);
    }
}
