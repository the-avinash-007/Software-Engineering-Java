package com.mealplanner.service;

//MealPlanService.java
//Written by Shivam Singh.
//This class acts as the bridge between the UI and the database
//for all meal plan related operations.
//I kept the business logic here separate from the UI so that
//if we ever change the interface, the core logic stays the same.

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

 // when the service starts up we immediately load any previously
 // saved meal plan from the database so the user never loses their plan.
    public MealPlanService(RecipeDatabase db) {
        this.db = db;
        this.currentPlan = new MealPlan();
        loadFromDatabase();
    }

    public MealPlan getMealPlan() {
        return currentPlan;
    }

 // assigns a recipe to a specific day and immediately saves it
 // to the database so it persists even if the app is closed.

    public void assignRecipe(MealPlan.Day day, Recipe recipe) {
        currentPlan.assignRecipe(day, recipe);
        db.saveMealPlanEntry(day.name(), recipe.getId(), recipe.getName());
    }

 // removes a recipe from a day - both from memory and the database
 // so the removal is permanent across sessions.
    public void removeRecipe(MealPlan.Day day) {
        currentPlan.removeRecipe(day);
        db.deleteMealPlanEntry(day.name());
    }

 // clears the entire week at once - useful when the user wants
 // to start their meal planning from scratch
    public void clearAll() {
        currentPlan.clearAll();
        db.clearMealPlan();
    }

 // this method runs on startup and restores the saved meal plan
 // I used a try-catch around the Day.valueOf() call because if
 // someone manually edits the database with a wrong day name
 // it shouldn't crash the whole application.
    
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
