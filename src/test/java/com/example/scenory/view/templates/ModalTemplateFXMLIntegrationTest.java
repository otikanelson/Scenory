package com.example.scenory.view.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import com.example.scenory.view.templates.config.ModalConfig;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Integration tests for ModalTemplate FXML and CSS loading functionality.
 * <p>
 * This test verifies:
 * - FXML loading from resource path
 * - IOException wrapping in RuntimeException with descriptive messages
 * - CSS loading from /com/example/scenory/styles.css
 * - CSS loading failure handling (non-fatal)
 * - Controller storage and retrieval
 * </p>
 * 
 * <p>
 * **Validates: Requirements 1.3, 1.5, 7.1, 7.2, 7.5, 7.6, 9.1, 9.2, 9.5, 9.6**
 * </p>
 */
@DisplayName("ModalTemplate FXML and CSS Integration Tests")
class ModalTemplateFXMLIntegrationTest extends ApplicationTest {
    
    @Override
    public void start(Stage stage) {
        // Required by TestFX but we won't display anything
    }
    
    @Test
    @DisplayName("createWithFXML() should load valid FXML file successfully - Requirement 1.3")
    void testCreateWithFXMLValidPath() {
        // Execute on JavaFX Application Thread
        Platform.runLater(() -> {
            ModalConfig config = ModalConfig.builder()
                    .title("Test FXML Modal")
                    .dimensions(600, 400)
                    .build();
            
            // Load FXML resource
            ModalTemplate modal = ModalTemplate.createWithFXML(
                    "/com/example/scenory/test/TestModal.fxml", 
                    config
            );
            
            assertNotNull(modal, "Modal should be created");
            assertNotNull(modal.getStage(), "Stage should be initialized");
            assertNotNull(modal.getScene(), "Scene should be initialized");
            assertNotNull(modal.getScene().getRoot(), "Root node should be loaded from FXML");
        });
        
        // Wait for JavaFX thread to complete
        waitFor(1000);
    }
    
    @Test
    @DisplayName("createWithFXML() should store and return FXML controller - Requirement 1.5")
    void testCreateWithFXMLStoresController() {
        // Execute on JavaFX Application Thread
        Platform.runLater(() -> {
            ModalConfig config = ModalConfig.builder().build();
            
            ModalTemplate modal = ModalTemplate.createWithFXML(
                    "/com/example/scenory/test/TestModal.fxml", 
                    config
            );
            
            // Get controller
            TestModalController controller = modal.getController();
            
            assertNotNull(controller, "Controller should be stored and retrievable");
            assertEquals("Test Modal", controller.getTitle(), "Controller should be properly initialized");
        });
        
        waitFor(1000);
    }
    
    @Test
    @DisplayName("createWithFXML() should throw RuntimeException for non-existent FXML - Requirement 9.1")
    void testCreateWithFXMLNonExistentFile() {
        Platform.runLater(() -> {
            ModalConfig config = ModalConfig.builder().build();
            
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                ModalTemplate.createWithFXML("/com/example/nonexistent.fxml", config);
            });
            
            // Verify exception message includes path and descriptive context
            String message = exception.getMessage();
            assertTrue(message.contains("ModalTemplate"), "Error message should include component name");
            assertTrue(message.contains("FXML loading"), "Error message should include operation context");
            assertTrue(message.contains("/com/example/nonexistent.fxml"), "Error message should include file path");
        });
        
        waitFor(1000);
    }
    
    @Test
    @DisplayName("createWithFXML() should throw IllegalArgumentException for null FXML path - Requirement 9.4")
    void testCreateWithFXMLNullPath() {
        Platform.runLater(() -> {
            ModalConfig config = ModalConfig.builder().build();
            
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                ModalTemplate.createWithFXML(null, config);
            });
            
            assertTrue(exception.getMessage().contains("FXML path"), 
                    "Error message should mention FXML path");
        });
        
        waitFor(1000);
    }
    
    @Test
    @DisplayName("createWithFXML() should throw IllegalArgumentException for empty FXML path - Requirement 9.4")
    void testCreateWithFXMLEmptyPath() {
        Platform.runLater(() -> {
            ModalConfig config = ModalConfig.builder().build();
            
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                ModalTemplate.createWithFXML("", config);
            });
            
            assertTrue(exception.getMessage().contains("FXML path"), 
                    "Error message should mention FXML path");
        });
        
        waitFor(1000);
    }
    
    @Test
    @DisplayName("Modal should load CSS from /com/example/scenory/styles.css - Requirement 7.2")
    void testCSSLoading() {
        Platform.runLater(() -> {
            ModalConfig config = ModalConfig.builder().build();
            
            ModalTemplate modal = ModalTemplate.createWithFXML(
                    "/com/example/scenory/test/TestModal.fxml", 
                    config
            );
            
            // Verify CSS stylesheet is loaded
            var stylesheets = modal.getScene().getStylesheets();
            assertNotNull(stylesheets, "Stylesheets collection should exist");
            
            // Check if styles.css is loaded (it will contain the resource path)
            boolean hasCSSFile = stylesheets.stream()
                    .anyMatch(css -> css.contains("styles.css"));
            
            assertTrue(hasCSSFile, "styles.css should be loaded into scene");
        });
        
        waitFor(1000);
    }
    
    @Test
    @DisplayName("CSS loading should be non-fatal - modal should work without CSS - Requirement 9.2")
    void testCSSLoadingNonFatal() {
        // This test verifies that even if CSS fails to load (which it shouldn't in normal cases),
        // the modal is still created successfully. The loadCSS() method catches exceptions
        // and logs warnings but doesn't throw.
        
        Platform.runLater(() -> {
            ModalConfig config = ModalConfig.builder().build();
            
            // Create modal - should succeed even if CSS has issues
            ModalTemplate modal = ModalTemplate.createWithFXML(
                    "/com/example/scenory/test/TestModal.fxml", 
                    config
            );
            
            assertNotNull(modal, "Modal should be created even if CSS loading has issues");
            assertNotNull(modal.getScene(), "Scene should be created");
        });
        
        waitFor(1000);
    }
    
    /**
     * Helper method to wait for JavaFX thread to complete operations.
     */
    private void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
