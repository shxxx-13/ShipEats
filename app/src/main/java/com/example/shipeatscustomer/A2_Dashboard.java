package com.example.shipeatscustomer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class A2_Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a2_dashboard);

        // Footer Navigation listeners
        findViewById(R.id.inventory_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A3_Inventory_Management.class)));

        findViewById(R.id.profile_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A6_Profile.class)));

        findViewById(R.id.orders_nav).setOnClickListener(v ->
                Toast.makeText(this, "Orders Page coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.menu_nav).setOnClickListener(v ->
                Toast.makeText(this, "Menu Page coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.dashboard_nav).setOnClickListener(v ->
                Toast.makeText(this, "Already on Dashboard", Toast.LENGTH_SHORT).show());
    }
}
