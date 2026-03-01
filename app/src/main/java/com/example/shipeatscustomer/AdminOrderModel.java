package com.example.shipeatscustomer;

import java.io.Serializable;

public class AdminOrderModel implements Serializable {
    public String orderId;
    public String items;
    public String totalPrice;
    public String status;
    public String customerName;
    public String customerId;
    public int itemCount;

    public AdminOrderModel() {
        // Required empty constructor for Firebase
    }

    // Comprehensive constructor
    public AdminOrderModel(String orderId, String items, String totalPrice, String status, int itemCount, String customerName, String customerId) {
        this.orderId = orderId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.itemCount = itemCount;
        this.customerName = customerName;
        this.customerId = customerId;
    }

    // Legacy constructor for compatibility if needed
    public AdminOrderModel(String orderId, String items, String totalPrice, String status, int itemCount) {
        this.orderId = orderId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.itemCount = itemCount;
    }
}