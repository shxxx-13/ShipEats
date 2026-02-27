package com.example.shipeatscustomer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

public class A4_OrderDetailPendingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_order_det_pending);

        String orderId = getIntent().getStringExtra("orderId");

        // ACCEPT ORDER: Change status to 'Preparing'
        findViewById(R.id.btnPrimaryAction).setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Orders")
                    .child(orderId).child("status").setValue("Preparing")
                    .addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(this, OrderAcceptedPPActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // CANCEL ORDER: Remove or change status to 'Cancelled'
        findViewById(R.id.btnSecondaryAction).setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Orders")
                    .child(orderId).child("status").setValue("Cancelled")
                    .addOnSuccessListener(aVoid -> {
                        AdminDialogHelper.showStatusDialog(this, R.layout.admin_dialog_order_deleted);
                        finish();
                    });
        });

        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
    }
}