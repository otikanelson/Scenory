package com.example.scenory.view.dialogs;

import com.example.scenory.model.Panel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Utility class for creating and showing the Rich Text Modal
 * Provides a simple API for opening the rich text editor
 */
public class RichTextModal {

    /**
     * Open the rich text editor modal for a specific panel
     *
     * @param panel The panel to edit description for
     * @param ownerWindow The parent window
     * @param onSave Callback function when content is saved
     */
    public static void openForPanel(Panel panel, Window ownerWindow, Consumer<String> onSave) {
        try {
            System.out.println("📝 Opening Rich Text Modal for: " + panel.getName());

            // Load the FXML
            FXMLLoader loader = new FXMLLoader(
                    RichTextModal.class.getResource("/com/example/scenory/RichTextModal.fxml")
            );
            Parent root = loader.load();

            // Get controller
            RichTextModalController controller = loader.getController();

            // Create stage
            Stage stage = new Stage();
            stage.setTitle("Edit Panel Description - " + panel.getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerWindow);

            // Setup scene
            Scene scene = new Scene(root, 900, 700);

            // Add styling
            try {
                String cssFile = RichTextModal.class.getResource("/com/example/scenory/styles.css")
                        .toExternalForm();
                scene.getStylesheets().add(cssFile);
            } catch (Exception e) {
                System.out.println("⚠️ Could not load CSS for Rich Text Modal");
            }

            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.centerOnScreen();

            // Setup controller
            controller.openForPanel(panel, onSave);

            // Show modal
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("❌ Failed to create Rich Text Modal: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not create Rich Text Modal", e);
        }
    }

    /**
     * Quick method to open rich text editor with minimal setup
     */
    public static void editPanelDescription(Panel panel, Window ownerWindow) {
        openForPanel(panel, ownerWindow, (content) -> {
            System.out.println("📝 Panel description saved: " + content.length() + " characters");
        });
    }
}