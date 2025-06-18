package com.example.scenory.view.dialogs;

import com.example.scenory.model.Panel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Simplified Rich Text Editor Modal Controller
 * Uses TextArea instead of WebView for better compatibility
 */
public class RichTextModalController implements Initializable {

    // =====================================
    // FXML UI Components
    // =====================================

    // Header Components
    @FXML private Label modalTitleLabel;
    @FXML private Label panelNameLabel;
    @FXML private Label characterCountLabel;
    @FXML private Button closeButton;

    // Content Components
    @FXML private TextArea descriptionTextArea;

    // Footer Components
    @FXML private Button insertTemplateButton;
    @FXML private Button clearAllButton;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    // =====================================
    // CONTROLLER STATE
    // =====================================

    private Panel currentPanel;
    private Consumer<String> onSaveCallback;
    private String originalText = "";
    private boolean isModified = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🖋️ Initializing Simplified Rich Text Modal Controller...");

        setupTextArea();
        setupEventHandlers();

        System.out.println("✅ Simplified Rich Text Modal Controller initialized");
    }

    private void setupTextArea() {
        descriptionTextArea.setWrapText(true);
        descriptionTextArea.setPromptText("Enter your panel description here...\n\nYou can write detailed descriptions for this panel including:\n• What happens in this scene\n• Character actions and expressions\n• Camera movements and angles\n• Visual effects or special notes\n• Dialogue or voice-over text");

        // Listen for text changes
        descriptionTextArea.textProperty().addListener((obs, oldText, newText) -> {
            isModified = !newText.equals(originalText);
            updateCharacterCount(newText);
        });
    }

    private void setupEventHandlers() {
        // Character count update
        updateCharacterCount("");

        // Window close handler
        closeButton.setOnAction(e -> handleWindowClose());
    }

    private void updateCharacterCount(String text) {
        if (characterCountLabel != null) {
            characterCountLabel.setText(text.length() + " characters");
        }
    }

    // =====================================
    // PUBLIC API METHODS
    // =====================================

    /**
     * Open the rich text editor for a specific panel
     */
    public void openForPanel(Panel panel, Consumer<String> saveCallback) {
        this.currentPanel = panel;
        this.onSaveCallback = saveCallback;

        // Update UI with panel info
        if (panel != null) {
            panelNameLabel.setText(panel.getName());
            modalTitleLabel.setText("Edit Description - " + panel.getName());

            // Load existing content
            originalText = panel.getDescriptionPlainText() != null ? panel.getDescriptionPlainText() : "";

            // Set content
            descriptionTextArea.setText(originalText);
            descriptionTextArea.positionCaret(0);
        }

        // Reset state
        isModified = false;
        updateCharacterCount(originalText);

        System.out.println("📝 Rich text editor opened for panel: " + (panel != null ? panel.getName() : "Unknown"));
    }

    // =====================================
    // TEMPLATE AND UTILITY ACTIONS
    // =====================================

    @FXML
    private void insertTemplate() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Basic Description",
                "Basic Description", "Action Scene", "Character Introduction", "Location Description");
        dialog.setTitle("Insert Template");
        dialog.setHeaderText("Choose a description template");
        dialog.setContentText("Template:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(template -> {
            String templateContent = getTemplateContent(template);
            if (!templateContent.isEmpty()) {
                // Replace current text with template
                descriptionTextArea.setText(templateContent);
                isModified = true;
                updateCharacterCount(templateContent);
            }
        });
    }

    private String getTemplateContent(String templateName) {
        return switch (templateName) {
            case "Basic Description" -> """
                What happens: Describe the main action or event in this panel.
                
                Visual details: Key visual elements, composition, or important details.
                
                Notes: Additional notes for animators or production team.
                """;
            case "Action Scene" -> """
                Action: [Describe the action taking place]
                
                Camera: [Camera angle and movement]
                
                Timing: [Pacing and duration notes]
                
                Effects: [Visual effects or special requirements]
                """;
            case "Character Introduction" -> """
                Character: [Character name and role]
                
                Appearance: [How they look, clothing, expression]
                
                Mood/Emotion: [Character's emotional state]
                
                Context: [Setting and situation]
                """;
            case "Location Description" -> """
                Location: [Where this scene takes place]
                
                Time: [Time of day, season, etc.]
                
                Atmosphere: [Mood and lighting]
                
                Key Elements: [Important background details]
                """;
            default -> "";
        };
    }

    @FXML
    private void clearAll() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear All Content");
        alert.setHeaderText("Clear all text content?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            descriptionTextArea.clear();
            isModified = true;
            updateCharacterCount("");
        }
    }

    // =====================================
    // SAVE/CANCEL ACTIONS
    // =====================================

    @FXML
    private void saveDescription() {
        if (currentPanel != null && onSaveCallback != null) {
            String content = descriptionTextArea.getText();

            // Update panel with new content
            currentPanel.setDescriptionPlainText(content);
            currentPanel.setDescriptionRichText(content); // For now, same as plain text

            // Call the save callback
            onSaveCallback.accept(content);

            // Close modal
            closeModal();

            System.out.println("💾 Panel description saved: " + content.length() + " characters");
        }
    }

    @FXML
    private void cancelEdit() {
        if (isModified) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes");
            alert.setContentText("Do you want to discard your changes?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                closeModal();
            }
        } else {
            closeModal();
        }
    }

    @FXML
    private void closeModal() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void handleWindowClose() {
        cancelEdit();
    }
}