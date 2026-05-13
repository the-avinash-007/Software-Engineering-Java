package com.mealplanner.model;

//GroceryItem.java
//Written by Sudarshan Londhe.
//This class represents a single item on the generated grocery list.
//The reason I added an addQuantity() method is because the same ingredient
//can appear in multiple recipes - so instead of listing garlic twice,
//we merge them into one item and add the quantities together.
public class GroceryItem {
    private String name;
    private double totalQuantity;
    private String unit;
    private String category;
    private boolean checked;
    
 // Initializing checked as false since nothing is bought yet when the list is first created
    public GroceryItem(String name, double totalQuantity, String unit, String category) {
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.unit = unit;
        this.category = category;
        this.checked = false;
    }
 // called when the same ingredient appears in more than one recipe
 // we just add to the existing quantity rather than creating a duplicate entry
    public void addQuantity(double amount) {
        this.totalQuantity += amount;
    }

    public String getName() { return name; }
    public double getTotalQuantity() { return totalQuantity; }
    public String getUnit() { return unit; }
    public String getCategory() { return category; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }

// formats the item for display on the grocery list
// shows unit only if it exists - some items like eggs don't have one
    public String getDisplayText() {
        if (unit != null && !unit.isEmpty()) {
            return String.format("%s — %.1f %s", name, totalQuantity, unit);
        }
        return String.format("%s — %.0f", name, totalQuantity);
    }

    @Override
    public String toString() {
        return getDisplayText();
    }
}
