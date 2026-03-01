package com.example.shipeatscustomer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class C5_History_Page extends AppCompatActivity {

    private LinearLayout historyContainer;
    private DatabaseReference ordersRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_c5_history_page);

        historyContainer = findViewById(R.id.history_lists_container);
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                        FirebaseAuth.getInstance().getCurrentUser().getUid() : "GuestUser";

        ImageView history_icon = findViewById(R.id.history_icon);
        TextView history_text = findViewById(R.id.history_text);
        ImageView back_button = findViewById(R.id.back_button);
        LinearLayout menu_nav = findViewById(R.id.menu_nav);
        LinearLayout settings_nav = findViewById(R.id.settings_nav);
        TextView clear_history = findViewById(R.id.clear_history);

        back_button.setOnClickListener(v -> finish());
        menu_nav.setOnClickListener(v -> startActivity(new Intent(this, C3_Menu_Page.class)));
        settings_nav.setOnClickListener(v -> startActivity(new Intent(this, C13_Settings_Page.class )));

        int activeColor = Color.parseColor("#FFD700");
        history_icon.setColorFilter(activeColor);
        history_text.setTextColor(activeColor);
        history_text.setTypeface(null, Typeface.BOLD);

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyContainer.removeAllViews();
                boolean hasOrders = false;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    AdminOrderModel order = ds.getValue(AdminOrderModel.class);
                    if (order != null && currentUserId.equals(order.customerId)) {
                        addHistoryRow(order);
                        hasOrders = true;
                    }
                }

                if (!hasOrders) {
                    Toast.makeText(C5_History_Page.this, "No order history found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(C5_History_Page.this, "Error loading history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addHistoryRow(AdminOrderModel order) {
        View row = LayoutInflater.from(this).inflate(R.layout.order_history_list_layout, historyContainer, false);
        
        TextView name = row.findViewById(R.id.item_name);
        TextView qty = row.findViewById(R.id.item_quantity);
        TextView price = row.findViewById(R.id.item_total_price);
        TextView instructions = row.findViewById(R.id.special_instruction);

        name.setText(order.items); // Summary of items
        qty.setText(String.valueOf(order.itemCount));
        price.setText(order.totalPrice);
        instructions.setText("Status: " + order.status);

        historyContainer.addView(row);
    }
}
