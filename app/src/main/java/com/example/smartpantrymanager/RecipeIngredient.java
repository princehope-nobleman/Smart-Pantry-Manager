package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID";

    private MaterialToolbar toolbar;
    private TextView txtDetailTitle;
    private TextView txtDetailCategory;
    private TextView txtDetailPrepTime;
    private TextView txtInstructions;
    private RecyclerView recyclerRecipeIngredients;
    private MaterialButton btnCook;

    private DatabaseHelper dbHelper;
    private long recipeId = -1;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = DatabaseHelper.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        txtDetailTitle = findViewById(R.id.txtDetailTitle);
        txtDetailCategory = findViewById(R.id.txtDetailCategory);
        txtDetailPrepTime = findViewById(R.id.txtDetailPrepTime);
        txtInstructions = findViewById(R.id.txtInstructions);
        recyclerRecipeIngredients = findViewById(R.id.recyclerRecipeIngredients);
        btnCook = findViewById(R.id.btnCook);

        toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent().hasExtra(EXTRA_RECIPE_ID)) {
            recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        }

        if (recipeId <= 0) {
            Toast.makeText(this, "Invalid Recipe ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipeData();

        btnCook.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cook Recipe")
                    .setMessage("Are you sure you want to cook '" + recipe.getTitle() + "'? Required ingredients will be deducted from your pantry.")
                    .setPositiveButton("Cook", (dialog, which) -> {
                        boolean success = dbHelper.cookRecipe(recipeId);
                        if (success) {
                            Toast.makeText(this, "Cooked! Pantry ingredients updated.", Toast.LENGTH_LONG).show();
                            loadRecipeData();
                        } else {
                            Toast.makeText(this, "Failed to update pantry.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadRecipeData() {
        recipe = dbHelper.getRecipeById(recipeId);
        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        toolbar.setTitle(recipe.getTitle());
        txtDetailTitle.setText(recipe.getTitle());
        txtDetailCategory.setText(recipe.getCategory());
        txtDetailPrepTime.setText("⏱ " + recipe.getPrepTimeMinutes() + " mins prep");
        txtInstructions.setText(recipe.getInstructions());

        List<Ingredient> pantryList = dbHelper.getAllIngredients();

        recyclerRecipeIngredients.setLayoutManager(new LinearLayoutManager(this));
        RecipeIngredientAdapter riAdapter = new RecipeIngredientAdapter(recipe.getIngredients(), pantryList);
        recyclerRecipeIngredients.setAdapter(riAdapter);
    }
}
