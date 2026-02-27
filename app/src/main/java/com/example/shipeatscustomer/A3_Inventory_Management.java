package com.example.shipeatscustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class A3_Inventory_Management extends AppCompatActivity {

    RecyclerView recyclerView;
    MaterialButton addItemBtn;

    DatabaseReference databaseRef;
    List<FoodItem> foodList;
    A3_InventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_a3_inventory_management);

        recyclerView = findViewById(R.id.inventory_recycler);
        addItemBtn = findViewById(R.id.btn_add_item);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        foodList = new ArrayList<>();

        databaseRef = FirebaseDatabase.getInstance().getReference("food_items");

        adapter = new A3_InventoryAdapter(this, foodList, new A3_InventoryAdapter.OnItemActionListener() {
            @Override
            public void onDelete(FoodItem item) {
                AdminDialogHelper.showDeleteConfirmDialog(A3_Inventory_Management.this, item.getId());
            }

            @Override
            public void onEdit(FoodItem item) {
                AdminDialogHelper.showEditMenuDialog(A3_Inventory_Management.this, item, false);
            }
        });

        recyclerView.setAdapter(adapter);

        // Real-time Firebase listener
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    FoodItem item = data.getValue(FoodItem.class);
                    if (item != null) {
                        foodList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(A3_Inventory_Management.this,
                        "Database Error", Toast.LENGTH_SHORT).show();
            }
        });

        addItemBtn.setOnClickListener(v -> AdminDialogHelper.showEditMenuDialog(this, null, true));

        setupBottomNav();
    }

    private void setupBottomNav() {
        View footer = findViewById(R.id.footer_section);
        footer.findViewById(R.id.dashboard_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A2_Dashboard.class)));

        footer.findViewById(R.id.profile_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A6_Profile.class)));
    }
}