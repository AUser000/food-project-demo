package com.example.foodprojectdemo.models;

public class Order {

    public String customerId;
    public String traderId;
    public String itemId;
    public Double price;
    public Long quantity;
    public Long orderedTime;

    public Order() {
    }

    public Order(String customerId, String traderId, String itemId, Double price, Long quantity, Long orderedTime) {
        this.customerId = customerId;
        this.traderId = traderId;
        this.itemId = itemId;
        this.price = price;
        this.quantity = quantity;
        this.orderedTime = orderedTime;
    }
}
