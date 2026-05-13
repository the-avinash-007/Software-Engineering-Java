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

    @Override
    public void stop() {
        if (database != null) database.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
