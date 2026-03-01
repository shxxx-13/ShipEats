package com.example.shipeatscustomer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.OrderViewHolder> {

    private final Context context;
    private final List<AdminOrderModel> orderList;

    public AdminOrdersAdapter(Context context, List<AdminOrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_it_order_card, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        AdminOrderModel order = orderList.get(position);

        if (order == null) return;

        // Null-safe Order ID display
        String orderId = order.orderId != null ? order.orderId : "unknown";
        String displayId = orderId.length() > 8 ? orderId.substring(orderId.length() - 8).toUpperCase() : orderId;
        holder.tvOrderNo.setText("#" + displayId);

        // Null-safe status display
        String status = order.status != null ? order.status : "Pending";
        holder.tvStatusBadge.setText(status.toLowerCase());

        holder.tvCustomerName.setText("⚪ " + (order.customerName != null ? order.customerName : "Guest"));
        
        String itemsText = order.items != null ? order.items.replace("\n", ", ") : "No items";
        holder.tvItems.setText("Items: " + itemsText);
        
        holder.tvItemCount.setText(order.itemCount + (order.itemCount > 1 ? " items" : " item"));
        holder.tvTotal.setText("Total " + (order.totalPrice != null ? order.totalPrice : "RM 0.00"));

        // Update badge color based on status
        if ("Pending".equalsIgnoreCase(status)) {
            holder.tvStatusBadge.setBackgroundResource(R.drawable.stat_pending_bg);
        } else if ("Preparing".equalsIgnoreCase(status)) {
            holder.tvStatusBadge.setBackgroundResource(R.drawable.stat_preparing_bg);
        } else {
            holder.tvStatusBadge.setBackgroundResource(R.drawable.stat_accepted_bg);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = null;
            if ("Pending".equalsIgnoreCase(status)) {
                intent = new Intent(context, A4_OrderDetailPendingActivity.class);
            } else if ("Preparing".equalsIgnoreCase(status)) {
                intent = new Intent(context, A4_OrderDetailPreparingActivity.class);
            }

            if (intent != null) {
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return orderList.size(); }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNo, tvStatusBadge, tvCustomerName, tvItems, tvItemCount, tvTotal;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNo = itemView.findViewById(R.id.tvOrderNo);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvItems = itemView.findViewById(R.id.tvItems);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
