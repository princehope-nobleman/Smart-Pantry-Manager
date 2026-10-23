package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IngredientAdapter.OnIngredientClickListener {

    private RecyclerView recyclerIngredients;
    private LinearLayout layoutEmpty;
    private MaterialCardView cardSuggestedRecipes;
    private MaterialCardView cardSettings;
    private TextView txtSuggestedSubtitle;
    private FloatingActionButton fabAdd;

    private IngredientAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Ingredient> ingredientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DatabaseHelper.getInstance(this);

        recyclerIngredients = findViewById(R.id.recyclerIngredients);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        cardSuggestedRecipes = findViewById(R.id.cardSuggestedRecipes);
        cardSettings = findViewById(R.id.cardSettings);
        txtSuggestedSubtitle = findViewById(R.id.txtSuggestedSubtitle);
        fabAdd = findViewById(R.id.fabAdd);

        recyclerIngredients.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientAdapter(ingredientList, this);
        recyclerIngredients.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        cardSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        cardSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryData();
    }

    private void loadPantryData() {
        ingredientList = dbHelper.getAllIngredients();
        adapter.updateList(ingredientList);

        if (ingredientList.isEmpty()) {
            recyclerIngredients.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerIngredients.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }

        List<Recipe> suggested = dbHelper.getSuggestedRecipes();
        int count = suggested.size();
        txtSuggestedSubtitle.setText(count == 1 ? "1 Ready to Make" : count + " Ready to Make");
    }

    @Override
    public void onItemClick(Ingredient ingredient) {
        openEditScreen(ingredient);
    }

    @Override
    public void onEditClick(Ingredient ingredient) {
        openEditScreen(ingredient);
    }

    private void openEditScreen(Ingredient ingredient) {
        Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_INGREDIENT_ID, ingredient.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Ingredient ingredient) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to delete '" + ingredient.getName() + "' from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteIngredient(ingredient.getId());
                    Toast.makeText(MainActivity.this, ingredient.getName() + " deleted", Toast.LENGTH_SHORT).show();
                    loadPantryData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
