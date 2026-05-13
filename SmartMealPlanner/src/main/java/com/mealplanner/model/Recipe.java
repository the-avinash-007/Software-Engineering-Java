package com.mealplanner.model;

// This class is created by Sudarshan Londhe. 
// It represents a single recipe in out Smart Meal Planner application.  
// I decided to store the prep time in minutes since it makes it easier to display and compare recipes later on.


import java.util.List;
import java.util.ArrayList;

/**
 * Represents a recipe with its name, ingredients, category, and instructions.
 */
public class Recipe {
    private int id;
    private String name;
    private String category;
    private String description;
    private List<Ingredient> ingredients;
    private String instructions;
    private int prepTimeMinutes;

    public Recipe() {
    	// setting an empty list by default so we never get a NullPointerException.
    	// when someone tries to access ingredients before adding any.
        this.ingredients = new ArrayList<>();
    }

    public Recipe(int id, String name, String category, String description,
                  List<Ingredient> ingredients, String instructions, int prepTimeMinutes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        this.instructions = instructions;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public int getPrepTimeMinutes() { return prepTimeMinutes; }
    public void setPrepTimeMinutes(int prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }

    @Override
    public String toString() {
    	// Just returning the name here since that's all we need when displaying recipes in the last list view.
        return this.name;
    }
}
