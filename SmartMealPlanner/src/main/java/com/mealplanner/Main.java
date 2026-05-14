// Main.java
// Written by Hassan Ahmed.
// This is the entry point of the Smart Meal Planner application.
// It initialises the database and hands control over to the
// MainController which builds and displays the UI.
// I made sure to close the database connection in the stop() method
// so we never leave an open connection when the app is closed.

package com.mealplanner;

import com.mealplanner.data.RecipeDatabase;
import com.mealplanner.ui.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Smart Meal Planner application.
 *
 * Run with: ./gradlew run
 */
public class Main extends Application {

    private RecipeDatabase database;

 // JavaFX calls this method automatically when the application launches
 // we set up the database first before building the UI
 // so all recipes are ready to display immediately on startup
    @Override
    public void start(Stage primaryStage) {
        database = new RecipeDatabase();
        MainController controller = new MainController(database);

        Scene scene = controller.buildScene(primaryStage);

        primaryStage.setTitle("Smart Meal Planner");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }
 // JavaFX calls this when the window is closed
 // important to close the database here to avoid any data corruption
    
    @Override
    public void stop() {
        if (database != null) database.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
