package com.example.shipeatscustomer;

import java.io.Serializable;

public class CartItem implements Serializable {
    private FoodItem foodItem;
    private int quantity;

    public CartItem() {}

    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}