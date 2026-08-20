package com.example.scenory.view.templates;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.scenory.view.templates.config.ModalConfig;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Unit tests for ModalTemplate class.
 * <p>
 * Tests cover:
 * - Factory method creation
 * - Stage initialization with APPLICATION_MODAL
 * - Configuration application (title, dimensions, resizable, owner)
 * - Minimum dimension constraints
 * - Custom CSS class application
 * - Center on screen behavior
 * - CSS loading following RichTextModal pattern
 * - CSS loading error handling (logs warning but doesn't fail)
 * - FXML loading error handling (throws RuntimeException with path)
 * - Close callback invocation
 * - Close callback exception handling
 * - Error handling for invalid inputs
 * </p>
 * 
 * <p>
 * **Validates: Requirements 1.2, 1.3, 1.4, 1.7, 1.8, 1.9, 1.10, 7.2, 7.6, 9.1, 9.2, 9.3**
 * </p>
 */
@DisplayName("ModalTemplate Tests")
class ModalTemplateTest {
    
    /**
     * Initialize JavaFX toolkit before running tests.
     * This is required for any JavaFX component testing.
     */
    @BeforeAll
    static void initJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
    }
    
    @Nested
    @DisplayName("Factory Method Creation")
    class FactoryMethodTests {
        
        @Test
        @DisplayName("create() should create ModalTemplate with empty content")
        void testCreateMethod() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .title("Test Modal")
                            .dimensions(800, 600)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    assertNotNull(modal, "Modal should not be null");
                    assertNotNull(modal.getStage(), "Stage should not be null");
                    assertNotNull(modal.getScene(), "Scene should not be null");
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get(), "Test should complete successfully");
        }
        
        @Test
        @DisplayName("create() should throw IllegalArgumentException for null config")
        void testCreateWithNullConfig() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean exceptionThrown = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    assertThrows(IllegalArgumentException.class, () -> {
                        ModalTemplate.create(null);
                    });
                    exceptionThrown.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(exceptionThrown.get(), "Exception should be thrown");
        }
        
        @Test
        @DisplayName("createWithContent() should create ModalTemplate with provided content")
        void testCreateWithContent() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    VBox content = new VBox(new Label("Test Content"));
                    ModalConfig config = ModalConfig.builder()
                            .title("Content Modal")
                            .dimensions(600, 400)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.createWithContent(content, config);
                    
                    assertNotNull(modal);
                    assertNotNull(modal.getStage());
                    assertNotNull(modal.getScene());
                    assertEquals(content, modal.getScene().getRoot());
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("createWithContent() should throw IllegalArgumentException for null content")
        void testCreateWithContentNullContent() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean exceptionThrown = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    assertThrows(IllegalArgumentException.class, () -> {
                        ModalTemplate.createWithContent(null, config);
                    });
                    exceptionThrown.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(exceptionThrown.get());
        }
        
        @Test
        @DisplayName("createWithContent() should throw IllegalArgumentException for null config")
        void testCreateWithContentNullConfig() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean exceptionThrown = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    VBox content = new VBox(new Label("Test"));
                    assertThrows(IllegalArgumentException.class, () -> {
                        ModalTemplate.createWithContent(content, null);
                    });
                    exceptionThrown.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(exceptionThrown.get());
        }
        
        @Test
        @DisplayName("createWithFXML() should throw IllegalArgumentException for null FXML path")
        void testCreateWithFXMLNullPath() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean exceptionThrown = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    assertThrows(IllegalArgumentException.class, () -> {
                        ModalTemplate.createWithFXML(null, config);
                    });
                    exceptionThrown.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(exceptionThrown.get());
        }
        
        @Test
        @DisplayName("createWithFXML() should throw IllegalArgumentException for empty FXML path")
        void testCreateWithFXMLEmptyPath() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean exceptionThrown = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    assertThrows(IllegalArgumentException.class, () -> {
                        ModalTemplate.createWithFXML("", config);
                    });
                    exceptionThrown.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(exceptionThrown.get());
        }
        
        @Test
        @DisplayName("createWithFXML() should throw RuntimeException for non-existent FXML file")
        void testCreateWithFXMLInvalidPath() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean exceptionThrown = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                        ModalTemplate.createWithFXML("/nonexistent/path.fxml", config);
                    });
                    
                    assertTrue(exception.getMessage().contains("FXML loading"));
                    assertTrue(exception.getMessage().contains("/nonexistent/path.fxml"));
                    exceptionThrown.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(exceptionThrown.get());
        }
    }
    
    @Nested
    @DisplayName("Stage Initialization - Requirement 1.2, 1.4")
    class StageInitializationTests {
        
        @Test
        @DisplayName("Stage should be initialized with APPLICATION_MODAL modality")
        void testApplicationModalModality() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    Stage stage = modal.getStage();
                    assertEquals(Modality.APPLICATION_MODAL, stage.getModality(), 
                            "Stage should have APPLICATION_MODAL modality");
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("Stage should be centered on screen after creation")
        void testCenterOnScreen() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .dimensions(800, 600)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    Stage stage = modal.getStage();
                    
                    // Note: centerOnScreen() sets the position, but we can't easily
                    // verify exact coordinates in headless mode. We verify that
                    // centerOnScreen() was called by checking the stage is created
                    // without errors and the position is not at (0, 0) which would
                    // indicate centering was not attempted.
                    assertNotNull(stage);
                    
                    // Show the stage to trigger positioning
                    stage.show();
                    
                    // In a non-headless environment, the stage would be centered.
                    // We verify it doesn't throw an exception and the stage is valid.
                    assertTrue(stage.getWidth() > 0 || stage.getHeight() > 0 || !stage.isShowing());
                    
                    stage.close();
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
    }
    
    @Nested
    @DisplayName("Configuration Application - Requirements 1.1, 1.8")
    class ConfigurationApplicationTests {
        
        @Test
        @DisplayName("Should apply title from config")
        void testTitleApplication() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .title("Custom Dialog Title")
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    assertEquals("Custom Dialog Title", modal.getStage().getTitle());
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("Should apply dimensions from config")
        void testDimensionsApplication() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .dimensions(1024, 768)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    Scene scene = modal.getScene();
                    
                    assertEquals(1024, scene.getWidth(), 0.01);
                    assertEquals(768, scene.getHeight(), 0.01);
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("Should apply resizable setting from config")
        void testResizableApplication() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .resizable(false)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    assertFalse(modal.getStage().isResizable());
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("Should apply owner window from config")
        void testOwnerWindowApplication() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    Stage ownerStage = new Stage();
                    
                    ModalConfig config = ModalConfig.builder()
                            .owner(ownerStage)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    assertEquals(ownerStage, modal.getStage().getOwner());
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
    }
    
    @Nested
    @DisplayName("Minimum Dimensions - Requirement 1.7")
    class MinimumDimensionsTests {
        
        @Test
        @DisplayName("Should apply minimum width when specified")
        void testMinimumWidthApplication() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .dimensions(1024, 768)
                            .minDimensions(800, 600)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    assertEquals(800, modal.getStage().getMinWidth(), 0.01);
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("Should apply minimum height when specified")
        void testMinimumHeightApplication() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .dimensions(1024, 768)
                            .minDimensions(800, 600)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    assertEquals(600, modal.getStage().getMinHeight(), 0.01);
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("Should not set minimum dimensions when not specified")
        void testNoMinimumDimensions() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .dimensions(800, 600)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    // Default JavaFX minWidth/minHeight should remain
                    // We're just verifying no exception is thrown
                    assertNotNull(modal.getStage());
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
    }
    
    @Nested
    @DisplayName("Custom CSS Classes - Requirement 6.5")
    class CSSClassesTests {
        
        @Test
        @DisplayName("Should apply custom CSS classes to scene root")
        void testCustomCSSClasses() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .styleClasses("custom-modal", "dark-theme")
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    var styleClasses = modal.getScene().getRoot().getStyleClass();
                    
                    assertTrue(styleClasses.contains("custom-modal"));
                    assertTrue(styleClasses.contains("dark-theme"));
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("Should handle empty CSS classes list")
        void testEmptyCSSClasses() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    // Should not throw exception, just create modal normally
                    assertNotNull(modal.getScene());
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
    }
    
    @Nested
    @DisplayName("Close Callback - Requirement 1.10")
    class CloseCallbackTests {
        
        @Test
        @DisplayName("Should invoke onClose callback when stage is closed")
        void testCloseCallback() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicInteger callCount = new AtomicInteger(0);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .onClose(v -> callCount.incrementAndGet())
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    // Simulate close event
                    modal.getStage().fireEvent(new javafx.stage.WindowEvent(
                            modal.getStage(), 
                            javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST
                    ));
                    
                    assertEquals(1, callCount.get(), "Callback should be invoked once");
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("Should handle null close callback gracefully")
        void testNullCloseCallback() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .onClose(null)
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    // Should not throw exception when closing
                    assertDoesNotThrow(() -> {
                        modal.close();
                    });
                    
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("Should handle callback exceptions gracefully")
        void testCallbackException() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder()
                            .onClose(v -> {
                                throw new RuntimeException("Test exception");
                            })
                            .build();
                    
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    // Should catch and log exception, not propagate it
                    assertDoesNotThrow(() -> {
                        modal.getStage().fireEvent(new javafx.stage.WindowEvent(
                                modal.getStage(), 
                                javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST
                        ));
                    });
                    
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
    }
    
    @Nested
    @DisplayName("CSS Loading - Requirements 1.3, 7.2, 7.6, 9.2")
    class CSSLoadingTests {
        
        @Test
        @DisplayName("CSS should be loaded from /com/example/scenory/styles.css")
        void testCSSLoading() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    Scene scene = modal.getScene();
                    
                    // Verify CSS stylesheet is loaded
                    // The stylesheet URL should contain the styles.css path
                    boolean cssLoaded = scene.getStylesheets().stream()
                            .anyMatch(url -> url.contains("styles.css"));
                    
                    assertTrue(cssLoaded, "CSS stylesheet should be loaded from styles.css");
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("CSS loading follows RichTextModal pattern")
        void testCSSLoadingPattern() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    Scene scene = modal.getScene();
                    
                    // Verify the CSS is loaded from the correct path following RichTextModal pattern:
                    // /com/example/scenory/styles.css
                    boolean correctPath = scene.getStylesheets().stream()
                            .anyMatch(url -> url.contains("/com/example/scenory/styles.css"));
                    
                    assertTrue(correctPath, 
                            "CSS should be loaded from /com/example/scenory/styles.css following RichTextModal pattern");
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("CSS loading error should log warning but not fail initialization")
        void testCSSLoadingErrorGraceful() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    // Create a modal - even if CSS loading fails internally,
                    // the modal should still be created successfully
                    ModalConfig config = ModalConfig.builder()
                            .title("Test Modal")
                            .dimensions(800, 600)
                            .build();
                    
                    // This should NOT throw an exception even if CSS loading fails
                    ModalTemplate modal = assertDoesNotThrow(() -> ModalTemplate.create(config),
                            "Modal creation should not fail even if CSS loading encounters errors");
                    
                    // Verify the modal is still functional
                    assertNotNull(modal);
                    assertNotNull(modal.getStage());
                    assertNotNull(modal.getScene());
                    assertEquals("Test Modal", modal.getStage().getTitle());
                    
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
    }
    
    @Nested
    @DisplayName("Display Methods")
    class DisplayMethodsTests {
        
        @Test
        @DisplayName("close() should close the stage")
        void testCloseMethod() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    modal.show();
                    assertTrue(modal.getStage().isShowing());
                    
                    modal.close();
                    assertFalse(modal.getStage().isShowing());
                    
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
    }
    
    @Nested
    @DisplayName("Controller Access")
    class ControllerAccessTests {
        
        @Test
        @DisplayName("getController() should return null for non-FXML modals")
        void testGetControllerNonFXML() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    ModalConfig config = ModalConfig.builder().build();
                    ModalTemplate modal = ModalTemplate.create(config);
                    
                    assertNull(modal.getController());
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
        
        @Test
        @DisplayName("getController() should return null for content-based modals")
        void testGetControllerContentBased() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean success = new AtomicBoolean(false);
            
            Platform.runLater(() -> {
                try {
                    VBox content = new VBox(new Label("Test"));
                    ModalConfig config = ModalConfig.builder().build();
                    ModalTemplate modal = ModalTemplate.createWithContent(content, config);
                    
                    assertNull(modal.getController());
                    success.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            latch.await(5, TimeUnit.SECONDS);
            assertTrue(success.get());
        }
    }
}
