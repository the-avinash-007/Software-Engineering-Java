package com.mealplanner.service;

import com.mealplanner.data.RecipeDatabase;
import com.mealplanner.model.MealPlan;
import com.mealplanner.model.Recipe;

import java.util.Map;
import java.util.Optional;

/**
 * Service layer for meal plan operations.
 * Handles saving/loading to/from the SQLite database.
 */
public class MealPlanService {
    private final RecipeDatabase db;
    private final MealPlan currentPlan;

    public MealPlanService(RecipeDatabase db) {
        this.db = db;
        this.currentPlan = new MealPlan();
        loadFromDatabase();
    }

    public MealPlan getMealPlan() {
        return currentPlan;
    }

    public void assignRecipe(MealPlan.Day day, Recipe recipe) {
        currentPlan.assignRecipe(day, recipe);
        db.saveMealPlanEntry(day.name(), recipe.getId(), recipe.getName());
    }

    public void removeRecipe(MealPlan.Day day) {
        currentPlan.removeRecipe(day);
        db.deleteMealPlanEntry(day.name());
    }

    public void clearAll() {
        currentPlan.clearAll();
        db.clearMealPlan();
    }

    private void loadFromDatabase() {
        Map<String, Integer> saved = db.loadSavedMealPlan();
        for (Map.Entry<String, Integer> entry : saved.entrySet()) {
            try {
                MealPlan.Day day = MealPlan.Day.valueOf(entry.getKey());
                Optional<Recipe> recipe = db.findById(entry.getValue());
                recipe.ifPresent(r -> currentPlan.assignRecipe(day, r));
            } catch (IllegalArgumentException ignored) {
                // unknown day name in DB, skip
            }
        }
    }
}
