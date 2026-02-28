package com.example.shipeatscustomer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class A5_MenuAdapter extends RecyclerView.Adapter<A5_MenuAdapter.MenuViewHolder> {
    private Context context;
    private List<FoodItem> menuList;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public A5_MenuAdapter(Context context, List<FoodItem> menuList, ActivityResultLauncher<Intent> launcher) {
        this.context = context;
        this.menuList = menuList;
        this.imagePickerLauncher = launcher;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_it_menuitem, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        FoodItem item = menuList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("RM " + String.format("%.2f", item.getPrice()));

        // Handle Edit Flow
        holder.itemView.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                AdminDialogHelper.showEditMenuDialog((AppCompatActivity) context, imagePickerLauncher, item, false);
            }
        });

        // Handle Delete Flow
        holder.btnRemove.setOnClickListener(v ->
                AdminDialogHelper.showDeleteConfirmDialog(context, item.getId()));
    }

    @Override
    public int getItemCount() { return menuList.size(); }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        Button btnRemove;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}