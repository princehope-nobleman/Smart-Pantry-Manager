package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView recyclerRecipes;
    private LinearLayout layoutEmptyRecipes;
    private TextView txtEmptyTitle;
    private TextView txtEmptySubtitle;

    private RecipeAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Recipe> currentRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = DatabaseHelper.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        layoutEmptyRecipes = findViewById(R.id.layoutEmptyRecipes);
        txtEmptyTitle = findViewById(R.id.txtEmptyTitle);
        txtEmptySubtitle = findViewById(R.id.txtEmptySubtitle);

        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this, currentRecipes, this);
        recyclerRecipes.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadRecipesForTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int selectedTab = tabLayout.getSelectedTabPosition();
        loadRecipesForTab(selectedTab >= 0 ? selectedTab : 0);
    }

    private void loadRecipesForTab(int tabPosition) {
        if (tabPosition == 0) {
            currentRecipes = dbHelper.getSuggestedRecipes();
            txtEmptyTitle.setText("No Suggested Recipes");
            txtEmptySubtitle.setText("None of the recipes match ALL your current pantry ingredients. Try adding more ingredients to your pantry!");
        } else {
            currentRecipes = dbHelper.getAllRecipes();
            txtEmptyTitle.setText("No Recipes Found");
            txtEmptySubtitle.setText("There are no recipes in the database.");
        }

        adapter.updateList(currentRecipes);

        if (currentRecipes.isEmpty()) {
            recyclerRecipes.setVisibility(View.GONE);
            layoutEmptyRecipes.setVisibility(View.VISIBLE);
        } else {
            recyclerRecipes.setVisibility(View.VISIBLE);
            layoutEmptyRecipes.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
