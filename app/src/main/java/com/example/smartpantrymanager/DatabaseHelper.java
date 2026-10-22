package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PANTRY = "pantry_ingredients";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Pantry Columns
    public static final String COLUMN_PANTRY_ID = "id";
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QUANTITY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";

    // Recipe Columns
    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_TITLE = "title";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";
    public static final String COLUMN_RECIPE_CATEGORY = "category";
    public static final String COLUMN_RECIPE_PREP_TIME = "prep_time";

    // Recipe Ingredient Columns
    public static final String COLUMN_RI_ID = "id";
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_NAME = "name";
    public static final String COLUMN_RI_QUANTITY = "quantity";
    public static final String COLUMN_RI_UNIT = "unit";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                COLUMN_PANTRY_QUANTITY + " REAL NOT NULL, " +
                COLUMN_PANTRY_UNIT + " TEXT NOT NULL)";

        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_TITLE + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL, " +
                COLUMN_RECIPE_CATEGORY + " TEXT, " +
                COLUMN_RECIPE_PREP_TIME + " INTEGER)";

        String CREATE_RI_TABLE = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COLUMN_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COLUMN_RI_NAME + " TEXT NOT NULL, " +
                COLUMN_RI_QUANTITY + " REAL NOT NULL, " +
                COLUMN_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") ON DELETE CASCADE)";

        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RI_TABLE);

        seedDefaultRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // --- Pantry Ingredient Operations ---

    public long addIngredient(Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, ingredient.getName().trim());
        values.put(COLUMN_PANTRY_QUANTITY, ingredient.getQuantity());
        values.put(COLUMN_PANTRY_UNIT, ingredient.getUnit().trim());

        long id = db.insert(TABLE_PANTRY, null, values);
        ingredient.setId(id);
        return id;
    }

    public int updateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, ingredient.getName().trim());
        values.put(COLUMN_PANTRY_QUANTITY, ingredient.getQuantity());
        values.put(COLUMN_PANTRY_UNIT, ingredient.getUnit().trim());

        return db.update(TABLE_PANTRY, values, COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(ingredient.getId())});
    }

    public int deleteIngredient(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PANTRY, COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COLUMN_PANTRY_NAME + " ASC");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME));
                    double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QUANTITY));
                    String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT));
                    list.add(new Ingredient(id, name, quantity, unit));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    public Ingredient getIngredientById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Ingredient ingredient = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT));
                ingredient = new Ingredient(id, name, quantity, unit);
            }
            cursor.close();
        }
        return ingredient;
    }

    public void clearAllIngredients() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, null, null);
    }

    // --- Recipe Operations ---

    public long addRecipe(Recipe recipe) {
        return addRecipeToDb(this.getWritableDatabase(), recipe);
    }

    private long addRecipeToDb(SQLiteDatabase db, Recipe recipe) {
        db.beginTransaction();
        long recipeId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECIPE_TITLE, recipe.getTitle());
            values.put(COLUMN_RECIPE_INSTRUCTIONS, recipe.getInstructions());
            values.put(COLUMN_RECIPE_CATEGORY, recipe.getCategory());
            values.put(COLUMN_RECIPE_PREP_TIME, recipe.getPrepTimeMinutes());

            recipeId = db.insert(TABLE_RECIPES, null, values);
            recipe.setId(recipeId);

            if (recipe.getIngredients() != null) {
                for (RecipeIngredient ri : recipe.getIngredients()) {
                    ContentValues riValues = new ContentValues();
                    riValues.put(COLUMN_RI_RECIPE_ID, recipeId);
                    riValues.put(COLUMN_RI_NAME, ri.getName().trim());
                    riValues.put(COLUMN_RI_QUANTITY, ri.getQuantity());
                    riValues.put(COLUMN_RI_UNIT, ri.getUnit().trim());
                    long riId = db.insert(TABLE_RECIPE_INGREDIENTS, null, riValues);
                    ri.setId(riId);
                    ri.setRecipeId(recipeId);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return recipeId;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COLUMN_RECIPE_TITLE + " ASC");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_TITLE));
                    String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_CATEGORY));
                    int prepTime = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_PREP_TIME));

                    List<RecipeIngredient> riList = getRecipeIngredients(id);
                    recipes.add(new Recipe(id, title, instructions, category, prepTime, riList));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return recipes;
    }

    public Recipe getRecipeById(long recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}, null, null, null);

        Recipe recipe = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_TITLE));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_CATEGORY));
                int prepTime = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_PREP_TIME));

                List<RecipeIngredient> riList = getRecipeIngredients(recipeId);
                recipe = new Recipe(recipeId, title, instructions, category, prepTime, riList);
            }
            cursor.close();
        }
        return recipe;
    }

    public List<RecipeIngredient> getRecipeIngredients(long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, COLUMN_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RI_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_NAME));
                    double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RI_QUANTITY));
                    String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_UNIT));
                    list.add(new RecipeIngredient(id, recipeId, name, quantity, unit));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    /**
     * Suggests recipes ONLY when ALL required ingredients exist in the pantry with sufficient quantity.
     */
    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> allRecipes = getAllRecipes();
        List<Ingredient> pantryList = getAllIngredients();

        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> reqIngredients = recipe.getIngredients();
            if (reqIngredients == null || reqIngredients.isEmpty()) {
                continue;
            }

            boolean canMake = true;
            for (RecipeIngredient req : reqIngredients) {
                boolean foundAndEnough = false;
                for (Ingredient pantry : pantryList) {
                    if (pantry.getName().trim().equalsIgnoreCase(req.getName().trim())) {
                        if (pantry.getQuantity() >= req.getQuantity()) {
                            foundAndEnough = true;
                            break;
                        }
                    }
                }
                if (!foundAndEnough) {
                    canMake = false;
                    break;
                }
            }

            if (canMake) {
                suggested.add(recipe);
            }
        }

        return suggested;
    }

    public boolean cookRecipe(long recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        if (recipe == null) return false;

        List<Ingredient> pantryList = getAllIngredients();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (RecipeIngredient req : recipe.getIngredients()) {
                for (Ingredient pantry : pantryList) {
                    if (pantry.getName().trim().equalsIgnoreCase(req.getName().trim())) {
                        double remaining = pantry.getQuantity() - req.getQuantity();
                        if (remaining <= 0) {
                            db.delete(TABLE_PANTRY, COLUMN_PANTRY_ID + " = ?", new String[]{String.valueOf(pantry.getId())});
                        } else {
                            ContentValues cv = new ContentValues();
                            cv.put(COLUMN_PANTRY_QUANTITY, remaining);
                            db.update(TABLE_PANTRY, cv, COLUMN_PANTRY_ID + " = ?", new String[]{String.valueOf(pantry.getId())});
                        }
                        break;
                    }
                }
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public void resetDefaultRecipes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DELETE FROM " + TABLE_RECIPES);
        seedDefaultRecipes(db);
    }

    private void seedDefaultRecipes(SQLiteDatabase db) {
        List<Recipe> defaultRecipes = new ArrayList<>();

        defaultRecipes.add(new Recipe("Pancakes",
                "1. In a large bowl, mix flour and sugar.\n2. Whisk in milk, egg, and melted butter until smooth.\n3. Heat a skillet over medium heat.\n4. Pour 1/4 cup batter for each pancake.\n5. Cook until golden brown on both sides.",
                "Breakfast", 15,
                Arrays.asList(
                        new RecipeIngredient("Flour", 200, "g"),
                        new RecipeIngredient("Milk", 250, "ml"),
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Sugar", 20, "g"),
                        new RecipeIngredient("Butter", 15, "g")
                )));

        defaultRecipes.add(new Recipe("Omelette",
                "1. Whisk eggs, milk, and salt in a bowl.\n2. Melt butter in a non-stick skillet over medium heat.\n3. Pour in eggs and cook gently until set.\n4. Sprinkle cheese on top, fold, and serve.",
                "Breakfast", 10,
                Arrays.asList(
                        new RecipeIngredient("Egg", 3, "pcs"),
                        new RecipeIngredient("Milk", 30, "ml"),
                        new RecipeIngredient("Salt", 2, "g"),
                        new RecipeIngredient("Butter", 10, "g"),
                        new RecipeIngredient("Cheese", 50, "g")
                )));

        defaultRecipes.add(new Recipe("Tomato Pasta",
                "1. Cook pasta in boiling salted water according to package instructions.\n2. Heat olive oil in a skillet and sauté minced garlic.\n3. Add chopped tomatoes and simmer for 10 minutes.\n4. Toss cooked pasta into the sauce and serve.",
                "Dinner", 20,
                Arrays.asList(
                        new RecipeIngredient("Pasta", 250, "g"),
                        new RecipeIngredient("Tomato", 4, "pcs"),
                        new RecipeIngredient("Garlic", 2, "pcs"),
                        new RecipeIngredient("Olive Oil", 20, "ml"),
                        new RecipeIngredient("Salt", 5, "g")
                )));

        defaultRecipes.add(new Recipe("Garlic Bread",
                "1. Preheat oven to 180°C (350°F).\n2. Mix softened butter with minced garlic and salt.\n3. Spread mixture over bread slices.\n4. Bake for 8-10 minutes until golden and crispy.",
                "Snack", 12,
                Arrays.asList(
                        new RecipeIngredient("Bread", 4, "pcs"),
                        new RecipeIngredient("Garlic", 2, "pcs"),
                        new RecipeIngredient("Butter", 30, "g"),
                        new RecipeIngredient("Salt", 1, "g")
                )));

        defaultRecipes.add(new Recipe("Fruit Smoothie",
                "1. Peel and slice banana.\n2. Combine banana, milk, honey, and yogurt in a blender.\n3. Blend on high until smooth and creamy.\n4. Serve immediately.",
                "Beverage", 5,
                Arrays.asList(
                        new RecipeIngredient("Banana", 1, "pcs"),
                        new RecipeIngredient("Milk", 200, "ml"),
                        new RecipeIngredient("Honey", 15, "g"),
                        new RecipeIngredient("Yogurt", 100, "g")
                )));

        defaultRecipes.add(new Recipe("Grilled Cheese Sandwich",
                "1. Butter one side of each bread slice.\n2. Place cheese between non-buttered sides.\n3. Heat skillet over medium heat and grill sandwich until bread is golden and cheese melts.",
                "Lunch", 10,
                Arrays.asList(
                        new RecipeIngredient("Bread", 2, "pcs"),
                        new RecipeIngredient("Cheese", 2, "pcs"),
                        new RecipeIngredient("Butter", 15, "g")
                )));

        defaultRecipes.add(new Recipe("Fried Rice",
                "1. Sauté garlic in oil.\n2. Scramble eggs in the pan.\n3. Add rice and soy sauce, stirring over high heat for 3-5 minutes.",
                "Lunch", 15,
                Arrays.asList(
                        new RecipeIngredient("Rice", 200, "g"),
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Soy Sauce", 15, "ml"),
                        new RecipeIngredient("Garlic", 1, "pcs")
                )));

        defaultRecipes.add(new Recipe("Scrambled Eggs",
                "1. Whisk eggs and salt.\n2. Melt butter in pan over low heat.\n3. Stir eggs gently until soft curds form.",
                "Breakfast", 5,
                Arrays.asList(
                        new RecipeIngredient("Egg", 2, "pcs"),
                        new RecipeIngredient("Butter", 10, "g"),
                        new RecipeIngredient("Salt", 1, "g")
                )));

        for (Recipe r : defaultRecipes) {
            addRecipeToDb(db, r);
        }
    }
}
