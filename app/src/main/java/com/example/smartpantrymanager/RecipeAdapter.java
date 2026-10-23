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

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipeList;
    private OnRecipeClickListener listener;
    private DatabaseHelper dbHelper;

    public RecipeAdapter(Context context, List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public void updateList(List<Recipe> newList) {
        this.recipeList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.txtRecipeTitle.setText(recipe.getTitle());
        holder.txtCategory.setText(recipe.getCategory());
        holder.txtPrepTime.setText(recipe.getPrepTimeMinutes() + " mins prep");

        int reqCount = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
        holder.txtIngredientCount.setText(reqCount + " ingredients");

        // Check matching ingredients
        List<Ingredient> pantryList = dbHelper.getAllIngredients();
        int matchedCount = 0;

        if (recipe.getIngredients() != null) {
            for (RecipeIngredient req : recipe.getIngredients()) {
                for (Ingredient pantry : pantryList) {
                    if (pantry.getName().trim().equalsIgnoreCase(req.getName().trim())
                            && pantry.getQuantity() >= req.getQuantity()) {
                        matchedCount++;
                        break;
                    }
                }
            }
        }

        if (reqCount > 0 && matchedCount == reqCount) {
            holder.txtMatchBadge.setText("✓ Ready to Cook (All Ingredients Available)");
            holder.txtMatchBadge.setBackgroundColor(Color.parseColor("#388E3C"));
            holder.txtMatchBadge.setVisibility(View.VISIBLE);
        } else if (matchedCount > 0) {
            holder.txtMatchBadge.setText("Partial (" + matchedCount + "/" + reqCount + " ingredients in pantry)");
            holder.txtMatchBadge.setBackgroundColor(Color.parseColor("#F57C00"));
            holder.txtMatchBadge.setVisibility(View.VISIBLE);
        } else {
            holder.txtMatchBadge.setText("Missing ingredients (" + reqCount + " needed)");
            holder.txtMatchBadge.setBackgroundColor(Color.parseColor("#D32F2F"));
            holder.txtMatchBadge.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRecipeClick(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRecipeTitle;
        TextView txtCategory;
        TextView txtPrepTime;
        TextView txtIngredientCount;
        TextView txtMatchBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRecipeTitle = itemView.findViewById(R.id.txtRecipeTitle);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtPrepTime = itemView.findViewById(R.id.txtPrepTime);
            txtIngredientCount = itemView.findViewById(R.id.txtIngredientCount);
            txtMatchBadge = itemView.findViewById(R.id.txtMatchBadge);
        }
    }
}
