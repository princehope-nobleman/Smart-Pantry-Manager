package com.example.smartpantrymanager;

import java.util.List;

public class Recipe {
    private long id;
    private String title;
    private String instructions;
    private String category;
    private int prepTimeMinutes;
    private List<RecipeIngredient> ingredients;

    public Recipe(long id, String title, String instructions, String category, int prepTimeMinutes, List<RecipeIngredient> ingredients) {
        this.id = id;
        this.title = title;
        this.instructions = instructions;
        this.category = category;
        this.prepTimeMinutes = prepTimeMinutes;
        this.ingredients = ingredients;
    }

    public Recipe(String title, String instructions, String category, int prepTimeMinutes, List<RecipeIngredient> ingredients) {
        this(-1, title, instructions, category, prepTimeMinutes, ingredients);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getCategory() {
        return category;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
}
