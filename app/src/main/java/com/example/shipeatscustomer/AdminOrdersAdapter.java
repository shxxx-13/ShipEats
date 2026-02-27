package com.example.shipeatscustomer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

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
        View v = LayoutInflater.from(context).inflate(R.layout.admin_it_order_card, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        AdminOrderModel order = orderList.get(position);

        holder.tvOrderId.setText("Order No. " + order.orderId);
        holder.tvTotal.setText("RM " + order.totalPrice);

        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            // Logic to route to the correct detail view based on current status
            if ("Pending".equals(order.status)) {
                intent = new Intent(context, A4_OrderDetailPendingActivity.class);
            } else if ("Preparing".equals(order.status)) {
                intent = new Intent(context, A4_OrderDetailPreparingActivity.class);
            } else {
                return; // No detail view for completed/cancelled in this flow
            }

            // Pass the ID to the activity
            intent.putExtra("orderId", order.orderId);
            context.startActivity(intent);
        });
    }

    private void openOrderDetail(AdminOrderModel order) {
        Dialog dialog = new Dialog(context);

        // Dynamic layout selection based on current status
        int layoutRes;
        switch (order.status) {
            case "Preparing": layoutRes = R.layout.activity_a4_order_det_preparing; break;
            case "Completed": layoutRes = R.layout.activity_a4_order_det_history; break;
            default: layoutRes = R.layout.activity_a4_order_det_pending; break;
        }

        dialog.setContentView(layoutRes);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Logic for "Primary Action" buttons (Accept / Mark as Ready)
        Button btnPrimary = dialog.findViewById(R.id.btnPrimaryAction);
        if (btnPrimary != null) {
            btnPrimary.setOnClickListener(v -> {
                String nextStatus = order.status.equals("Pending") ? "Preparing" : "Completed";
                int successLayout = order.status.equals("Pending") ?
                        R.layout.admin_dialog_order_accepted : R.layout.admin_dialog_order_ready;

                FirebaseDatabase.getInstance().getReference("Orders")
                        .child(order.orderId).child("status").setValue(nextStatus)
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                            AdminDialogHelper.showStatusDialog(context, successLayout);
                        });
            });
        }

        // Logic for Cancel/Delete
        View btnSecondary = dialog.findViewById(R.id.btnSecondaryAction);
        if (btnSecondary != null) {
            btnSecondary.setOnClickListener(v -> {
                dialog.dismiss();
                AdminDialogHelper.showDeleteConfirmDialog(context, order.orderId);
            });
        }

        dialog.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public int getItemCount() { return orderList.size(); }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvTotal;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrder);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
