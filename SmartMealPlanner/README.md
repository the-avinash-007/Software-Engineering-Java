# Smart Meal Planner

A desktop application built with Java and JavaFX that helps users plan weekly meals and automatically generate grocery lists.

## Project Description

This application was implemented based on the Software Requirements Specification (SRS) provided for the Smart Meal Planner system. The system allows users to:

- **Browse available recipes** (REQ1) — filtered by category via the sidebar
- **Assign recipes to days of the week** (REQ2) — via drag-select or dropdown assignment
- **Generate a combined grocery list** (REQ3) — merged from all planned meals
- **Group grocery items by category** (REQ4) — Vegetables, Proteins, Dairy, Grains, Other

## Technology Stack

| Component        | Technology              |
|------------------|-------------------------|
| Language         | Java 17                 |
| UI Framework     | JavaFX 21               |
| Data Storage     | SQLite (via sqlite-jdbc)|
| CSV Reading      | Apache Commons CSV      |
| Build System     | Gradle 8.5              |

## How to Run

### Prerequisites
- Java 17 or higher installed
- Internet connection for first Gradle dependency download

### Run the application

```bash
./gradlew run        # Linux / macOS
gradlew.bat run      # Windows
```

The application window will open automatically.

### Build a JAR

```bash
./gradlew build
```

## Project Structure

```
SmartMealPlanner/
├── src/main/java/com/mealplanner/
│   ├── Main.java                      # Application entry point
│   ├── model/
│   │   ├── Recipe.java                # Recipe data model
│   │   ├── Ingredient.java            # Ingredient with category
│   │   ├── MealPlan.java              # Weekly plan (Day → Recipe map)
│   │   └── GroceryItem.java           # Merged grocery item
│   ├── data/
│   │   └── RecipeDatabase.java        # CSV loading + SQLite persistence
│   ├── service/
│   │   ├── MealPlanService.java       # Meal plan business logic
│   │   └── GroceryListService.java    # Grocery list generation & merging
│   └── ui/
│       └── MainController.java        # JavaFX scene & UI
├── src/main/resources/
│   └── recipes.csv                    # Recipe dataset
├── docs/
│   ├── description.pdf                # Project requirements (SRS)
│   └── handover1.pdf                  # Handover document
├── build.gradle
└── README.md
```

## Data Storage

- **Recipes** are loaded from `src/main/resources/recipes.csv` at startup.
- **The meal plan** is persisted in a local SQLite database (`meal_planner.db`) — so your plan is saved between sessions.

## Features Overview

| Feature                    | Location            |
|----------------------------|---------------------|
| Browse & filter recipes    | Tab 1: Browse Recipes |
| View recipe details        | Tab 1: right panel  |
| Assign recipe to a day     | Tab 1: dropdown + button |
| View/edit weekly plan      | Tab 2: Weekly Plan  |
| Remove meals from days     | Tab 2: day cards    |
| Clear entire week          | Tab 2: Clear button |
| Auto-generated grocery list| Tab 3: Grocery List |
| Grouped by category        | Tab 3: colour-coded sections |
| Copy list to clipboard     | Tab 3: Copy button  |

## Authors

Manish Harish Kumar, Om Gajanan Badgujar, Aayush Sudhakar Raibole, Sakshi Ashok Palhade  
Software Engineering SoSe 26 — Bauhaus-Universität Weimar
