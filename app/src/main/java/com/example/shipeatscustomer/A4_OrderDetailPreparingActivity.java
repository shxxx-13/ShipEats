package com.example.shipeatscustomer;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

public class A4_OrderDetailPreparingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_order_det_preparing);

        String orderId = getIntent().getStringExtra("orderId");

        findViewById(R.id.btnPrimaryAction).setOnClickListener(v -> {
            // Update status to 'Ready' or 'Completed'
            FirebaseDatabase.getInstance().getReference("Orders")
                    .child(orderId).child("status").setValue("Completed")
                    .addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(this, OrderReadyPPActivity.class));
                        finish();
                    });
        });

        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
    }
}