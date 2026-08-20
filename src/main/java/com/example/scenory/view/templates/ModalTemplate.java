package com.example.scenory.view.templates;

import java.io.IOException;

import com.example.scenory.view.templates.config.ModalConfig;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Base modal dialog template that provides consistent styling and behavior.
 * <p>
 * This template follows the existing RichTextModal pattern for FXML loading,
 * CSS styling, and Stage management while providing a flexible, configurable API.
 * </p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Create modal with custom configuration
 * ModalConfig config = ModalConfig.builder()
 *     .title("My Dialog")
 *     .dimensions(800, 600)
 *     .minDimensions(600, 400)
 *     .resizable(true)
 *     .owner(primaryStage)
 *     .onClose(v -> System.out.println("Dialog closed"))
 *     .build();
 * 
 * // Create modal with FXML content
 * ModalTemplate modal = ModalTemplate.createWithFXML("/com/example/scenory/MyDialog.fxml", config);
 * modal.showAndWait();
 * 
 * // Or create with programmatic content
 * VBox content = new VBox(new Label("Hello World"));
 * ModalTemplate modal2 = ModalTemplate.createWithContent(content, config);
 * modal2.show();
 * }</pre>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>APPLICATION_MODAL modality by default</li>
 *   <li>Automatic CSS stylesheet loading from /com/example/scenory/styles.css</li>
 *   <li>Configurable dimensions, title, owner window, and styling</li>
 *   <li>Minimum dimension constraints support</li>
 *   <li>Automatic stage centering</li>
 *   <li>Close callback support</li>
 *   <li>Support for both FXML-based and programmatic content</li>
 * </ul>
 * 
 * @see ModalConfig
 */
public class ModalTemplate {
    
    private final Stage stage;
    private final Scene scene;
    private final ModalConfig config;
    private Object controller;
    
    /**
     * Private constructor - use factory methods to create instances.
     * 
     * @param content The root node for the modal's scene
     * @param config The configuration object
     */
    private ModalTemplate(Parent content, ModalConfig config) {
        this.config = config;
        
        // Initialize Stage with APPLICATION_MODAL
        this.stage = new Stage();
        this.stage.initModality(Modality.APPLICATION_MODAL);
        
        // Apply configuration to stage
        applyConfiguration();
        
        // Create and configure scene
        this.scene = new Scene(content, config.getWidth(), config.getHeight());
        
        // Load CSS stylesheet
        loadCSS();
        
        // Apply custom CSS classes from config
        applyStyleClasses();
        
        // Set scene to stage
        this.stage.setScene(this.scene);
        
        // Center stage on screen
        this.stage.centerOnScreen();
        
        // Setup close callback
        setupCloseCallback();
    }
    
    /**
     * Creates a modal dialog with the provided configuration.
     * This method creates an empty modal that can have content added programmatically.
     * 
     * @param config The modal configuration
     * @return A new ModalTemplate instance
     * @throws IllegalArgumentException if config is null
     */
    public static ModalTemplate create(ModalConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("[ModalTemplate] Config cannot be null");
        }
        
        // Create empty parent for programmatic content
        Parent emptyRoot = new javafx.scene.layout.VBox();
        return new ModalTemplate(emptyRoot, config);
    }
    
    /**
     * Creates a modal dialog with content loaded from an FXML file.
     * <p>
     * The FXML file should be in the resources directory and follow the
     * /com/example/scenory/ path convention.
     * </p>
     * 
     * @param fxmlPath The resource path to the FXML file (e.g., "/com/example/scenory/MyDialog.fxml")
     * @param config The modal configuration
     * @return A new ModalTemplate instance with FXML content loaded
     * @throws RuntimeException if FXML loading fails
     * @throws IllegalArgumentException if config or fxmlPath is null
     */
    public static ModalTemplate createWithFXML(String fxmlPath, ModalConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("[ModalTemplate] Config cannot be null");
        }
        if (fxmlPath == null || fxmlPath.trim().isEmpty()) {
            throw new IllegalArgumentException("[ModalTemplate] FXML path cannot be null or empty");
        }
        
        try {
            System.out.println("📄 [ModalTemplate] Loading FXML: " + fxmlPath);
            
            FXMLLoader loader = new FXMLLoader(ModalTemplate.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            ModalTemplate modal = new ModalTemplate(root, config);
            modal.controller = loader.getController();
            
            System.out.println("✅ [ModalTemplate] FXML loaded successfully");
            return modal;
            
        } catch (IOException e) {
            throw new RuntimeException(
                "[ModalTemplate] FXML loading: Could not load " + fxmlPath + " (file not found or IO error)", 
                e
            );
        } catch (Exception e) {
            throw new RuntimeException(
                "[ModalTemplate] FXML loading: Unexpected error loading " + fxmlPath, 
                e
            );
        }
    }
    
    /**
     * Creates a modal dialog with programmatically-created content.
     * 
     * @param content The JavaFX Node to display in the modal
     * @param config The modal configuration
     * @return A new ModalTemplate instance
     * @throws IllegalArgumentException if content or config is null
     */
    public static ModalTemplate createWithContent(Node content, ModalConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("[ModalTemplate] Config cannot be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("[ModalTemplate] Content cannot be null");
        }
        
        // Wrap content in a Parent if it isn't already
        Parent root;
        if (content instanceof Parent parent) {
            root = parent;
        } else {
            javafx.scene.layout.VBox wrapper = new javafx.scene.layout.VBox(content);
            root = wrapper;
        }
        
        return new ModalTemplate(root, config);
    }
    
    /**
     * Applies configuration settings to the stage.
     */
    private void applyConfiguration() {
        // Apply title
        stage.setTitle(config.getTitle());
        
        // Apply resizable setting
        stage.setResizable(config.isResizable());
        
        // Apply owner window
        if (config.getOwnerWindow() != null) {
            stage.initOwner(config.getOwnerWindow());
        }
        
        // Apply minimum dimensions if specified
        if (config.getMinWidth() != null) {
            stage.setMinWidth(config.getMinWidth());
        }
        if (config.getMinHeight() != null) {
            stage.setMinHeight(config.getMinHeight());
        }
    }
    
    /**
     * Loads the CSS stylesheet from application resources.
     * Follows the existing pattern from RichTextModal.
     */
    private void loadCSS() {
        try {
            String cssFile = ModalTemplate.class
                .getResource("/com/example/scenory/styles.css")
                .toExternalForm();
            scene.getStylesheets().add(cssFile);
            System.out.println("✅ [ModalTemplate] CSS loaded successfully");
        } catch (Exception e) {
            System.out.println("⚠️ [ModalTemplate] CSS loading failed: " + e.getMessage());
            System.out.println("   Continuing with default styling");
        }
    }
    
    /**
     * Applies custom CSS style classes from the configuration.
     */
    private void applyStyleClasses() {
        if (!config.getStyleClasses().isEmpty()) {
            Parent root = scene.getRoot();
            root.getStyleClass().addAll(config.getStyleClasses());
            System.out.println("✅ [ModalTemplate] Applied " + config.getStyleClasses().size() + " custom style classes");
        }
    }
    
    /**
     * Sets up the close callback handler.
     */
    private void setupCloseCallback() {
        if (config.getOnClose() != null) {
            stage.setOnCloseRequest(event -> {
                try {
                    config.getOnClose().accept(null);
                } catch (Exception e) {
                    System.err.println("❌ [ModalTemplate] Close callback error: " + e.getMessage());
                    System.err.println("   Stack trace: " + e.getClass().getName() + " at " + 
                        (e.getStackTrace().length > 0 ? e.getStackTrace()[0].toString() : "unknown"));
                }
            });
        }
    }
    
    /**
     * Displays the modal dialog without blocking.
     * The modal will be shown and control will return immediately.
     */
    public void show() {
        stage.show();
    }
    
    /**
     * Displays the modal dialog and blocks until it is closed.
     * This method will not return until the modal is closed by the user.
     */
    public void showAndWait() {
        stage.showAndWait();
    }
    
    /**
     * Closes the modal dialog.
     * This will trigger the onClose callback if one was configured.
     */
    public void close() {
        stage.close();
    }
    
    /**
     * Gets the Stage instance for advanced customization.
     * 
     * @return The underlying JavaFX Stage
     */
    public Stage getStage() {
        return stage;
    }
    
    /**
     * Gets the Scene instance for advanced customization.
     * 
     * @return The JavaFX Scene
     */
    public Scene getScene() {
        return scene;
    }
    
    /**
     * Gets the FXML controller instance if the modal was created with FXML.
     * Returns null if the modal was created without FXML.
     * 
     * @param <T> The controller type
     * @return The controller instance, or null if not applicable
     */
    @SuppressWarnings("unchecked")
    public <T> T getController() {
        return (T) controller;
    }
}
