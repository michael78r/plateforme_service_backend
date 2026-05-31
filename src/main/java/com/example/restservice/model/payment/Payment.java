package com.example.restservice.model.payment;

public class Payment {
    private String id;
    private String orderId;
    private double amount;
    public Payment(String id, String orderId, double amount) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
}
