package com.example.scenory;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ScenoryApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                ScenoryApplication.class.getResource("welcome-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // Load custom fonts
        try {
            // Inter
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Inter/static/Inter-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Inter/static/Inter-Bold.ttf"), 14);
            
            // Outfit
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Outfit/static/Outfit-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Outfit/static/Outfit-ExtraBold.ttf"), 14);
            
            // Space Grotesk
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-Medium.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-SemiBold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-Bold.ttf"), 14);
            
            // JetBrains Mono
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/JetBrainsMono/static/JetBrainsMono-Regular.ttf"), 14);
            
            System.out.println("✅ Custom fonts loaded successfully (including Space Grotesk)");
        } catch (Exception e) {
            System.out.println("⚠️ Could not load custom fonts: " + e.getMessage());
        }

        try {
            String cssFile = ScenoryApplication.class.getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(cssFile);
            System.out.println("✅ CSS loaded successfully from: " + cssFile);
            System.out.println("📋 Total stylesheets loaded: " + scene.getStylesheets().size());
        } catch (Exception e) {
            System.out.println("⚠️ CSS file not found. Running with default styling.");
            e.printStackTrace();
        }

        stage.setTitle("Scenory - Professional Storyboard Creator");

        // ✅ Correct icon loading
        URL iconUrl = ScenoryApplication.class.getResource("/com/example/scenory/icon.png");
        if (iconUrl != null) {
            stage.getIcons().add(new Image(iconUrl.toExternalForm()));
            System.out.println("🖼️ App icon loaded successfully");
        } else {
            System.out.println("⚠️ Icon not found at: /com/example/scenory/icon.png");
        }

        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(750);
        
        // Add listener to fix window position when restoring from maximized
        stage.maximizedProperty().addListener((observable, wasMaximized, isNowMaximized) -> {
            if (wasMaximized && !isNowMaximized) {
                // Window was just restored from maximized state
                // Fix the position if it's negative (title bar above screen)
                javafx.application.Platform.runLater(() -> {
                    if (stage.getY() < 0) {
                        System.out.println("⚠️ Detected negative Y position after restore: " + stage.getY());
                        System.out.println("🔧 Fixing window position...");
                        stage.setY(0);  // Move to top of screen (title bar visible)
                        stage.setX(Math.max(0, stage.getX()));  // Ensure X is also valid
                    }
                });
            }
        });
        
        stage.setMaximized(true);
        stage.show();

        System.out.println("🚀 Scenory application started - Welcome screen displayed");
    }

    public static void main(String[] args) {
        launch();
    }
}
