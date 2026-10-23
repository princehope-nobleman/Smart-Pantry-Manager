package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PREFS_SMART_PANTRY";
    private static final String KEY_UNIT_SYSTEM = "KEY_UNIT_SYSTEM";

    private MaterialToolbar toolbar;
    private SwitchMaterial switchUnitSystem;
    private TextView txtUnitSystemLabel;
    private MaterialButton btnResetRecipes;
    private MaterialButton btnClearPantry;

    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = DatabaseHelper.getInstance(this);
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        toolbar = findViewById(R.id.toolbar);
        switchUnitSystem = findViewById(R.id.switchUnitSystem);
        txtUnitSystemLabel = findViewById(R.id.txtUnitSystemLabel);
        btnResetRecipes = findViewById(R.id.btnResetRecipes);
        btnClearPantry = findViewById(R.id.btnClearPantry);

        toolbar.setNavigationOnClickListener(v -> finish());

        boolean isImperial = "Imperial".equalsIgnoreCase(preferences.getString(KEY_UNIT_SYSTEM, "Metric"));
        switchUnitSystem.setChecked(isImperial);
        updateUnitLabel(isImperial);

        switchUnitSystem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String system = isChecked ? "Imperial" : "Metric";
            preferences.edit().putString(KEY_UNIT_SYSTEM, system).apply();
            updateUnitLabel(isChecked);
        });

        btnResetRecipes.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Reset Recipes")
                    .setMessage("Are you sure you want to reset all recipes to default list?")
                    .setPositiveButton("Reset", (dialog, which) -> {
                        dbHelper.resetDefaultRecipes();
                        Toast.makeText(this, "Default recipes restored!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnClearPantry.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Clear Pantry")
                    .setMessage("Are you sure you want to delete all ingredients from your pantry?")
                    .setPositiveButton("Clear All", (dialog, which) -> {
                        dbHelper.clearAllIngredients();
                        Toast.makeText(this, "Pantry ingredients cleared!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void updateUnitLabel(boolean isImperial) {
        if (isImperial) {
            txtUnitSystemLabel.setText("Current: Imperial (oz, lbs, cups)");
        } else {
            txtUnitSystemLabel.setText("Current: Metric (g, kg, ml, L, pcs)");
        }
    }
}
