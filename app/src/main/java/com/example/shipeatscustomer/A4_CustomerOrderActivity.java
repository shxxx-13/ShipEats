package com.example.shipeatscustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class A4_CustomerOrderActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private RecyclerView rvOrders;
    private AdminOrdersAdapter adapter;
    private List<AdminOrderModel> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_cust_order);

        tabLayout = findViewById(R.id.tabLayout);
        rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrdersAdapter(this, orderList);
        rvOrders.setAdapter(adapter);

        // Default Load
        loadOrders("All");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadOrders(tab.getText().toString());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        setupBottomNav();
    }

    private void loadOrders(String status) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    AdminOrderModel order = ds.getValue(AdminOrderModel.class);
                    if (order != null) {
                        order.orderId = ds.getKey();
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        };

        if (status.equals("All")) {
            FirebaseDatabase.getInstance().getReference("Orders").addValueEventListener(listener);
        } else {
            FirebaseDatabase.getInstance().getReference("Orders")
                    .orderByChild("status").equalTo(status)
                    .addValueEventListener(listener);
        }
    }

    private void setupBottomNav() {
        View footer = findViewById(R.id.footer_section);
        footer.findViewById(R.id.dashboard_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A2_Dashboard.class)));

        footer.findViewById(R.id.inventory_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A3_Inventory_Management.class)));

        footer.findViewById(R.id.menu_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A5_MenuManagementActivity.class)));

        footer.findViewById(R.id.profile_nav).setOnClickListener(v ->
                startActivity(new Intent(this, A6_Profile.class)));
    }
}
