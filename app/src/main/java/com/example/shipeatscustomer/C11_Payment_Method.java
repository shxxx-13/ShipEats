package com.example.shipeatscustomer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class C11_Payment_Method extends AppCompatActivity {

    private MaterialCardView btnCard, btnFPX;
    private TextView tvPaymentInfo;
    private ImageView ivBack;
    private Button btnPlaceOrder;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c11_payment_method);

        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // 1. Initialize Views
        btnCard = findViewById(R.id.btnCardInfo);
        btnFPX = findViewById(R.id.btnFPXInfo);
        tvPaymentInfo = findViewById(R.id.tvPaymentInfo);
        ivBack = findViewById(R.id.ivBack);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // 2. Back Button Logic
        ivBack.setOnClickListener(v -> finish());

        // 3. Set Card Click Logic
        btnCard.setOnClickListener(v -> showCardInfo());

        // 4. Set FPX Click Logic
        btnFPX.setOnClickListener(v -> showFPXInfo());

        // 5. Place Order Logic
        btnPlaceOrder.setOnClickListener(v -> placeOrder());

        // Load default state
        showCardInfo();
    }

    private void placeOrder() {
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : "GuestUser";
        String userName = user != null && user.getEmail() != null ? user.getEmail().split("@")[0] : "Guest Customer";
        
        String orderId = ordersRef.push().getKey();
        
        StringBuilder itemsSummary = new StringBuilder();
        double subtotal = 0;
        int totalQty = 0;
        
        for (CartItem item : cartItems) {
            itemsSummary.append(item.getFoodItem().getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append("\n");
            subtotal += item.getFoodItem().getPrice() * item.getQuantity();
            totalQty += item.getQuantity();
        }

        double total = subtotal + (subtotal * 0.06) + 1.00; // Total with tax and platform fee
        String finalPrice = String.format("RM %.2f", total);

        // Creating the order object for the admin to see
        // FIX: Passing all 7 required arguments to match the updated AdminOrderModel constructor
        AdminOrderModel order = new AdminOrderModel(
                orderId, 
                itemsSummary.toString().trim(), 
                finalPrice, 
                "Pending", 
                totalQty, 
                userName, 
                userId
        );

        if (orderId != null) {
            ordersRef.child(orderId).setValue(order).addOnSuccessListener(aVoid -> {
                // Clear the cart after successful order
                CartManager.getInstance().clearCart();
                
                // Show success screen and pass order number
                Intent intent = new Intent(C11_Payment_Method.this, C8_Order_Placed.class);
                intent.putExtra("ORDER_ID", orderId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                
                Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showCardInfo() {
        btnCard.setCardBackgroundColor(Color.parseColor("#B2EBF2")); 
        btnFPX.setCardBackgroundColor(Color.WHITE);
        tvPaymentInfo.setText("Secure credit/debit card payment. Your data is encrypted and never stored on our servers.");
    }

    private void showFPXInfo() {
        btnFPX.setCardBackgroundColor(Color.parseColor("#B2EBF2"));
        btnCard.setCardBackgroundColor(Color.WHITE);
        tvPaymentInfo.setText("Pay directly via your bank's secure online portal. Fast and reliable transactions.");
    }
}