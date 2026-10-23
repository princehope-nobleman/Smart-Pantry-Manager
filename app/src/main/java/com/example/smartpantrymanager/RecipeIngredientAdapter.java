package com.example.smartpantrymanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder> {

    private List<RecipeIngredient> ingredientList;
    private List<Ingredient> pantryList;

    public RecipeIngredientAdapter(List<RecipeIngredient> ingredientList, List<Ingredient> pantryList) {
        this.ingredientList = ingredientList;
        this.pantryList = pantryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_ingredient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeIngredient ri = ingredientList.get(position);

        holder.txtIngredientName.setText(ri.getName());

        String formattedQty = String.format("%.2f", ri.getQuantity());
        if (formattedQty.endsWith(".00")) {
            formattedQty = String.format("%.0f", ri.getQuantity());
        }
        holder.txtRequiredQuantity.setText(formattedQty + " " + ri.getUnit());

        Ingredient match = null;
        if (pantryList != null) {
            for (Ingredient p : pantryList) {
                if (p.getName().trim().equalsIgnoreCase(ri.getName().trim())) {
                    match = p;
                    break;
                }
            }
        }

        if (match != null) {
            String pQtyStr = String.format("%.2f", match.getQuantity());
            if (pQtyStr.endsWith(".00")) {
                pQtyStr = String.format("%.0f", match.getQuantity());
            }

            if (match.getQuantity() >= ri.getQuantity()) {
                holder.txtPantryStatus.setText("✓ In Pantry (" + pQtyStr + " " + match.getUnit() + ")");
                holder.txtPantryStatus.setBackgroundColor(Color.parseColor("#388E3C"));
            } else {
                holder.txtPantryStatus.setText("⚠ Low (" + pQtyStr + " " + match.getUnit() + ")");
                holder.txtPantryStatus.setBackgroundColor(Color.parseColor("#F57C00"));
            }
        } else {
            holder.txtPantryStatus.setText("✗ Missing");
            holder.txtPantryStatus.setBackgroundColor(Color.parseColor("#D32F2F"));
        }
    }

    @Override
    public int getItemCount() {
        return ingredientList != null ? ingredientList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtIngredientName;
        TextView txtRequiredQuantity;
        TextView txtPantryStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIngredientName = itemView.findViewById(R.id.txtIngredientName);
            txtRequiredQuantity = itemView.findViewById(R.id.txtRequiredQuantity);
            txtPantryStatus = itemView.findViewById(R.id.txtPantryStatus);
        }
    }
}
