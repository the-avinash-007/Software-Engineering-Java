package com.mealplanner.model;

//Ingredient.java
//Written by Sudarshan Londhe.
//This class represents a single ingredient used in a recipe.
//Each ingredient has a name, quantity, unit and category.
//The category field is important because we use it later
//to group items on the grocery list (e.g. Vegetables, Dairy etc.)

public class Ingredient {
    private String name;
    private double quantity;
    private String unit;
    private String category; // e.g., Vegetables, Dairy, Proteins, Grains, Other

    public Ingredient() {}

    public Ingredient(String name, double quantity, String unit, String category) {
        this.name = name;
        this.quantity = quantity;
     // unit can be empty for countable items like eggs or bananas
        this.unit = unit;
     // category helps us sort the grocery list into sections later
        this.category = category;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // formats the ingredient nicely for display
 // if a unit exists we show it, otherwise just show the quantity as a whole number
    @Override
    public String toString() {
        if (unit != null && !unit.isEmpty()) {
            return String.format("%s (%.1f %s)", name, quantity, unit);
        }
        return String.format("%s (%.0f)", name, quantity);
    }
}
