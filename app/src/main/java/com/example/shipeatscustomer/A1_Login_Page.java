package com.example.shipeatscustomer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class A1_Login_Page extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a1_login_page);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        emailInput = findViewById(R.id.email_address);
        passwordInput = findViewById(R.id.password);

        // Tab Listeners
        findViewById(R.id.login_option).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.login_button).setOnClickListener(v -> performLogin());

        findViewById(R.id.create_account).setOnClickListener(v -> {
            Intent intent = new Intent(this, C2_Create_Account.class);
            intent.putExtra("userType", "Admin");
            startActivity(intent);
        });
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) return;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        // Verify Admin status in database
                        mDatabase.child("Admins").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(A1_Login_Page.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(A1_Login_Page.this, A2_Dashboard.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(A1_Login_Page.this, "Access Denied: Not an admin.", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    } else {
                        Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}