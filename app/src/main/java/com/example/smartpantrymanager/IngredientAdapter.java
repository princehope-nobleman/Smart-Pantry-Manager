package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    public interface OnIngredientClickListener {
        void onItemClick(Ingredient ingredient);
        void onEditClick(Ingredient ingredient);
        void onDeleteClick(Ingredient ingredient);
    }

    private List<Ingredient> ingredientList;
    private OnIngredientClickListener listener;

    public IngredientAdapter(List<Ingredient> ingredientList, OnIngredientClickListener listener) {
        this.ingredientList = ingredientList;
        this.listener = listener;
    }

    public void updateList(List<Ingredient> newList) {
        this.ingredientList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);

        holder.txtName.setText(ingredient.getName());

        String formattedQty = String.format("%.2f", ingredient.getQuantity());
        if (formattedQty.endsWith(".00")) {
            formattedQty = String.format("%.0f", ingredient.getQuantity());
        }
        holder.txtQuantity.setText(formattedQty + " " + ingredient.getUnit());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(ingredient);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(ingredient);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(ingredient);
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList != null ? ingredientList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtQuantity;
        ImageButton btnEdit;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
