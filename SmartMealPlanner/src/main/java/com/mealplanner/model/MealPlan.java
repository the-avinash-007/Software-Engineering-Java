package com.mealplanner.model;
//MealPlan.java
//Written by Sudarshan Londhe.
//This class manages the weekly meal plan.
//I used a HashMap here because we need to quickly look up
//which recipe is assigned to a specific day without looping
//through the entire list every time.

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a weekly meal plan mapping days to recipes.
 */
public class MealPlan {
	// enum for days of the week so we avoid typos and invalid day names
	// each day has a display name for showing in the UI
    public enum Day {
        MONDAY("Monday"),
        TUESDAY("Tuesday"),
        WEDNESDAY("Wednesday"),
        THURSDAY("Thursday"),
        FRIDAY("Friday"),
        SATURDAY("Saturday"),
        SUNDAY("Sunday");

        private final String displayName;

        Day(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private Map<Day, Recipe> meals;

    public MealPlan() {
        this.meals = new HashMap<>();
    }

    public void assignRecipe(Day day, Recipe recipe) {
    	// simply overwrite if a recipe is already assigned to that day.
        // this makes it easy to change your mind about a meal.
        meals.put(day, recipe);
    }

    public void removeRecipe(Day day) {
        meals.remove(day);
    }

    public Recipe getRecipe(Day day) {
        return meals.get(day);
    }

    public Map<Day, Recipe> getAllMeals() {
        return meals;
    }

    public boolean hasRecipe(Day day) {
        return meals.containsKey(day) && meals.get(day) != null;
    }

    public void clearAll() {
    	// wipe the entire week so the user can start fresh.
        meals.clear();
    }

    public int getAssignedCount() {
        return meals.size();
    }
}
