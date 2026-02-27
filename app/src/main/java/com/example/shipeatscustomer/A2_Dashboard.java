package com.example.shipeatscustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class A2_Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_a2_dashboard);

        setupBottomNav();
    }

    private void setupBottomNav() {
        View footer = findViewById(R.id.footer_section);
        
        footer.findViewById(R.id.dashboard_nav).setOnClickListener(v ->
                Toast.makeText(this, "You are already on Dashboard", Toast.LENGTH_SHORT).show());

        footer.findViewById(R.id.inventory_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A3_Inventory_Management.class)));

        footer.findViewById(R.id.orders_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A4_CustomerOrderActivity.class)));

        footer.findViewById(R.id.menu_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A5_MenuManagementActivity.class)));

        footer.findViewById(R.id.profile_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A6_Profile.class)));
    }
}
