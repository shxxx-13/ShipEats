package com.example.shipeatscustomer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminDialogHelper {
    private static DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("food_items");
    private static Uri selectedImageUri = null;
    private static ImageView dialogImageView = null;

    public static void showEditMenuDialog(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher, FoodItem item, boolean isNew) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.admin_dialog_add_item);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etName = dialog.findViewById(R.id.et_name);
        EditText etDesc = dialog.findViewById(R.id.et_description);
        EditText etPrice = dialog.findViewById(R.id.et_price);
        Spinner spinnerQty = dialog.findViewById(R.id.spinner_quantity);
        Spinner spinnerCat = dialog.findViewById(R.id.spinner_category);
        Button btnDone = dialog.findViewById(R.id.btn_add_confirm);
        ImageView btnCancel = dialog.findViewById(R.id.btn_close);
        View cardImageUpload = dialog.findViewById(R.id.card_image_upload);
        dialogImageView = dialog.findViewById(R.id.img_food);

        selectedImageUri = null; // Reset for new session

        // Populate Spinners
        ArrayAdapter<CharSequence> qtyAdapter = ArrayAdapter.createFromResource(activity,
                R.array.quantity_array, android.R.layout.simple_spinner_item);
        qtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQty.setAdapter(qtyAdapter);

        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(activity,
                R.array.category_array, android.R.layout.simple_spinner_item);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(catAdapter);

        if (!isNew && item != null) {
            etName.setText(item.getName());
            etDesc.setText(item.getDescription());
            etPrice.setText(String.valueOf(item.getPrice()));
            // Set spinner selections
            int qtyPosition = qtyAdapter.getPosition(String.valueOf(item.getQuantity()));
            spinnerQty.setSelection(qtyPosition);
            int catPosition = catAdapter.getPosition(item.getCategory());
            spinnerCat.setSelection(catPosition);
            
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(activity).load(item.getImageUrl()).into(dialogImageView);
            }
        }

        cardImageUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });

        btnDone.setOnClickListener(v -> {
            try {
                String id = isNew ? databaseRef.push().getKey() : item.getId();
                String name = etName.getText().toString();
                String desc = etDesc.getText().toString();
                double price = Double.parseDouble(etPrice.getText().toString());
                int quantity = Integer.parseInt(spinnerQty.getSelectedItem().toString());
                String category = spinnerCat.getSelectedItem().toString();
                
                String imageUrl = (selectedImageUri != null) ? selectedImageUri.toString() : 
                                 (item != null && item.getImageUrl() != null ? item.getImageUrl() : "");

                FoodItem updatedItem = new FoodItem(id, name, desc, category, price, quantity, imageUrl);

                databaseRef.child(id).setValue(updatedItem).addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    showStatusDialog(activity, isNew ? R.layout.admin_dialog_menu_add : R.layout.admin_dialog_menu_complete);
                });
            } catch (NumberFormatException e) {
                Toast.makeText(activity, "Invalid number format", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public static void handleImageResult(Uri uri) {
        selectedImageUri = uri;
        if (dialogImageView != null && uri != null) {
            Glide.with(dialogImageView.getContext()).load(uri).into(dialogImageView);
        }
    }

    public static void showDeleteConfirmDialog(Context context, String itemId) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.admin_dialog_item_confirm_delete);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnDelete = dialog.findViewById(R.id.btnDelete);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnDelete.setOnClickListener(v -> {
            databaseRef.child(itemId).removeValue().addOnSuccessListener(aVoid -> {
                dialog.dismiss();
                showStatusDialog(context, R.layout.admin_dialog_menu_delete);
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public static void showStatusDialog(Context context, int layoutId) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(layoutId);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        new android.os.Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 1500);
    }
}