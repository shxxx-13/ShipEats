package com.example.shipeatscustomer;

public class AdminOrderModel {
    public String orderId, items, totalPrice, status, customerName;
    public int itemCount;

    public AdminOrderModel() {} // Required for Firebase

    public AdminOrderModel(String orderId, String items, String totalPrice, String status, int itemCount) {
        this.orderId = orderId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.itemCount = itemCount;
    }
}