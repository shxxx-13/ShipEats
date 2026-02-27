package com.example.shipeatscustomer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderDeletedPPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_order_det_pending);

        findViewById(R.id.btnPrimaryAction).setOnClickListener(v -> {
            // Success Logic
            AdminDialogHelper.showStatusDialog(this, R.layout.admin_dialog_order_accepted);
            finish();
        });
    }
}