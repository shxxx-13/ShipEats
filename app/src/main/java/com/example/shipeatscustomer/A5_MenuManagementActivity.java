package com.example.shipeatscustomer;

import android.app.Dialog;
import android.os.Bundle;

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
                    menuList.add(ds.getValue(FoodItem.class));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Add Item Button -> Opens Inventory Dialog
        findViewById(R.id.add_menu_item_button).setOnClickListener(v -> showInventoryDialog());
    }

    private void showInventoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_a5_menu_management_add_item);

        RecyclerView rvInv = dialog.findViewById(R.id.rvInventoryItems);
        // (Assume InventoryAdapter is set up similarly to MenuAdapter)
        // When an inventory item is clicked:
        // AdminDialogHelper.showEditMenuDialog(this, selectedInventoryItem, true);

        dialog.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}