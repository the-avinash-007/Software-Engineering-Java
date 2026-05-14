// MainController.java
// Written by Hassan Ahmed.
// This class builds the entire user interface for the Smart Meal Planner.
// I structured it around a 3-tab layout because each tab represents
// a distinct step in the user's workflow:
// Tab 1: Browse and discover recipes
// Tab 2: Build your weekly meal plan
// Tab 3: View and copy your grocery list
// The colour palette was chosen to feel fresh and food-related
// using greens as the primary colour throughout.

package com.mealplanner.ui;

import com.mealplanner.data.RecipeDatabase;
import com.mealplanner.model.GroceryItem;
import com.mealplanner.model.MealPlan;
import com.mealplanner.model.Recipe;
import com.mealplanner.service.GroceryListService;
import com.mealplanner.service.MealPlanService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

/**
 * Main UI controller for the Smart Meal Planner application.
 * Implements: REQ1 (browse recipes), REQ2 (assign to days),
 *             REQ3 (generate grocery list), REQ4 (grouped by category).
 */
public class MainController {

    // ----- Colour palette -----
    private static final String COLOR_BG       = "#F4F6F9";
    private static final String COLOR_SIDEBAR  = "#2D6A4F";
    private static final String COLOR_ACCENT   = "#52B788";
    private static final String COLOR_HEADER   = "#1B4332";
    private static final String COLOR_CARD     = "#FFFFFF";
    private static final String COLOR_TEXT     = "#1A1A2E";
    private static final String COLOR_MUTED    = "#6C757D";
    private static final String COLOR_DANGER   = "#E63946";

    private final RecipeDatabase database;
    private final MealPlanService mealPlanService;
    private final GroceryListService groceryListService;

    // UI state
    private String selectedCategory = "All";
    private Recipe selectedRecipe = null;

    // Day panels map
    private final Map<MealPlan.Day, Label> dayRecipeLabels = new LinkedHashMap<>();
    private final Map<MealPlan.Day, Button> dayRemoveButtons = new LinkedHashMap<>();

    // Recipe list
    private ListView<Recipe> recipeListView;
    private TextArea recipeDetailArea;
    private VBox groceryListContainer;
    private Label statusLabel;

    public MainController(RecipeDatabase database) {
        this.database = database;
        this.mealPlanService = new MealPlanService(database);
        this.groceryListService = new GroceryListService();
    }

    public Scene buildScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // Header
        root.setTop(buildHeader());

        // Sidebar
        root.setLeft(buildSidebar());

        // Center: TabPane with 3 tabs
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: " + COLOR_BG + ";");

        Tab browseTab = new Tab("🍽  Browse Recipes", buildBrowseTab());
        Tab planTab   = new Tab("📅  Weekly Plan",    buildPlanTab());
        Tab groceryTab= new Tab("🛒  Grocery List",   buildGroceryTab());

        tabs.getTabs().addAll(browseTab, planTab, groceryTab);
        root.setCenter(tabs);

        // Status bar
        statusLabel = new Label("Ready — " + database.getAllRecipes().size() + " recipes loaded.");
        statusLabel.setStyle("-fx-padding: 4 12; -fx-text-fill: " + COLOR_MUTED + ";");
        root.setBottom(statusLabel);

        refreshPlanTab();
        return new Scene(root, 1100, 680);
    }

    // -----------------------------------------------------------------------
    // Header
    // -----------------------------------------------------------------------
 // builds the top header bar with the app title
 // kept it simple with just the title and a subtitle on the right.
    
    private HBox buildHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + COLOR_HEADER + "; -fx-padding: 14 20;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🥗 Smart Meal Planner");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label subtitle = new Label("Weekly Planning · Grocery Automation");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web("#95D5B2"));

        header.getChildren().addAll(title, spacer, subtitle);
        return header;
    }

    // -----------------------------------------------------------------------
    // Sidebar — category filter
    // -----------------------------------------------------------------------
 // the sidebar shows category filter buttons
 // clicking a category filters the recipe list in Tab 1
 // I used ToggleButtons so only one category can be selected at a time.
    
    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setStyle("-fx-background-color: " + COLOR_SIDEBAR + "; -fx-padding: 16 10;");
        sidebar.setPrefWidth(150);

        Label catLabel = new Label("CATEGORIES");
        catLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        catLabel.setTextFill(Color.web("#95D5B2"));
        catLabel.setPadding(new Insets(0, 0, 8, 6));

        sidebar.getChildren().add(catLabel);

        List<String> cats = new ArrayList<>();
        cats.add("All");
        cats.addAll(database.getAllCategories());

        ToggleGroup tg = new ToggleGroup();
        for (String cat : cats) {
            ToggleButton btn = new ToggleButton(cat);
            btn.setToggleGroup(tg);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle(sidebarBtnStyle(false));
            btn.selectedProperty().addListener((obs, o, n) ->
                    btn.setStyle(sidebarBtnStyle(n)));
            if (cat.equals("All")) btn.setSelected(true);
            btn.setOnAction(e -> {
                selectedCategory = cat;
                refreshRecipeList();
            });
            sidebar.getChildren().add(btn);
        }
        return sidebar;
    }

    private String sidebarBtnStyle(boolean selected) {
        if (selected) {
            return "-fx-background-color: " + COLOR_ACCENT + "; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 7 12; -fx-cursor: hand;";
        }
        return "-fx-background-color: transparent; -fx-text-fill: #D8F3DC; " +
                "-fx-background-radius: 6; -fx-padding: 7 12; -fx-cursor: hand;";
    }

    // -----------------------------------------------------------------------
    // Tab 1: Browse Recipes (REQ1)
    // -----------------------------------------------------------------------
 // split into two panels - recipe list on the left, details on the right
 // the assign section at the bottom right lets users add recipes to their plan.
    
    private SplitPane buildBrowseTab() {
        // Left: recipe list
        recipeListView = new ListView<>();
        recipeListView.setStyle("-fx-background-color: " + COLOR_CARD + ";");
        refreshRecipeList();
        recipeListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, recipe) -> showRecipeDetail(recipe));

        VBox leftBox = new VBox(8);
        leftBox.setPadding(new Insets(12));
        leftBox.setStyle("-fx-background-color: " + COLOR_BG + ";");

        Label listTitle = sectionLabel("Recipes");
        leftBox.getChildren().addAll(listTitle, recipeListView);
        VBox.setVgrow(recipeListView, Priority.ALWAYS);

        // Right: detail + assign panel
        recipeDetailArea = new TextArea();
        recipeDetailArea.setWrapText(true);
        recipeDetailArea.setEditable(false);
        recipeDetailArea.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13;");
        recipeDetailArea.setText("← Select a recipe to see details.");

        // Day assignment dropdown + button
        ComboBox<MealPlan.Day> dayCombo = new ComboBox<>(
                FXCollections.observableArrayList(MealPlan.Day.values()));
        dayCombo.setPromptText("Select day…");
        dayCombo.setMaxWidth(Double.MAX_VALUE);

        Button assignBtn = styledButton("Assign to Day", COLOR_ACCENT, "white");
        assignBtn.setMaxWidth(Double.MAX_VALUE);
        assignBtn.setOnAction(e -> {
            Recipe recipe = recipeListView.getSelectionModel().getSelectedItem();
            MealPlan.Day day = dayCombo.getValue();
            if (recipe == null) { showStatus("Please select a recipe first."); return; }
            if (day == null)    { showStatus("Please select a day.");          return; }
            mealPlanService.assignRecipe(day, recipe);
            refreshPlanTab();
            showStatus("✅ " + recipe.getName() + " assigned to " + day + ".");
        });

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(12));
        rightBox.setStyle("-fx-background-color: " + COLOR_BG + ";");

        Label detailTitle = sectionLabel("Recipe Details");
        Label assignTitle = sectionLabel("Assign to Meal Plan");
        rightBox.getChildren().addAll(detailTitle, recipeDetailArea, assignTitle, dayCombo, assignBtn);
        VBox.setVgrow(recipeDetailArea, Priority.ALWAYS);

        SplitPane split = new SplitPane(leftBox, rightBox);
        split.setDividerPositions(0.38);
        return split;
    }

    private void refreshRecipeList() {
        List<Recipe> recipes = selectedCategory.equals("All")
                ? database.getAllRecipes()
                : database.getRecipesByCategory(selectedCategory);
        recipeListView.setItems(FXCollections.observableArrayList(recipes));
    }

    private void showRecipeDetail(Recipe recipe) {
        if (recipe == null) { recipeDetailArea.setText("Select a recipe."); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("📖 ").append(recipe.getName()).append("\n");
        sb.append("Category: ").append(recipe.getCategory())
          .append("   |   Prep time: ").append(recipe.getPrepTimeMinutes()).append(" min\n\n");
        sb.append(recipe.getDescription()).append("\n\n");
        sb.append("─── Ingredients ───\n");
        for (var ing : recipe.getIngredients()) {
            sb.append("  • ").append(ing.toString()).append("  [").append(ing.getCategory()).append("]\n");
        }
        sb.append("\n─── Instructions ───\n");
        sb.append(recipe.getInstructions());
        recipeDetailArea.setText(sb.toString());
    }

    // -----------------------------------------------------------------------
    // Tab 2: Weekly Plan (REQ2)
    // -----------------------------------------------------------------------

    private ScrollPane buildPlanTab() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(16));
        container.setStyle("-fx-background-color: " + COLOR_BG + ";");

        Label planTitle = sectionLabel("Weekly Meal Plan");
        container.getChildren().add(planTitle);

        // Day cards grid (2 columns)
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        MealPlan.Day[] days = MealPlan.Day.values();
        for (int i = 0; i < days.length; i++) {
            VBox card = buildDayCard(days[i]);
            grid.add(card, i % 2, i / 2);
            GridPane.setHgrow(card, Priority.ALWAYS);
        }
        container.getChildren().add(grid);

        // Bottom actions
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button clearBtn = styledButton("Clear Week", COLOR_DANGER, "white");
        clearBtn.setOnAction(e -> {
            mealPlanService.clearAll();
            refreshPlanTab();
            showStatus("Meal plan cleared.");
        });
        Button groceryBtn = styledButton("Generate Grocery List →", COLOR_HEADER, "white");
        groceryBtn.setOnAction(e -> {
            refreshGroceryList();
            showStatus("Grocery list generated from " + mealPlanService.getMealPlan().getAssignedCount() + " meals.");
        });
        actions.getChildren().addAll(clearBtn, groceryBtn);
        container.getChildren().add(actions);

        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + COLOR_BG + ";");
        return scroll;
    }
 // builds a single day card for the weekly plan view
 // the remove button is hidden by default and only appears
 // when a recipe has been assigned to that day. 
    
    private VBox buildDayCard(MealPlan.Day day) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: " + COLOR_CARD + "; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);");
        card.setMinWidth(300);

        Label dayLabel = new Label(day.getDisplayName().toUpperCase());
        dayLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        dayLabel.setStyle("-fx-text-fill: " + COLOR_HEADER + ";");

        Label recipeLabel = new Label("—  No meal assigned");
        recipeLabel.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 13;");
        recipeLabel.setWrapText(true);

        Button removeBtn = new Button("Remove");
        removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_DANGER +
                "; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 0;");
        removeBtn.setVisible(false);
        removeBtn.setOnAction(e -> {
            mealPlanService.removeRecipe(day);
            refreshPlanTab();
            showStatus(day.getDisplayName() + " cleared.");
        });

        dayRecipeLabels.put(day, recipeLabel);
        dayRemoveButtons.put(day, removeBtn);

        card.getChildren().addAll(dayLabel, recipeLabel, removeBtn);
        return card;
    }

    private void refreshPlanTab() {
        MealPlan plan = mealPlanService.getMealPlan();
        for (MealPlan.Day day : MealPlan.Day.values()) {
            Label lbl = dayRecipeLabels.get(day);
            Button btn = dayRemoveButtons.get(day);
            if (lbl == null) continue;
            Recipe r = plan.getRecipe(day);
            if (r != null) {
                lbl.setText("🍴 " + r.getName() + "  (" + r.getPrepTimeMinutes() + " min)");
                lbl.setStyle("-fx-text-fill: " + COLOR_TEXT + "; -fx-font-size: 13;");
                if (btn != null) btn.setVisible(true);
            } else {
                lbl.setText("—  No meal assigned");
                lbl.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 13;");
                if (btn != null) btn.setVisible(false);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Tab 3: Grocery List (REQ3, REQ4)
    // -----------------------------------------------------------------------
 // starts empty and gets populated when the user clicks
 // Generate Grocery List from the Weekly Plan tab.
    private ScrollPane buildGroceryTab() {
        groceryListContainer = new VBox(14);
        groceryListContainer.setPadding(new Insets(16));
        groceryListContainer.setStyle("-fx-background-color: " + COLOR_BG + ";");

        Label hint = new Label("Assign meals in the Weekly Plan tab, then click 'Generate Grocery List'.");
        hint.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-style: italic;");
        groceryListContainer.getChildren().add(hint);

        ScrollPane scroll = new ScrollPane(groceryListContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + COLOR_BG + ";");
        return scroll;
    }
 // generates and displays the grocery list grouped by category
 // each category gets its own card with a color coded header
 // items can be checked off as the user shops.
    private void refreshGroceryList() {
        groceryListContainer.getChildren().clear();

        MealPlan plan = mealPlanService.getMealPlan();
        if (plan.getAssignedCount() == 0) {
            Label empty = new Label("No meals planned yet — go to the Weekly Plan tab first.");
            empty.setStyle("-fx-text-fill: " + COLOR_MUTED + ";");
            groceryListContainer.getChildren().add(empty);
            return;
        }

        Label title = sectionLabel("Grocery List — Week Overview");
        groceryListContainer.getChildren().add(title);

        // Summary of planned meals
        HBox mealSummary = new HBox(8);
        mealSummary.setMaxHeight(Double.MAX_VALUE);
        for (Map.Entry<MealPlan.Day, Recipe> e : plan.getAllMeals().entrySet()) {
            Label chip = new Label(e.getKey().toString().substring(0, 3) + ": " + e.getValue().getName());
            chip.setStyle("-fx-background-color: #D8F3DC; -fx-text-fill: " + COLOR_HEADER +
                    "; -fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11;");
            mealSummary.getChildren().add(chip);
        }
        groceryListContainer.getChildren().add(mealSummary);

        // Categorized items
        Map<String, List<GroceryItem>> grouped = groceryListService.generateGroceryList(plan);

        for (Map.Entry<String, List<GroceryItem>> entry : grouped.entrySet()) {
            String category = entry.getKey();
            List<GroceryItem> items = entry.getValue();

            VBox catBox = new VBox(4);
            catBox.setPadding(new Insets(10));
            catBox.setStyle("-fx-background-color: " + COLOR_CARD + "; -fx-background-radius: 8;");

            Label catLabel = new Label(categoryEmoji(category) + "  " + category.toUpperCase());
            catLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            catLabel.setStyle("-fx-text-fill: " + COLOR_HEADER + ";");
            catBox.getChildren().add(catLabel);

            for (GroceryItem item : items) {
                CheckBox cb = new CheckBox(item.getDisplayText());
                cb.setStyle("-fx-font-size: 13; -fx-text-fill: " + COLOR_TEXT + ";");
                cb.selectedProperty().addListener((obs, o, n) ->
                        cb.setStyle("-fx-font-size: 13; -fx-text-fill: " + (n ? COLOR_MUTED : COLOR_TEXT) + ";"));
                catBox.getChildren().add(cb);
            }

            groceryListContainer.getChildren().add(catBox);
        }

        // Export button
        Button exportBtn = styledButton("📋  Copy to Clipboard", COLOR_SIDEBAR, "white");
        exportBtn.setOnAction(e -> copyGroceryToClipboard(grouped));
        groceryListContainer.getChildren().add(exportBtn);
    }
 // formats the grocery list as plain text and copies it to clipboard
 // useful if the user wants to paste it into their notes or messages app.
    private void copyGroceryToClipboard(Map<String, List<GroceryItem>> grouped) {
        StringBuilder sb = new StringBuilder("=== GROCERY LIST ===\n\n");
        for (Map.Entry<String, List<GroceryItem>> entry : grouped.entrySet()) {
            sb.append("[").append(entry.getKey().toUpperCase()).append("]\n");
            for (GroceryItem item : entry.getValue()) {
                sb.append("  □ ").append(item.getDisplayText()).append("\n");
            }
            sb.append("\n");
        }
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(sb.toString());
        clipboard.setContent(content);
        showStatus("Grocery list copied to clipboard!");
    }

    private String categoryEmoji(String cat) {
        return switch (cat) {
            case "Vegetables" -> "🥦";
            case "Proteins"   -> "🥩";
            case "Dairy"      -> "🧀";
            case "Grains"     -> "🌾";
            default           -> "🧺";
        };
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lbl.setStyle("-fx-text-fill: " + COLOR_HEADER + ";");
        return lbl;
    }

    private Button styledButton(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg +
                "; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }

    private void showStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }
}
