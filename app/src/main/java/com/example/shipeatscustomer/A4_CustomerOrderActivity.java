package com.example.shipeatscustomer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class A4_CustomerOrderActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private RecyclerView rvOrders;
    private AdminOrdersAdapter adapter;
    private List<AdminOrderModel> allOrders = new ArrayList<>();
    private List<AdminOrderModel> filteredList = new ArrayList<>();
    private String currentStatus = "All";
    private TextInputEditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_cust_order);

        tabLayout = findViewById(R.id.tabLayout);
        rvOrders = findViewById(R.id.rvOrders);
        searchInput = findViewById(R.id.search_input);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrdersAdapter(this, filteredList);
        rvOrders.setAdapter(adapter);

        loadAllOrders();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null && tab.getText() != null) {
                    currentStatus = tab.getText().toString();
                    applyFilters();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        if (searchInput != null) {
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyFilters();
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        setupBottomNav();
    }

    private void loadAllOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allOrders.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    AdminOrderModel order = ds.getValue(AdminOrderModel.class);
                    if (order != null) {
                        order.orderId = ds.getKey();
                        allOrders.add(order);
                    }
                }
                applyFilters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(A4_CustomerOrderActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String query = (searchInput != null && searchInput.getText() != null) ? 
                      searchInput.getText().toString().toLowerCase().trim() : "";
        filteredList.clear();

        for (AdminOrderModel order : allOrders) {
            if (order == null || order.status == null) continue;

            String status = order.status;
            // Map internal database status to TabLayout text if necessary
            if ("Completed".equalsIgnoreCase(status)) status = "Done";

            boolean matchesStatus = "All".equalsIgnoreCase(currentStatus) || currentStatus.equalsIgnoreCase(status);
            
            String orderId = order.orderId != null ? order.orderId.toLowerCase() : "";
            String custName = order.customerName != null ? order.customerName.toLowerCase() : "";
            
            boolean matchesSearch = orderId.contains(query) || custName.contains(query);

            if (matchesStatus && matchesSearch) {
                filteredList.add(order);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupBottomNav() {
        View footer = findViewById(R.id.footer_section);
        if (footer != null) {
            footer.findViewById(R.id.dashboard_nav).setOnClickListener(v ->
                    startActivity(new Intent(this, A2_Dashboard.class)));

            footer.findViewById(R.id.inventory_nav).setOnClickListener(v ->
                    startActivity(new Intent(this, A3_Inventory_Management.class)));

            footer.findViewById(R.id.orders_nav).setOnClickListener(v ->
                    Toast.makeText(this, "Already on Orders", Toast.LENGTH_SHORT).show());

            footer.findViewById(R.id.menu_nav).setOnClickListener(v ->
                    startActivity(new Intent(this, A5_MenuManagementActivity.class)));

            footer.findViewById(R.id.profile_nav).setOnClickListener(v ->
                    startActivity(new Intent(this, A6_Profile.class)));
        }
    }
}
