package com.mealplanner.data;

//RecipeDatabase.java
//Written by Avinash Shandilya.
//This class is responsible for two things:
//1. Loading all recipes from the CSV file when the app starts
//2. Saving and loading the weekly meal plan using a local SQLite database
//I chose SQLite because it's lightweight and works offline which fits perfectly with our desktop application design.

import com.mealplanner.model.Ingredient;
import com.mealplanner.model.Recipe;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages recipe data. Loads from CSV (bundled resource) and persists the meal plan in SQLite.
 */
public class RecipeDatabase {
    private static final Logger logger = Logger.getLogger(RecipeDatabase.class.getName());
    private static final String DB_PATH = "meal_planner.db";
    private static final String CSV_RESOURCE = "/recipes.csv";

    private Connection connection;
    private List<Recipe> recipeCache;

    public RecipeDatabase() {
        recipeCache = new ArrayList<>();
        initDatabase();
        loadRecipesFromCSV();
    }

    // -----------------------------------------------------------------------
    // Database Initialization
    // -----------------------------------------------------------------------

 // sets up the SQLite database connection and creates the meal_plan table
 // if it doesn't exist yet - this runs every time the app starts.
    private void initDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            createTables();
            logger.info("SQLite database connected: " + DB_PATH);
        } catch (SQLException e) {
            logger.severe("Failed to connect to database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String createMealPlan = """
                CREATE TABLE IF NOT EXISTS meal_plan (
                    day TEXT PRIMARY KEY,
                    recipe_id INTEGER,
                    recipe_name TEXT
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createMealPlan);
        }
    }

    // -----------------------------------------------------------------------
    // CSV Loading
    // -----------------------------------------------------------------------
    
 // reads all recipes from the bundled CSV file in resources
 // if the file is missing for some reason, we fall back to
 // the hardcoded default recipes so the app never starts empty
    
    private void loadRecipesFromCSV() {
        InputStream is = getClass().getResourceAsStream(CSV_RESOURCE);
        if (is == null) {
            logger.warning("recipes.csv not found in resources — loading built-in defaults.");
            loadDefaultRecipes();
            return;
        }

        try (CSVParser parser = CSVParser.parse(is, StandardCharsets.UTF_8,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {
            for (CSVRecord record : parser) {
                try {
                    int id = Integer.parseInt(record.get("id"));
                    String name = record.get("name");
                    String category = record.get("category");
                    String description = record.get("description");
                    String instructions = record.get("instructions");
                    int prepTime = Integer.parseInt(record.get("prep_time"));
                    String ingredientsRaw = record.get("ingredients");
                    List<Ingredient> ingredients = parseIngredients(ingredientsRaw);

                    recipeCache.add(new Recipe(id, name, category, description,
                            ingredients, instructions, prepTime));
                } catch (Exception e) {
                    logger.warning("Skipping malformed CSV row: " + e.getMessage());
                }
            }
            logger.info("Loaded " + recipeCache.size() + " recipes from CSV.");
        } catch (IOException e) {
            logger.severe("Error reading CSV: " + e.getMessage());
            loadDefaultRecipes();
        }
    }

    /**
     * Parses ingredient string format: "name:qty:unit:category|name:qty:unit:category|..."
     */
    
 // each ingredient in the CSV is stored as a pipe-separated string
 // format is: name:quantity:unit:category|name:quantity:unit:category
 // for example: Garlic:2:cloves:Vegetables|Olive Oil:2:tbsp:Other
    
    private List<Ingredient> parseIngredients(String raw) {
        List<Ingredient> list = new ArrayList<>();
        if (raw == null || raw.isBlank()) return list;
        String[] parts = raw.split("\\|");
        for (String part : parts) {
            String[] fields = part.trim().split(":");
            if (fields.length >= 4) {
                try {
                    String iName = fields[0].trim();
                    double qty = Double.parseDouble(fields[1].trim());
                    String unit = fields[2].trim();
                    String cat = fields[3].trim();
                    list.add(new Ingredient(iName, qty, unit, cat));
                } catch (NumberFormatException ignored) {}
            }
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Default Built-in Recipes (fallback if CSV missing)
    // -----------------------------------------------------------------------

 // backup recipes in case the CSV file fails to load
 // I added these manually so the app always has something to show
 // even if something goes wrong with the file reading
    
    private void loadDefaultRecipes() {
        recipeCache.addAll(Arrays.asList(
            buildRecipe(1, "Spaghetti Bolognese", "Pasta",
                "Classic Italian meat sauce pasta.",
                "Cook pasta. Brown minced beef, add tomato sauce, simmer 20 min. Serve over pasta.",
                30,
                new Ingredient("Spaghetti", 200, "g", "Grains"),
                new Ingredient("Minced Beef", 150, "g", "Proteins"),
                new Ingredient("Tomato Sauce", 200, "ml", "Vegetables"),
                new Ingredient("Onion", 1, "", "Vegetables"),
                new Ingredient("Garlic", 2, "cloves", "Vegetables"),
                new Ingredient("Olive Oil", 2, "tbsp", "Other"),
                new Ingredient("Parmesan", 30, "g", "Dairy")),

            buildRecipe(2, "Chicken Stir-Fry", "Chicken",
                "Quick and healthy Asian-style stir-fry.",
                "Slice chicken, stir-fry with vegetables and soy sauce for 10–12 min. Serve with rice.",
                20,
                new Ingredient("Chicken Breast", 200, "g", "Proteins"),
                new Ingredient("Bell Pepper", 1, "", "Vegetables"),
                new Ingredient("Broccoli", 150, "g", "Vegetables"),
                new Ingredient("Soy Sauce", 3, "tbsp", "Other"),
                new Ingredient("Garlic", 2, "cloves", "Vegetables"),
                new Ingredient("Ginger", 1, "tsp", "Other"),
                new Ingredient("Rice", 180, "g", "Grains")),

            buildRecipe(3, "Avocado Toast", "Breakfast",
                "Simple and nutritious breakfast or snack.",
                "Toast bread. Mash avocado with lemon juice. Spread on toast, season to taste.",
                10,
                new Ingredient("Bread", 2, "slices", "Grains"),
                new Ingredient("Avocado", 1, "", "Vegetables"),
                new Ingredient("Lemon Juice", 1, "tbsp", "Other"),
                new Ingredient("Salt", 1, "pinch", "Other"),
                new Ingredient("Chili Flakes", 0.5, "tsp", "Other")),

            buildRecipe(4, "Greek Salad", "Salad",
                "Fresh Mediterranean salad.",
                "Chop vegetables, combine with olives and feta. Drizzle with olive oil and oregano.",
                15,
                new Ingredient("Tomato", 2, "", "Vegetables"),
                new Ingredient("Cucumber", 1, "", "Vegetables"),
                new Ingredient("Red Onion", 0.5, "", "Vegetables"),
                new Ingredient("Feta Cheese", 80, "g", "Dairy"),
                new Ingredient("Olives", 50, "g", "Other"),
                new Ingredient("Olive Oil", 2, "tbsp", "Other"),
                new Ingredient("Oregano", 1, "tsp", "Other")),

            buildRecipe(5, "Vegetable Curry", "Vegetarian",
                "Rich and warming Indian-inspired curry.",
                "Sauté onion and garlic. Add curry paste, chickpeas, vegetables and coconut milk. Simmer 20 min.",
                35,
                new Ingredient("Chickpeas", 400, "g", "Proteins"),
                new Ingredient("Coconut Milk", 400, "ml", "Dairy"),
                new Ingredient("Spinach", 100, "g", "Vegetables"),
                new Ingredient("Tomato", 2, "", "Vegetables"),
                new Ingredient("Onion", 1, "", "Vegetables"),
                new Ingredient("Curry Paste", 2, "tbsp", "Other"),
                new Ingredient("Rice", 200, "g", "Grains")),

            buildRecipe(6, "Pancakes", "Breakfast",
                "Fluffy breakfast pancakes.",
                "Mix flour, egg, milk into batter. Cook on pan until bubbles form, flip, cook 1 min more.",
                20,
                new Ingredient("Flour", 200, "g", "Grains"),
                new Ingredient("Egg", 2, "", "Proteins"),
                new Ingredient("Milk", 200, "ml", "Dairy"),
                new Ingredient("Butter", 30, "g", "Dairy"),
                new Ingredient("Sugar", 2, "tbsp", "Other")),

            buildRecipe(7, "Grilled Salmon", "Seafood",
                "Simple and healthy grilled salmon fillet.",
                "Season salmon. Grill 4 min each side. Serve with lemon and steamed vegetables.",
                20,
                new Ingredient("Salmon Fillet", 200, "g", "Proteins"),
                new Ingredient("Lemon", 1, "", "Other"),
                new Ingredient("Garlic", 1, "clove", "Vegetables"),
                new Ingredient("Broccoli", 200, "g", "Vegetables"),
                new Ingredient("Olive Oil", 1, "tbsp", "Other")),

            buildRecipe(8, "Tomato Soup", "Soup",
                "Classic creamy tomato soup.",
                "Sauté onion, add canned tomatoes and broth. Simmer 15 min, blend until smooth, add cream.",
                25,
                new Ingredient("Canned Tomatoes", 400, "g", "Vegetables"),
                new Ingredient("Onion", 1, "", "Vegetables"),
                new Ingredient("Vegetable Broth", 500, "ml", "Other"),
                new Ingredient("Heavy Cream", 100, "ml", "Dairy"),
                new Ingredient("Garlic", 2, "cloves", "Vegetables"),
                new Ingredient("Basil", 1, "tsp", "Other")),

            buildRecipe(9, "Omelette", "Breakfast",
                "Quick protein-packed omelette.",
                "Beat eggs. Pour into hot pan. Add fillings on one half, fold over. Cook 1 more minute.",
                10,
                new Ingredient("Egg", 3, "", "Proteins"),
                new Ingredient("Cheese", 40, "g", "Dairy"),
                new Ingredient("Bell Pepper", 0.5, "", "Vegetables"),
                new Ingredient("Mushrooms", 60, "g", "Vegetables"),
                new Ingredient("Butter", 10, "g", "Dairy")),

            buildRecipe(10, "Bean Tacos", "Mexican",
                "Quick vegetarian tacos with black beans.",
                "Heat beans with cumin and chili. Warm tortillas. Assemble with toppings.",
                15,
                new Ingredient("Black Beans", 400, "g", "Proteins"),
                new Ingredient("Tortillas", 4, "", "Grains"),
                new Ingredient("Avocado", 1, "", "Vegetables"),
                new Ingredient("Tomato", 1, "", "Vegetables"),
                new Ingredient("Cheese", 50, "g", "Dairy"),
                new Ingredient("Cumin", 1, "tsp", "Other"),
                new Ingredient("Lime", 1, "", "Other"))
        ));
    }

    private Recipe buildRecipe(int id, String name, String category, String desc,
                                String instructions, int prep, Ingredient... ingredients) {
        return new Recipe(id, name, category, desc, Arrays.asList(ingredients), instructions, prep);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    public List<Recipe> getAllRecipes() {
        return Collections.unmodifiableList(recipeCache);
    }

    public List<Recipe> getRecipesByCategory(String category) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipeCache) {
            if (r.getCategory().equalsIgnoreCase(category)) result.add(r);
        }
        return result;
    }

    public List<String> getAllCategories() {
        Set<String> cats = new LinkedHashSet<>();
        for (Recipe r : recipeCache) cats.add(r.getCategory());
        return new ArrayList<>(cats);
    }

    public Optional<Recipe> findById(int id) {
        return recipeCache.stream().filter(r -> r.getId() == id).findFirst();
    }

    // -----------------------------------------------------------------------
    // Meal Plan Persistence (SQLite)
    // -----------------------------------------------------------------------

 // saves a single day's meal assignment to the database
 // using INSERT OR REPLACE so we automatically handle updates
 // without needing a separate update query.
    
    public void saveMealPlanEntry(String day, int recipeId, String recipeName) {
        String sql = "INSERT OR REPLACE INTO meal_plan(day, recipe_id, recipe_name) VALUES(?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, day);
            ps.setInt(2, recipeId);
            ps.setString(3, recipeName);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error saving meal plan entry: " + e.getMessage());
        }
    }

    public void deleteMealPlanEntry(String day) {
        String sql = "DELETE FROM meal_plan WHERE day = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, day);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error deleting meal plan entry: " + e.getMessage());
        }
    }
    
 // loads the previously saved meal plan from the database
 // called on startup so the user's plan is restored automatically.
    
    public Map<String, Integer> loadSavedMealPlan() {
        Map<String, Integer> saved = new HashMap<>();
        String sql = "SELECT day, recipe_id FROM meal_plan";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                saved.put(rs.getString("day"), rs.getInt("recipe_id"));
            }
        } catch (SQLException e) {
            logger.severe("Error loading meal plan: " + e.getMessage());
        }
        return saved;
    }

    public void clearMealPlan() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM meal_plan");
        } catch (SQLException e) {
            logger.severe("Error clearing meal plan: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            logger.warning("Error closing database: " + e.getMessage());
        }
    }
}
