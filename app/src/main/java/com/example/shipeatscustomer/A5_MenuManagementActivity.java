package com.example.shipeatscustomer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class A5_MenuManagementActivity extends AppCompatActivity {
    private RecyclerView rvMenuItems;
    private A5_MenuAdapter adapter;
    private List<FoodItem> menuList = new ArrayList<>();
    private DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("food_items");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a5_menu_management);

        rvMenuItems = findViewById(R.id.rvMenuItems);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new A5_MenuAdapter(this, menuList);
        rvMenuItems.setAdapter(adapter);

        // Listen for Menu changes
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                menuList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FoodItem item = ds.getValue(FoodItem.class);
                    if (item != null) {
                        menuList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Add Item Button -> Opens Inventory Dialog
        findViewById(R.id.add_menu_item_button).setOnClickListener(v -> showInventoryDialog());

        // Preview Menu Button -> Navigates to MenuPreviewActivity
        findViewById(R.id.btn_preview_menu).setOnClickListener(v -> 
                startActivity(new Intent(this, MenuPreviewActivity.class)));

        setupBottomNav();
    }

    private void showInventoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_a5_menu_management_add_item);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        RecyclerView rvInv = dialog.findViewById(R.id.rvInventoryItems);
        rvInv.setLayoutManager(new LinearLayoutManager(this));
        
        List<FoodItem> inventoryList = new ArrayList<>();
        A3_InventoryAdapter invAdapter = new A3_InventoryAdapter(this, inventoryList, new A3_InventoryAdapter.OnItemActionListener() {
            @Override
            public void onDelete(FoodItem item) {
                // Not used in selection dialog
            }

            @Override
            public void onEdit(FoodItem item) {
                dialog.dismiss();
                AdminDialogHelper.showEditMenuDialog(A5_MenuManagementActivity.this, item, true);
            }
        });
        rvInv.setAdapter(invAdapter);

        // Load items from Inventory (food_items)
        FirebaseDatabase.getInstance().getReference("food_items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventoryList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FoodItem item = ds.getValue(FoodItem.class);
                    if (item != null) {
                        inventoryList.add(item);
                    }
                }
                invAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        dialog.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setupBottomNav() {
        View footer = findViewById(R.id.footer_section);
        footer.findViewById(R.id.dashboard_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A2_Dashboard.class)));

        footer.findViewById(R.id.inventory_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A3_Inventory_Management.class)));

        footer.findViewById(R.id.orders_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A4_CustomerOrderActivity.class)));

        footer.findViewById(R.id.menu_nav).setOnClickListener(v ->
                Toast.makeText(this, "You are already on Menu Management", Toast.LENGTH_SHORT).show());

        footer.findViewById(R.id.profile_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A6_Profile.class)));
    }
}