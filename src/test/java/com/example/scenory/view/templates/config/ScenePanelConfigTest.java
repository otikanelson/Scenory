package com.example.scenory.view.templates.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Unit tests for {@link ScenePanelConfig} class.
 * <p>
 * Tests configuration validation, default values, builder pattern,
 * and callback handling.
 * </p>
 * 
 * <h3>Validated Requirements:</h3>
 * <ul>
 *   <li><strong>Requirement 3.2</strong>: THE Scene_Panel Template SHALL support a scrollable content area for multiple frames</li>
 *   <li><strong>Requirement 3.4</strong>: WHEN a frame item is added, THE Scene_Panel Template SHALL display the thumbnail and label in the panel</li>
 *   <li><strong>Requirement 3.9</strong>: THE Scene_Panel Template SHALL support optional layer view mode for displaying layer hierarchy</li>
 *   <li><strong>Requirement 6.1</strong>: THE Template_Component SHALL accept a Configuration_Object containing customization parameters</li>
 *   <li><strong>Requirement 6.2</strong>: WHEN a Configuration_Object is provided, THE Template_Component SHALL apply the specified title, dimensions, and styling options</li>
 *   <li><strong>Requirement 6.3</strong>: THE Template_Component SHALL provide default values for all configuration parameters</li>
 *   <li><strong>Requirement 6.4</strong>: WHEN a configuration parameter is not specified, THE Template_Component SHALL use the default value</li>
 *   <li><strong>Requirement 6.7</strong>: WHEN a Template_Component is instantiated, THE Template_Component SHALL validate the Configuration_Object parameters</li>
 *   <li><strong>Requirement 9.4</strong>: THE Template_Component SHALL validate required Configuration_Object parameters and throw IllegalArgumentException for invalid values</li>
 * </ul>
 */
class ScenePanelConfigTest {

    // ==================== Default Values Tests (Requirements 6.3, 6.4) ====================
    
    @Test
    void testDefaultValues() {
        // When: Creating config with no parameters
        ScenePanelConfig config = ScenePanelConfig.builder().build();
        
        // Then: All default values should be applied
        assertEquals(120.0, config.getThumbnailWidth(), 
            "Default thumbnail width should be 120");
        assertEquals(90.0, config.getThumbnailHeight(), 
            "Default thumbnail height should be 90");
        assertTrue(config.isShowFrameLabels(), 
            "Default showFrameLabels should be true");
        assertFalse(config.isEnableLayerMode(), 
            "Default enableLayerMode should be false");
        assertNull(config.getOnFrameSelect(), 
            "Default onFrameSelect callback should be null");
        assertNull(config.getOnFrameDoubleClick(), 
            "Default onFrameDoubleClick callback should be null");
        assertTrue(config.getStyleClasses().isEmpty(), 
            "Default style classes should be empty list");
    }
    
    // ==================== Builder Pattern Tests (Requirement 6.1, 6.2) ====================
    
    @Test
    void testBuilderProducesCorrectConfiguration() {
        // Given: Callback consumers
        Consumer<String> selectCallback = frameId -> {};
        Consumer<String> doubleClickCallback = frameId -> {};
        
        // When: Building config with custom values
        ScenePanelConfig config = ScenePanelConfig.builder()
            .thumbnailWidth(160.0)
            .thumbnailHeight(120.0)
            .showFrameLabels(false)
            .enableLayerMode(true)
            .onFrameSelect(selectCallback)
            .onFrameDoubleClick(doubleClickCallback)
            .styleClasses("custom-class", "dark-theme")
            .build();
        
        // Then: All values should match what was configured
        assertEquals(160.0, config.getThumbnailWidth());
        assertEquals(120.0, config.getThumbnailHeight());
        assertFalse(config.isShowFrameLabels());
        assertTrue(config.isEnableLayerMode());
        assertSame(selectCallback, config.getOnFrameSelect());
        assertSame(doubleClickCallback, config.getOnFrameDoubleClick());
        assertEquals(2, config.getStyleClasses().size());
        assertTrue(config.getStyleClasses().contains("custom-class"));
        assertTrue(config.getStyleClasses().contains("dark-theme"));
    }
    
    @Test
    void testBuilderThumbnailDimensionsMethod() {
        // When: Using thumbnailDimensions convenience method
        ScenePanelConfig config = ScenePanelConfig.builder()
            .thumbnailDimensions(200, 150)
            .build();
        
        // Then: Both width and height should be set
        assertEquals(200.0, config.getThumbnailWidth());
        assertEquals(150.0, config.getThumbnailHeight());
    }
    
    @Test
    void testBuilderFluentAPI() {
        // When: Using builder in fluent style
        ScenePanelConfig.Builder builder = ScenePanelConfig.builder();
        
        // Then: Each method should return the builder for chaining
        assertSame(builder, builder.thumbnailWidth(160));
        assertSame(builder, builder.thumbnailHeight(120));
        assertSame(builder, builder.thumbnailDimensions(160, 120));
        assertSame(builder, builder.showFrameLabels(false));
        assertSame(builder, builder.enableLayerMode(true));
        assertSame(builder, builder.onFrameSelect(null));
        assertSame(builder, builder.onFrameDoubleClick(null));
        assertSame(builder, builder.styleClasses("class1"));
    }
    
    // ==================== Validation Tests (Requirements 6.7, 9.4) ====================
    
    @Test
    void testValidConfiguration_accepted() {
        // When: Creating config with valid parameters (minimum positive values)
        ScenePanelConfig config = ScenePanelConfig.builder()
            .thumbnailWidth(1)
            .thumbnailHeight(1)
            .build();
        
        // Then: No exception should be thrown
        assertEquals(1.0, config.getThumbnailWidth());
        assertEquals(1.0, config.getThumbnailHeight());
    }
    
    @Test
    void testZeroThumbnailWidth_throwsException() {
        // When: Thumbnail width is zero
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ScenePanelConfig.builder()
                .thumbnailWidth(0)
                .build()
        );
        
        // Then: Exception message should include component name and actual value
        assertTrue(exception.getMessage().contains("[ScenePanelConfig]"), 
            "Error message should include component name");
        assertTrue(exception.getMessage().contains("0"), 
            "Error message should include the invalid value");
        assertTrue(exception.getMessage().toLowerCase().contains("positive"), 
            "Error message should mention 'positive' constraint");
    }
    
    @Test
    void testNegativeThumbnailWidth_throwsException() {
        // When: Thumbnail width is negative
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ScenePanelConfig.builder()
                .thumbnailWidth(-120)
                .build()
        );
        
        // Then: Exception message should include component name and actual value
        assertTrue(exception.getMessage().contains("[ScenePanelConfig]"));
        assertTrue(exception.getMessage().contains("-120"));
        assertTrue(exception.getMessage().toLowerCase().contains("positive"));
    }
    
    @Test
    void testZeroThumbnailHeight_throwsException() {
        // When: Thumbnail height is zero
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ScenePanelConfig.builder()
                .thumbnailHeight(0)
                .build()
        );
        
        // Then: Exception message should include component name and actual value
        assertTrue(exception.getMessage().contains("[ScenePanelConfig]"));
        assertTrue(exception.getMessage().contains("0"));
        assertTrue(exception.getMessage().toLowerCase().contains("positive"));
    }
    
    @Test
    void testNegativeThumbnailHeight_throwsException() {
        // When: Thumbnail height is negative
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ScenePanelConfig.builder()
                .thumbnailHeight(-90)
                .build()
        );
        
        // Then: Exception message should include component name and actual value
        assertTrue(exception.getMessage().contains("[ScenePanelConfig]"));
        assertTrue(exception.getMessage().contains("-90"));
        assertTrue(exception.getMessage().toLowerCase().contains("positive"));
    }
    
    @Test
    void testThumbnailDimensionsMethod_bothInvalid_throwsException() {
        // When: Using thumbnailDimensions with invalid values
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ScenePanelConfig.builder()
                .thumbnailDimensions(-100, -100)
                .build()
        );
        
        // Then: Exception should be thrown for negative values
        assertTrue(exception.getMessage().contains("[ScenePanelConfig]"));
    }
    
    // ==================== Callback Tests ====================
    
    @Test
    void testOnFrameSelectCallback_isStored() {
        // Given: A callback that captures the frame ID
        AtomicReference<String> capturedFrameId = new AtomicReference<>();
        Consumer<String> callback = capturedFrameId::set;
        
        // When: Building config with callback
        ScenePanelConfig config = ScenePanelConfig.builder()
            .onFrameSelect(callback)
            .build();
        
        // Then: Callback should be stored and functional
        assertNotNull(config.getOnFrameSelect());
        config.getOnFrameSelect().accept("frame-1");
        assertEquals("frame-1", capturedFrameId.get());
    }
    
    @Test
    void testOnFrameDoubleClickCallback_isStored() {
        // Given: A callback that captures the frame ID
        AtomicReference<String> capturedFrameId = new AtomicReference<>();
        Consumer<String> callback = capturedFrameId::set;
        
        // When: Building config with callback
        ScenePanelConfig config = ScenePanelConfig.builder()
            .onFrameDoubleClick(callback)
            .build();
        
        // Then: Callback should be stored and functional
        assertNotNull(config.getOnFrameDoubleClick());
        config.getOnFrameDoubleClick().accept("frame-2");
        assertEquals("frame-2", capturedFrameId.get());
    }
    
    @Test
    void testCallbacks_canBeNull() {
        // When: Not setting callbacks (leaving as default null)
        ScenePanelConfig config = ScenePanelConfig.builder().build();
        
        // Then: Callbacks should be null
        assertNull(config.getOnFrameSelect());
        assertNull(config.getOnFrameDoubleClick());
    }
    
    @Test
    void testCallbacks_canBeExplicitlyNull() {
        // When: Explicitly setting callbacks to null
        ScenePanelConfig config = ScenePanelConfig.builder()
            .onFrameSelect(frameId -> {})
            .onFrameSelect(null)  // Override with null
            .onFrameDoubleClick(frameId -> {})
            .onFrameDoubleClick(null)  // Override with null
            .build();
        
        // Then: Callbacks should be null
        assertNull(config.getOnFrameSelect());
        assertNull(config.getOnFrameDoubleClick());
    }
    
    // ==================== Boolean Flag Tests (Requirement 6.6) ====================
    
    @Test
    void testShowFrameLabels_canBeTrue() {
        // When: Setting showFrameLabels to true
        ScenePanelConfig config = ScenePanelConfig.builder()
            .showFrameLabels(true)
            .build();
        
        // Then: Flag should be true
        assertTrue(config.isShowFrameLabels());
    }
    
    @Test
    void testShowFrameLabels_canBeFalse() {
        // When: Setting showFrameLabels to false
        ScenePanelConfig config = ScenePanelConfig.builder()
            .showFrameLabels(false)
            .build();
        
        // Then: Flag should be false
        assertFalse(config.isShowFrameLabels());
    }
    
    @Test
    void testEnableLayerMode_canBeTrue() {
        // When: Setting enableLayerMode to true (Requirement 3.9)
        ScenePanelConfig config = ScenePanelConfig.builder()
            .enableLayerMode(true)
            .build();
        
        // Then: Flag should be true
        assertTrue(config.isEnableLayerMode());
    }
    
    @Test
    void testEnableLayerMode_canBeFalse() {
        // When: Setting enableLayerMode to false
        ScenePanelConfig config = ScenePanelConfig.builder()
            .enableLayerMode(false)
            .build();
        
        // Then: Flag should be false
        assertFalse(config.isEnableLayerMode());
    }
    
    // ==================== Style Classes Tests ====================
    
    @Test
    void testStyleClasses_defensiveCopy() {
        // When: Creating config with style classes
        ScenePanelConfig config = ScenePanelConfig.builder()
            .styleClasses("class1", "class2")
            .build();
        
        // Then: Getting style classes should return a defensive copy
        var classes1 = config.getStyleClasses();
        var classes2 = config.getStyleClasses();
        
        assertNotSame(classes1, classes2, 
            "Multiple calls to getStyleClasses should return different list instances");
        assertEquals(classes1, classes2, 
            "Multiple calls should return equal lists");
        
        // When: Modifying returned list
        classes1.add("should-not-affect-config");
        
        // Then: Config should remain unchanged
        assertEquals(2, config.getStyleClasses().size(), 
            "Config should not be affected by modifications to returned list");
    }
    
    @Test
    void testStyleClasses_multipleCallsToBuilder() {
        // When: Calling styleClasses multiple times on builder
        ScenePanelConfig config = ScenePanelConfig.builder()
            .styleClasses("class1", "class2")
            .styleClasses("class3")
            .build();
        
        // Then: All classes should be accumulated
        assertEquals(3, config.getStyleClasses().size());
        assertTrue(config.getStyleClasses().contains("class1"));
        assertTrue(config.getStyleClasses().contains("class2"));
        assertTrue(config.getStyleClasses().contains("class3"));
    }
    
    @Test
    void testStyleClasses_emptyByDefault() {
        // When: Not setting style classes
        ScenePanelConfig config = ScenePanelConfig.builder().build();
        
        // Then: Style classes should be empty list (not null)
        assertNotNull(config.getStyleClasses());
        assertTrue(config.getStyleClasses().isEmpty());
    }
    
    // ==================== Integration Tests ====================
    
    @Test
    void testCompleteConfiguration_allParametersSet() {
        // Given: All configuration parameters
        Consumer<String> selectCallback = frameId -> System.out.println("Selected: " + frameId);
        Consumer<String> doubleClickCallback = frameId -> System.out.println("Double-clicked: " + frameId);
        
        // When: Building complete configuration
        ScenePanelConfig config = ScenePanelConfig.builder()
            .thumbnailWidth(200)
            .thumbnailHeight(150)
            .showFrameLabels(true)
            .enableLayerMode(true)
            .onFrameSelect(selectCallback)
            .onFrameDoubleClick(doubleClickCallback)
            .styleClasses("scene-panel", "custom-theme", "dark-mode")
            .build();
        
        // Then: All parameters should be correctly stored
        assertEquals(200.0, config.getThumbnailWidth());
        assertEquals(150.0, config.getThumbnailHeight());
        assertTrue(config.isShowFrameLabels());
        assertTrue(config.isEnableLayerMode());
        assertSame(selectCallback, config.getOnFrameSelect());
        assertSame(doubleClickCallback, config.getOnFrameDoubleClick());
        assertEquals(3, config.getStyleClasses().size());
        assertTrue(config.getStyleClasses().contains("scene-panel"));
        assertTrue(config.getStyleClasses().contains("custom-theme"));
        assertTrue(config.getStyleClasses().contains("dark-mode"));
    }
    
    @Test
    void testBuilderReuse_independentConfigs() {
        // When: Creating multiple configs from the same builder pattern
        ScenePanelConfig config1 = ScenePanelConfig.builder()
            .thumbnailWidth(100)
            .build();
        
        ScenePanelConfig config2 = ScenePanelConfig.builder()
            .thumbnailWidth(200)
            .build();
        
        // Then: Configs should be independent
        assertEquals(100.0, config1.getThumbnailWidth());
        assertEquals(200.0, config2.getThumbnailWidth());
        assertNotSame(config1, config2);
    }
}
