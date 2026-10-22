package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditIngrediantActivity extends AppCompatActivity {

    public static final String EXTRA_INGREDIENT_ID = "EXTRA_INGREDIENT_ID";

    private MaterialToolbar toolbar;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private AutoCompleteTextView spinnerUnit;
    private MaterialButton btnSave;
    private MaterialButton btnDelete;

    private DatabaseHelper dbHelper;
    private long ingredientId = -1;
    private Ingredient existingIngredient;

    private static final String[] UNITS = new String[]{
            "pcs", "g", "kg", "ml", "L", "cups", "tbsp", "tsp", "oz", "lbs"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingrediant);

        dbHelper = DatabaseHelper.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        toolbar.setNavigationOnClickListener(v -> finish());

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, UNITS);
        spinnerUnit.setAdapter(unitAdapter);

        if (getIntent().hasExtra(EXTRA_INGREDIENT_ID)) {
            ingredientId = getIntent().getLongExtra(EXTRA_INGREDIENT_ID, -1);
        }

        if (ingredientId > 0) {
            existingIngredient = dbHelper.getIngredientById(ingredientId);
        }

        if (existingIngredient != null) {
            toolbar.setTitle("Edit Ingredient");
            editName.setText(existingIngredient.getName());

            String formattedQty = String.format("%.2f", existingIngredient.getQuantity());
            if (formattedQty.endsWith(".00")) {
                formattedQty = String.format("%.0f", existingIngredient.getQuantity());
            }
            editQuantity.setText(formattedQty);

            spinnerUnit.setText(existingIngredient.getUnit(), false);
            btnDelete.setVisibility(android.view.View.VISIBLE);
        } else {
            toolbar.setTitle("Add Ingredient");
            spinnerUnit.setText(UNITS[0], false);
            btnDelete.setVisibility(android.view.View.GONE);
        }

        btnSave.setOnClickListener(v -> saveIngredient());

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Ingredient")
                    .setMessage("Are you sure you want to delete this ingredient?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (existingIngredient != null) {
                            dbHelper.deleteIngredient(existingIngredient.getId());
                            Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void saveIngredient() {
        String name = editName.getText() != null ? editName.getText().toString().trim() : "";
        String qtyStr = editQuantity.getText() != null ? editQuantity.getText().toString().trim() : "";
        String unit = spinnerUnit.getText() != null ? spinnerUnit.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            editName.setError("Please enter ingredient name");
            editName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(qtyStr)) {
            editQuantity.setError("Please enter quantity");
            editQuantity.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                editQuantity.setError("Quantity must be greater than 0");
                editQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editQuantity.setError("Invalid number format");
            editQuantity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(unit)) {
            unit = "pcs";
        }

        if (existingIngredient != null) {
            existingIngredient.setName(name);
            existingIngredient.setQuantity(quantity);
            existingIngredient.setUnit(unit);
            dbHelper.updateIngredient(existingIngredient);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        } else {
            Ingredient newIngredient = new Ingredient(name, quantity, unit);
            dbHelper.addIngredient(newIngredient);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
