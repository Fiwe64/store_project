package storeproject.model;

import java.util.Date;

public class Product {
    private int id;
    private String description;
    private String barCode;
    private String unitOfMeasurement;
    private Date lastPurchaseDate;
    private Double salePrice;
    private int stockQuantity;

    public Product(){}
    public Product(int id, String description,
                   String barCode, String unitOfMeasurement,
                   Date lastPurchaseDate, Double salePrice,
                   int stockQuantity) {
        this.id = id;
        this.description = description;
        this.barCode = barCode;
        this.unitOfMeasurement = unitOfMeasurement;
        this.lastPurchaseDate = lastPurchaseDate;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
    }
    public Product(String description,
                   String barCode,String unitOfMeasurement, Double salePrice,
                   int stockQuantity) {
        this.description = description;
        this.barCode = barCode;
        this.unitOfMeasurement = unitOfMeasurement;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public Date getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(Date lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }



}
