// GroceryListService.java
// Written by Shivam Singh.
// This is one of the core features of the application.
// It takes the weekly meal plan and generates a smart grocery list
// by merging duplicate ingredients and grouping them by category.
// For example if Monday needs 2 cloves of garlic and Wednesday needs
// 1 clove, the list will show 3 cloves total instead of two separate entries.

package com.mealplanner.service;

import com.mealplanner.model.GroceryItem;
import com.mealplanner.model.Ingredient;
import com.mealplanner.model.MealPlan;
import com.mealplanner.model.Recipe;

import java.util.*;

/**
 * Generates a combined grocery list from the weekly meal plan.
 * Merges duplicate ingredients and groups them by category (REQ3, REQ4).
 */
public class GroceryListService {

    /**
     * Build a categorized grocery list from the current meal plan.
     * Ingredients with the same name AND unit are merged (quantities summed).
     *
	> main method that builds the categorized grocery list
	> first we merge all duplicate ingredients, then we group by category
	> the category order is fixed so the list always looks consistent
	 */
	public Map<String, List<GroceryItem>> generateGroceryList(MealPlan mealPlan) {
        // Merge ingredients: key = "name|unit"
		// key format is "name|unit" so that garlic in cloves and garlic in grams
		// are treated as different items and not incorrectly merged together.
        Map<String, GroceryItem> merged = new LinkedHashMap<>();

        for (Map.Entry<MealPlan.Day, Recipe> entry : mealPlan.getAllMeals().entrySet()) {
            Recipe recipe = entry.getValue();
            if (recipe == null) continue;

            for (Ingredient ing : recipe.getIngredients()) {
                String key = ing.getName().toLowerCase().trim() + "|" + ing.getUnit().toLowerCase().trim();
                if (merged.containsKey(key)) {
                    merged.get(key).addQuantity(ing.getQuantity());
                } else {
                    merged.put(key, new GroceryItem(
                            capitalise(ing.getName()),
                            ing.getQuantity(),
                            ing.getUnit(),
                            ing.getCategory()
                    ));
                }
            }
        }

        // Group by category
        Map<String, List<GroceryItem>> grouped = new LinkedHashMap<>();
        // Predefined category order for nicer display
     // defining category order manually so Vegetables always appears first
     // and Other always appears last - makes the list easier to read in the store
        List<String> categoryOrder = Arrays.asList(
                "Vegetables", "Proteins", "Dairy", "Grains", "Other");

        for (String cat : categoryOrder) {
            grouped.put(cat, new ArrayList<>());
        }

        for (GroceryItem item : merged.values()) {
            String cat = item.getCategory();
            grouped.computeIfAbsent(cat, k -> new ArrayList<>()).add(item);
        }

        // Remove empty categories
        grouped.entrySet().removeIf(e -> e.getValue().isEmpty());

        return grouped;
    }

    /**
     * Flat list of all grocery items (sorted by category then name).
	> simplified version that returns all items in one flat list
	> used when we need to export or copy the grocery list
	*/
    public List<GroceryItem> generateFlatList(MealPlan mealPlan) {
        Map<String, List<GroceryItem>> grouped = generateGroceryList(mealPlan);
        List<GroceryItem> flat = new ArrayList<>();
        for (List<GroceryItem> items : grouped.values()) {
            flat.addAll(items);
        }
        return flat;
    }
 // small helper to capitalise ingredient names consistently
 // so we don't end up with both "garlic" and "Garlic" in the list.
    private String capitalise(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
