package com.example.scenory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ScenoryApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                ScenoryApplication.class.getResource("welcome-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        try {
            String cssFile = ScenoryApplication.class.getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(cssFile);
            System.out.println("✅ CSS loaded successfully");
        } catch (Exception e) {
            System.out.println("⚠️ CSS file not found. Running with default styling.");
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
        stage.setMinWidth(900);
        stage.setMinHeight(700);
        stage.centerOnScreen();
        stage.show();

        System.out.println("🚀 Scenory application started - Welcome screen displayed");
    }

    public static void main(String[] args) {
        launch();
    }
}
