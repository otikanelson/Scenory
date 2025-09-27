package com.example.scenory.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Handles navigation between different screens in the application
 */
public class NavigationManager {

    private final Consumer<String> statusUpdater;

    public NavigationManager(Consumer<String> statusUpdater) {
        this.statusUpdater = statusUpdater;
    }

    /**
     * Navigate to welcome screen
     */
    public void navigateToWelcomeScreen(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scenory/welcome-view.fxml"));
            Parent welcomeView = loader.load();

            stage.getScene().setRoot(welcomeView);
            stage.setTitle("Scenory - Professional Storyboard Creator");

            System.out.println("🏠 Returned to welcome screen");
            statusUpdater.accept("Returned to welcome screen");

        } catch (IOException e) {
            System.err.println("❌ Failed to return to welcome screen: " + e.getMessage());
            e.printStackTrace();
            statusUpdater.accept("❌ Error navigating to welcome screen");
        }
    }

    /**
     * Update window title
     */
    public void updateWindowTitle(Stage stage, String projectName) {
        try {
            if (projectName != null && !projectName.trim().isEmpty()) {
                stage.setTitle("Scenory - " + projectName);
            } else {
                stage.setTitle("Scenory");
            }
        } catch (Exception e) {
            System.err.println("❌ Error updating window title: " + e.getMessage());
        }
    }

    /**
     * Handle application exit
     */
    public void exitApplication() {
        System.out.println("👋 Exiting Scenory application");
        System.exit(0);
    }
}