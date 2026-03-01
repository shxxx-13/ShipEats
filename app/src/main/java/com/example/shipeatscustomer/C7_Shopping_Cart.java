package com.example.shipeatscustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class C7_Shopping_Cart extends AppCompatActivity {

    private LinearLayout cartItemsContainer;
    private TextView subtotalTv, taxTv, feeTv, totalTv;
    private double platformFee = 1.00;
    private MaterialButton continueToPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_c7_shopping_cart);
        
        ImageView back_button = findViewById(R.id.back_button);
        SwitchMaterial preOrderSwitch = findViewById(R.id.pre_order);
        LinearLayout preOrderSection = findViewById(R.id.enable_pre_order);
        cartItemsContainer = findViewById(R.id.shopping_cart_items);
        continueToPaymentButton = findViewById(R.id.continue_to_payment_button);
        
        subtotalTv = findViewById(R.id.subtotal);
        taxTv = findViewById(R.id.service_tax);
        feeTv = findViewById(R.id.platform_fee);
        totalTv = findViewById(R.id.total_price);

        back_button.setOnClickListener(v -> finish());

        // IF THE PRE-ORDER TOGGLE IS ON, SHOW THE TIME SELECTION
        preOrderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preOrderSection.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        continueToPaymentButton.setOnClickListener(v -> {
            // Navigate to Payment Method Selection
            Intent intent = new Intent(this, C11_Payment_Method.class);
            startActivity(intent);
        });

        loadCartItems();
    }

    private void loadCartItems() {
        cartItemsContainer.removeAllViews();
        List<CartItem> items = CartManager.getInstance().getCartItems();

        if (items.isEmpty()) {
            checkCartEmpty();
            return;
        }

        for (CartItem item : items) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.shopping_cart_items_layout, cartItemsContainer, false);
            
            TextView name = itemView.findViewById(R.id.item_name);
            TextView price = itemView.findViewById(R.id.item_price);
            TextView counter = itemView.findViewById(R.id.food_counter);
            ImageView image = itemView.findViewById(R.id.item_image);
            ImageView plus = itemView.findViewById(R.id.plus_button);
            ImageView minus = itemView.findViewById(R.id.minus_button);

            FoodItem food = item.getFoodItem();
            name.setText(food.getName());
            price.setText("RM " + String.format("%.2f", food.getPrice() * item.getQuantity()));
            counter.setText(String.valueOf(item.getQuantity()));
            Glide.with(this).load(food.getImageUrl()).placeholder(R.drawable.no_image_available).into(image);

            plus.setOnClickListener(v -> {
                item.setQuantity(item.getQuantity() + 1);
                updateItemUI(item, counter, price);
                calculateTotal();
            });

            minus.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    updateItemUI(item, counter, price);
                } else {
                    CartManager.getInstance().getCartItems().remove(item);
                    cartItemsContainer.removeView(itemView);
                    checkCartEmpty();
                }
                calculateTotal();
            });

            cartItemsContainer.addView(itemView);
        }

        calculateTotal();
        checkCartEmpty();
    }

    private void updateItemUI(CartItem item, TextView counter, TextView price) {
        counter.setText(String.valueOf(item.getQuantity()));
        price.setText("RM " + String.format("%.2f", item.getFoodItem().getPrice() * item.getQuantity()));
    }

    private void calculateTotal() {
        double subtotal = 0;
        for (CartItem item : CartManager.getInstance().getCartItems()) {
            subtotal += item.getFoodItem().getPrice() * item.getQuantity();
        }

        double tax = subtotal * 0.06; // 6% Service Tax
        double total = subtotal + tax + platformFee;

        subtotalTv.setText("RM " + String.format("%.2f", subtotal));
        taxTv.setText("RM " + String.format("%.2f", tax));
        feeTv.setText("RM " + String.format("%.2f", platformFee));
        totalTv.setText("RM " + String.format("%.2f", total));
    }

    private void checkCartEmpty() {
        TextView emptyMessage = findViewById(R.id.empty_cart_message);
        LinearLayout preOrderContainer = findViewById(R.id.pre_order_container);
        LinearLayout summaryContainer = findViewById(R.id.checkout_summary_container);

        boolean isEmpty = CartManager.getInstance().getCartItems().isEmpty();

        emptyMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        preOrderContainer.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        summaryContainer.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (isEmpty) {
            continueToPaymentButton.setEnabled(false);
            continueToPaymentButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#c7c7c7")));
        } else {
            continueToPaymentButton.setEnabled(true);
            continueToPaymentButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E69303")));
        }
    }
}