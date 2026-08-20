package com.example.scenory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Manual Bug Condition Exploration Test for JavaFX Window Positioning
 * 
 * **Validates: Requirements 1.1, 1.2, 1.3, 1.4**
 * 
 * CRITICAL: This test demonstrates the bug on UNFIXED code.
 * DO NOT attempt to fix the test or the code when it fails.
 * 
 * This application allows manual testing of the window positioning bug.
 * 
 * INSTRUCTIONS:
 * 1. Run this application
 * 2. The window will start MAXIMIZED (replicating ScenoryApplication behavior)
 * 3. Click "Test Case 1: Restore Window" to restore from maximized state
 * 4. Observe the window Y coordinate - it should be NEGATIVE (bug confirmed)
 * 5. Click "Test Case 2: Scene Transition" to test Scene transition while maximized
 * 6. Click "Restore Window" again after the transition
 * 7. Observe Y coordinate again - should remain NEGATIVE (bug persists)
 * 
 * EXPECTED OUTCOME ON UNFIXED CODE:
 * - Y coordinate will be -8 or -11 after restore
 * - Title bar will be above screen top (inaccessible)
 * - Window controls (minimize, maximize, close) cannot be accessed
 * 
 * EXPECTED OUTCOME ON FIXED CODE:
 * - Y coordinate will be >= 0 after restore
 * - Title bar will be fully visible and accessible
 */
public class ManualWindowPositioningBugTest extends Application {

    private Stage primaryStage;
    private Label statusLabel;
    private Label coordinatesLabel;
    private int testCase = 0;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        
        // Replicate ScenoryApplication.start() logic WITHOUT the fix
        FXMLLoader fxmlLoader = new FXMLLoader(
            ScenoryApplication.class.getResource("welcome-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // Load custom fonts (same as ScenoryApplication)
        loadCustomFonts();

        // Load CSS
        try {
            String cssFile = ScenoryApplication.class.getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(cssFile);
        } catch (Exception e) {
            System.out.println("⚠️ CSS file not found");
        }

        stage.setTitle("Manual Bug Test - Window Positioning");
        
        // Load icon
        URL iconUrl = ScenoryApplication.class.getResource("/com/example/scenory/icon.png");
        if (iconUrl != null) {
            stage.getIcons().add(new Image(iconUrl.toExternalForm()));
        }

        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(750);
        
        // BUG CONDITION: Call setMaximized(true) WITHOUT centerOnScreen()
        // This is the current behavior in ScenoryApplication.java line 67
        stage.setMaximized(true);
        stage.show();

        System.out.println("\n=== MANUAL BUG TEST STARTED ===");
        System.out.println("Window is now MAXIMIZED");
        System.out.println("Window position while maximized: X=" + stage.getX() + ", Y=" + stage.getY());
        System.out.println("\nINSTRUCTIONS:");
        System.out.println("1. Manually click the RESTORE button (middle button in title bar)");
        System.out.println("2. Observe if the title bar is visible or hidden above the screen");
        System.out.println("3. Check console output for Y coordinate (should be NEGATIVE on unfixed code)");
        System.out.println("\nTo see automated measurements, create a test control window...");
        
        // Create a small control window for automated testing
        Platform.runLater(() -> createTestControlWindow());
        
        // Monitor window state changes
        stage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (!isNowMaximized && wasMaximized) {
                // Window was just restored from maximized
                double yCoord = stage.getY();
                System.out.println("\n>>> RESTORE DETECTED <<<");
                System.out.println("Window position after restore: X=" + stage.getX() + ", Y=" + yCoord);
                System.out.println("Window size after restore: W=" + stage.getWidth() + ", H=" + stage.getHeight());
                
                if (yCoord < 0) {
                    System.out.println(">>> BUG CONFIRMED: Y coordinate is NEGATIVE (" + yCoord + ")");
                    System.out.println(">>> Title bar is positioned ABOVE screen top (Y = 0)");
                    System.out.println(">>> Window controls are INACCESSIBLE to the user");
                } else {
                    System.out.println(">>> EXPECTED BEHAVIOR: Y coordinate is >= 0 (" + yCoord + ")");
                    System.out.println(">>> Title bar is visible and accessible");
                }
                
                updateTestControls(yCoord);
            }
        });
    }

    private void createTestControlWindow() {
        Stage controlStage = new Stage();
        controlStage.setTitle("Test Controls");
        controlStage.setAlwaysOnTop(true);
        
        VBox controlBox = new VBox(10);
        controlBox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label titleLabel = new Label("Window Positioning Bug Test");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        statusLabel = new Label("Status: Window is maximized");
        statusLabel.setStyle("-fx-font-size: 14px;");
        
        coordinatesLabel = new Label("Coordinates: (waiting for restore)");
        coordinatesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: blue;");
        
        Button restoreButton = new Button("Test Case 1: Restore Window");
        restoreButton.setOnAction(e -> {
            testCase = 1;
            primaryStage.setMaximized(false);
            statusLabel.setText("Status: Restoring from maximized...");
        });
        
        Button sceneTransitionButton = new Button("Test Case 2: Scene Transition");
        sceneTransitionButton.setOnAction(e -> {
            testCase = 2;
            testSceneTransition();
        });
        
        Button maximizeButton = new Button("Maximize Window");
        maximizeButton.setOnAction(e -> {
            primaryStage.setMaximized(true);
            statusLabel.setText("Status: Window maximized");
            coordinatesLabel.setText("Coordinates: (maximized)");
        });
        
        Button checkPositionButton = new Button("Check Current Position");
        checkPositionButton.setOnAction(e -> {
            double x = primaryStage.getX();
            double y = primaryStage.getY();
            boolean maximized = primaryStage.isMaximized();
            
            coordinatesLabel.setText(String.format("Coordinates: X=%.1f, Y=%.1f (Maximized: %s)", x, y, maximized));
            System.out.println("Current position: X=" + x + ", Y=" + y + " (Maximized: " + maximized + ")");
            
            if (!maximized && y < 0) {
                System.out.println(">>> BUG CONFIRMED: Negative Y coordinate detected!");
            }
        });
        
        Label instructionsLabel = new Label(
            "Instructions:\n" +
            "1. Click 'Test Case 1' to restore window\n" +
            "2. Check if Y coordinate is negative\n" +
            "3. Click 'Test Case 2' to test Scene transition\n" +
            "4. Restore again and check Y coordinate"
        );
        instructionsLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        instructionsLabel.setWrapText(true);
        instructionsLabel.setMaxWidth(300);
        
        controlBox.getChildren().addAll(
            titleLabel,
            statusLabel,
            coordinatesLabel,
            restoreButton,
            sceneTransitionButton,
            maximizeButton,
            checkPositionButton,
            instructionsLabel
        );
        
        Scene controlScene = new Scene(controlBox, 350, 400);
        controlStage.setScene(controlScene);
        controlStage.setX(50);
        controlStage.setY(100);
        controlStage.show();
    }

    private void updateTestControls(double yCoord) {
        if (coordinatesLabel != null) {
            coordinatesLabel.setText(String.format("Coordinates: X=%.1f, Y=%.1f", 
                primaryStage.getX(), yCoord));
            
            if (yCoord < 0) {
                coordinatesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red; -fx-font-weight: bold;");
                statusLabel.setText("Status: BUG CONFIRMED - Y < 0 (Test Case " + testCase + ")");
            } else {
                coordinatesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green; -fx-font-weight: bold;");
                statusLabel.setText("Status: EXPECTED BEHAVIOR - Y >= 0 (Test Case " + testCase + ")");
            }
        }
    }

    private void testSceneTransition() {
        try {
            System.out.println("\n=== TEST CASE 2: Scene Transition While Maximized ===");
            System.out.println("Current state: Maximized=" + primaryStage.isMaximized());
            
            // Maximize first if not already maximized
            if (!primaryStage.isMaximized()) {
                primaryStage.setMaximized(true);
                Thread.sleep(500);
            }
            
            // Load main-view.fxml (simulating template button click)
            FXMLLoader mainLoader = new FXMLLoader(
                ScenoryApplication.class.getResource("main-view.fxml")
            );
            Parent mainView = mainLoader.load();
            
            // Create NEW Scene (this is the critical operation)
            Scene newScene = new Scene(mainView);
            
            // Load CSS for new scene
            try {
                String cssFile = ScenoryApplication.class.getResource("styles.css").toExternalForm();
                newScene.getStylesheets().clear();
                newScene.getStylesheets().add(cssFile);
            } catch (Exception e) {
                System.out.println("⚠️ CSS file not found for main view");
            }
            
            // BUG CONDITION: Set scene while stage is maximized
            // This is the current behavior in WelcomeController.java line 215
            primaryStage.setScene(newScene);
            primaryStage.setTitle("Manual Bug Test - Main View (After Transition)");
            
            // The current code also calls setMaximized(true) again at line 226
            primaryStage.setMaximized(true);
            
            System.out.println("Scene transition completed");
            System.out.println("Now click 'Test Case 1' button to restore and see if Y < 0");
            
            if (statusLabel != null) {
                statusLabel.setText("Status: Scene transitioned, now restore to test");
            }
            
        } catch (Exception e) {
            System.err.println("Error during scene transition test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCustomFonts() {
        try {
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Inter/static/Inter-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Inter/static/Inter-Bold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Outfit/static/Outfit-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Outfit/static/Outfit-ExtraBold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-Medium.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-SemiBold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-Bold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/JetBrainsMono/static/JetBrainsMono-Regular.ttf"), 14);
            System.out.println("✅ Custom fonts loaded successfully");
        } catch (Exception e) {
            System.out.println("⚠️ Could not load custom fonts: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║  MANUAL WINDOW POSITIONING BUG TEST                             ║");
        System.out.println("║  Property 1: Bug Condition - Title Bar Inaccessible After       ║");
        System.out.println("║              Restore From Maximized                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("This test demonstrates the bug on UNFIXED code.");
        System.out.println("The application will start MAXIMIZED (without centerOnScreen()).");
        System.out.println("Use the test control window to trigger different test cases.");
        System.out.println();
        launch(args);
    }
}
