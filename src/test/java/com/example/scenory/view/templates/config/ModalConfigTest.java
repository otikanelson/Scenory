package com.example.scenory.view.templates.config;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ModalConfig builder pattern and validation.
 * <p>
 * Tests cover:
 * - Default values
 * - Builder fluent API
 * - Configuration validation
 * - Defensive copying
 * - Error handling
 * </p>
 */
@DisplayName("ModalConfig Tests")
class ModalConfigTest {
    
    @Nested
    @DisplayName("Default Values")
    class DefaultValuesTests {
        
        @Test
        @DisplayName("Builder with no configuration should apply default values")
        void testDefaultValues() {
            ModalConfig config = ModalConfig.builder().build();
            
            assertEquals("Dialog", config.getTitle(), "Default title should be 'Dialog'");
            assertEquals(600, config.getWidth(), "Default width should be 600");
            assertEquals(400, config.getHeight(), "Default height should be 400");
            assertNull(config.getMinWidth(), "Default minWidth should be null");
            assertNull(config.getMinHeight(), "Default minHeight should be null");
            assertTrue(config.isResizable(), "Default resizable should be true");
            assertTrue(config.isCloseable(), "Default closeable should be true");
            assertNull(config.getOwnerWindow(), "Default ownerWindow should be null");
            assertTrue(config.getStyleClasses().isEmpty(), "Default styleClasses should be empty");
            assertNull(config.getOnClose(), "Default onClose callback should be null");
        }
    }
    
    @Nested
    @DisplayName("Builder Pattern")
    class BuilderPatternTests {
        
        @Test
        @DisplayName("Builder should support fluent API for title")
        void testBuilderTitle() {
            ModalConfig config = ModalConfig.builder()
                    .title("Custom Title")
                    .build();
            
            assertEquals("Custom Title", config.getTitle());
        }
        
        @Test
        @DisplayName("Builder should support fluent API for dimensions")
        void testBuilderDimensions() {
            ModalConfig config = ModalConfig.builder()
                    .dimensions(800, 600)
                    .build();
            
            assertEquals(800, config.getWidth());
            assertEquals(600, config.getHeight());
        }
        
        @Test
        @DisplayName("Builder should support fluent API for minimum dimensions")
        void testBuilderMinDimensions() {
            ModalConfig config = ModalConfig.builder()
                    .dimensions(800, 600)
                    .minDimensions(640, 480)
                    .build();
            
            assertEquals(640, config.getMinWidth());
            assertEquals(480, config.getMinHeight());
        }
        
        @Test
        @DisplayName("Builder should support fluent API for resizable flag")
        void testBuilderResizable() {
            ModalConfig config = ModalConfig.builder()
                    .resizable(false)
                    .build();
            
            assertFalse(config.isResizable());
        }
        
        @Test
        @DisplayName("Builder should support fluent API for closeable flag")
        void testBuilderCloseable() {
            ModalConfig config = ModalConfig.builder()
                    .closeable(false)
                    .build();
            
            assertFalse(config.isCloseable());
        }
        
        @Test
        @DisplayName("Builder should support fluent API for style classes")
        void testBuilderStyleClasses() {
            ModalConfig config = ModalConfig.builder()
                    .styleClasses("class1", "class2", "class3")
                    .build();
            
            List<String> styleClasses = config.getStyleClasses();
            assertEquals(3, styleClasses.size());
            assertTrue(styleClasses.contains("class1"));
            assertTrue(styleClasses.contains("class2"));
            assertTrue(styleClasses.contains("class3"));
        }
        
        @Test
        @DisplayName("Builder should support multiple styleClasses calls")
        void testBuilderMultipleStyleClassesCalls() {
            ModalConfig config = ModalConfig.builder()
                    .styleClasses("class1")
                    .styleClasses("class2", "class3")
                    .build();
            
            List<String> styleClasses = config.getStyleClasses();
            assertEquals(3, styleClasses.size());
        }
        
        @Test
        @DisplayName("Builder should support fluent API for onClose callback")
        void testBuilderOnClose() {
            Consumer<Void> callback = v -> {};
            ModalConfig config = ModalConfig.builder()
                    .onClose(callback)
                    .build();
            
            assertSame(callback, config.getOnClose());
        }
        
        @Test
        @DisplayName("Builder should support method chaining")
        void testBuilderChaining() {
            Consumer<Void> callback = v -> {};
            
            ModalConfig config = ModalConfig.builder()
                    .title("Test Dialog")
                    .dimensions(1024, 768)
                    .minDimensions(800, 600)
                    .resizable(true)
                    .closeable(false)
                    .styleClasses("dark-theme", "custom-modal")
                    .onClose(callback)
                    .build();
            
            assertEquals("Test Dialog", config.getTitle());
            assertEquals(1024, config.getWidth());
            assertEquals(768, config.getHeight());
            assertEquals(800, config.getMinWidth());
            assertEquals(600, config.getMinHeight());
            assertTrue(config.isResizable());
            assertFalse(config.isCloseable());
            assertEquals(2, config.getStyleClasses().size());
            assertSame(callback, config.getOnClose());
        }
    }
    
    @Nested
    @DisplayName("Validation")
    class ValidationTests {
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for negative width")
        void testNegativeWidth() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder().dimensions(-100, 600).build()
            );
            
            assertTrue(exception.getMessage().contains("Width must be positive"));
            assertTrue(exception.getMessage().contains("-100"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for zero width")
        void testZeroWidth() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder().dimensions(0, 600).build()
            );
            
            assertTrue(exception.getMessage().contains("Width must be positive"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for negative height")
        void testNegativeHeight() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder().dimensions(800, -400).build()
            );
            
            assertTrue(exception.getMessage().contains("Height must be positive"));
            assertTrue(exception.getMessage().contains("-400"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for zero height")
        void testZeroHeight() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder().dimensions(800, 0).build()
            );
            
            assertTrue(exception.getMessage().contains("Height must be positive"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for negative min width")
        void testNegativeMinWidth() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder()
                            .dimensions(800, 600)
                            .minDimensions(-100, 400)
                            .build()
            );
            
            assertTrue(exception.getMessage().contains("Min width must be positive"));
            assertTrue(exception.getMessage().contains("-100"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for zero min width")
        void testZeroMinWidth() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder()
                            .dimensions(800, 600)
                            .minDimensions(0, 400)
                            .build()
            );
            
            assertTrue(exception.getMessage().contains("Min width must be positive"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for negative min height")
        void testNegativeMinHeight() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder()
                            .dimensions(800, 600)
                            .minDimensions(640, -300)
                            .build()
            );
            
            assertTrue(exception.getMessage().contains("Min height must be positive"));
            assertTrue(exception.getMessage().contains("-300"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException for zero min height")
        void testZeroMinHeight() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder()
                            .dimensions(800, 600)
                            .minDimensions(640, 0)
                            .build()
            );
            
            assertTrue(exception.getMessage().contains("Min height must be positive"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException when min width exceeds width")
        void testMinWidthExceedsWidth() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder()
                            .dimensions(800, 600)
                            .minDimensions(900, 500)
                            .build()
            );
            
            assertTrue(exception.getMessage().contains("Min width cannot exceed width"));
            assertTrue(exception.getMessage().contains("900"));
            assertTrue(exception.getMessage().contains("800"));
        }
        
        @Test
        @DisplayName("Should throw IllegalArgumentException when min height exceeds height")
        void testMinHeightExceedsHeight() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ModalConfig.builder()
                            .dimensions(800, 600)
                            .minDimensions(700, 700)
                            .build()
            );
            
            assertTrue(exception.getMessage().contains("Min height cannot exceed height"));
            assertTrue(exception.getMessage().contains("700"));
            assertTrue(exception.getMessage().contains("600"));
        }
        
        @Test
        @DisplayName("Should accept min dimensions equal to dimensions")
        void testMinDimensionsEqualToDimensions() {
            assertDoesNotThrow(() -> {
                ModalConfig config = ModalConfig.builder()
                        .dimensions(800, 600)
                        .minDimensions(800, 600)
                        .build();
                
                assertEquals(800, config.getWidth());
                assertEquals(600, config.getHeight());
                assertEquals(800, config.getMinWidth());
                assertEquals(600, config.getMinHeight());
            });
        }
        
        @Test
        @DisplayName("Should accept valid min dimensions less than dimensions")
        void testValidMinDimensions() {
            assertDoesNotThrow(() -> {
                ModalConfig config = ModalConfig.builder()
                        .dimensions(1024, 768)
                        .minDimensions(800, 600)
                        .build();
                
                assertEquals(1024, config.getWidth());
                assertEquals(768, config.getHeight());
                assertEquals(800, config.getMinWidth());
                assertEquals(600, config.getMinHeight());
            });
        }
    }
    
    @Nested
    @DisplayName("Defensive Copying")
    class DefensiveCopyingTests {
        
        @Test
        @DisplayName("Should return defensive copy of style classes list")
        void testStyleClassesDefensiveCopy() {
            ModalConfig config = ModalConfig.builder()
                    .styleClasses("class1", "class2")
                    .build();
            
            List<String> styleClasses1 = config.getStyleClasses();
            List<String> styleClasses2 = config.getStyleClasses();
            
            // Should be different list instances
            assertNotSame(styleClasses1, styleClasses2);
            
            // But with same content
            assertEquals(styleClasses1, styleClasses2);
        }
        
        @Test
        @DisplayName("Modifying returned style classes should not affect config")
        void testStyleClassesImmutability() {
            ModalConfig config = ModalConfig.builder()
                    .styleClasses("class1", "class2")
                    .build();
            
            List<String> styleClasses = config.getStyleClasses();
            styleClasses.add("class3");
            
            // Original config should be unchanged
            assertEquals(2, config.getStyleClasses().size());
        }
    }
    
    @Nested
    @DisplayName("Callback Functionality")
    class CallbackTests {
        
        @Test
        @DisplayName("OnClose callback should be stored and retrievable")
        void testOnCloseCallback() {
            AtomicInteger callCount = new AtomicInteger(0);
            Consumer<Void> callback = v -> callCount.incrementAndGet();
            
            ModalConfig config = ModalConfig.builder()
                    .onClose(callback)
                    .build();
            
            Consumer<Void> retrievedCallback = config.getOnClose();
            assertNotNull(retrievedCallback);
            
            // Test that callback works
            retrievedCallback.accept(null);
            assertEquals(1, callCount.get());
        }
        
        @Test
        @DisplayName("OnClose callback can be null")
        void testNullOnCloseCallback() {
            ModalConfig config = ModalConfig.builder()
                    .onClose(null)
                    .build();
            
            assertNull(config.getOnClose());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should accept very small positive dimensions")
        void testSmallPositiveDimensions() {
            assertDoesNotThrow(() -> {
                ModalConfig config = ModalConfig.builder()
                        .dimensions(1, 1)
                        .build();
                
                assertEquals(1, config.getWidth());
                assertEquals(1, config.getHeight());
            });
        }
        
        @Test
        @DisplayName("Should accept very large dimensions")
        void testLargeDimensions() {
            assertDoesNotThrow(() -> {
                ModalConfig config = ModalConfig.builder()
                        .dimensions(10000, 10000)
                        .build();
                
                assertEquals(10000, config.getWidth());
                assertEquals(10000, config.getHeight());
            });
        }
        
        @Test
        @DisplayName("Should handle empty style classes array")
        void testEmptyStyleClasses() {
            ModalConfig config = ModalConfig.builder()
                    .styleClasses()
                    .build();
            
            assertTrue(config.getStyleClasses().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle null title")
        void testNullTitle() {
            ModalConfig config = ModalConfig.builder()
                    .title(null)
                    .build();
            
            assertNull(config.getTitle());
        }
    }
    
    @Nested
    @DisplayName("Complete Configuration")
    class CompleteConfigurationTests {
        
        @Test
        @DisplayName("Should create complete configuration with all fields")
        void testCompleteConfiguration() {
            AtomicInteger closeCallCount = new AtomicInteger(0);
            Consumer<Void> closeCallback = v -> closeCallCount.incrementAndGet();
            
            ModalConfig config = ModalConfig.builder()
                    .title("Test Modal Dialog")
                    .dimensions(1280, 720)
                    .minDimensions(800, 600)
                    .resizable(true)
                    .closeable(false)
                    .styleClasses("dark-theme", "custom-modal", "large")
                    .onClose(closeCallback)
                    .build();
            
            // Verify all fields
            assertEquals("Test Modal Dialog", config.getTitle());
            assertEquals(1280, config.getWidth());
            assertEquals(720, config.getHeight());
            assertEquals(800, config.getMinWidth());
            assertEquals(600, config.getMinHeight());
            assertTrue(config.isResizable());
            assertFalse(config.isCloseable());
            assertEquals(3, config.getStyleClasses().size());
            assertNotNull(config.getOnClose());
            
            // Test callback
            config.getOnClose().accept(null);
            assertEquals(1, closeCallCount.get());
        }
    }
}
