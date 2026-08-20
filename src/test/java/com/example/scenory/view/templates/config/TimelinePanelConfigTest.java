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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Unit tests for {@link TimelinePanelConfig} class.
 * <p>
 * Tests configuration validation, default values, builder pattern,
 * and callback functionality.
 * </p>
 * 
 * <h3>Validated Requirements:</h3>
 * <ul>
 *   <li><strong>Requirement 4.2</strong>: THE Timeline_Panel Template SHALL support adding frame markers with frame number labels</li>
 *   <li><strong>Requirement 4.3</strong>: THE Timeline_Panel Template SHALL display a playback button with play/pause toggle state</li>
 *   <li><strong>Requirement 4.4</strong>: WHEN the playback button is clicked, THE Timeline_Panel Template SHALL invoke the playback Consumer_Callback</li>
 *   <li><strong>Requirement 4.7</strong>: THE Timeline_Panel Template SHALL support an add frame button that invokes a Consumer_Callback when clicked</li>
 *   <li><strong>Requirement 6.1</strong>: THE Template_Component SHALL accept a Configuration_Object containing customization parameters</li>
 *   <li><strong>Requirement 6.2</strong>: WHEN a Configuration_Object is provided, THE Template_Component SHALL apply the specified title, dimensions, and styling options</li>
 *   <li><strong>Requirement 6.3</strong>: THE Template_Component SHALL provide default values for all configuration parameters</li>
 *   <li><strong>Requirement 6.4</strong>: WHEN a configuration parameter is not specified, THE Template_Component SHALL use the default value</li>
 * </ul>
 */
class TimelinePanelConfigTest {

    // ==================== Default Values Tests (Requirements 6.3, 6.4) ====================
    
    @Test
    void testDefaultValues() {
        // When: Creating config with no parameters
        TimelinePanelConfig config = TimelinePanelConfig.builder().build();
        
        // Then: All default values should be applied
        assertEquals(60.0, config.getFrameMarkerWidth(), 
            "Default frame marker width should be 60");
        assertEquals(40.0, config.getFrameMarkerHeight(), 
            "Default frame marker height should be 40");
        assertTrue(config.isShowPlaybackButton(), 
            "Default showPlaybackButton should be true");
        assertTrue(config.isShowAddFrameButton(), 
            "Default showAddFrameButton should be true");
        assertNull(config.getOnFrameSelect(), 
            "Default onFrameSelect callback should be null");
        assertNull(config.getOnPlaybackToggle(), 
            "Default onPlaybackToggle callback should be null");
        assertTrue(config.getStyleClasses().isEmpty(), 
            "Default style classes should be empty list");
    }
    
    // ==================== Builder Pattern Tests (Requirement 6.1, 6.2) ====================
    
    @Test
    void testBuilderProducesCorrectConfiguration() {
        // Given: Mock callbacks
        Consumer<Integer> frameSelectCallback = frameNum -> {};
        Consumer<Boolean> playbackToggleCallback = playing -> {};
        
        // When: Building config with custom values
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .frameMarkerDimensions(70, 50)
            .showPlaybackButton(false)
            .showAddFrameButton(false)
            .onFrameSelect(frameSelectCallback)
            .onPlaybackToggle(playbackToggleCallback)
            .styleClasses("custom-timeline", "dark-theme")
            .build();
        
        // Then: All values should match what was configured
        assertEquals(70.0, config.getFrameMarkerWidth());
        assertEquals(50.0, config.getFrameMarkerHeight());
        assertFalse(config.isShowPlaybackButton());
        assertFalse(config.isShowAddFrameButton());
        assertSame(frameSelectCallback, config.getOnFrameSelect());
        assertSame(playbackToggleCallback, config.getOnPlaybackToggle());
        assertEquals(2, config.getStyleClasses().size());
        assertTrue(config.getStyleClasses().contains("custom-timeline"));
        assertTrue(config.getStyleClasses().contains("dark-theme"));
    }
    
    @Test
    void testBuilderFluentAPI() {
        // When: Using builder in fluent style
        TimelinePanelConfig.Builder builder = TimelinePanelConfig.builder();
        
        // Then: Each method should return the builder for chaining
        assertSame(builder, builder.frameMarkerDimensions(70, 50));
        assertSame(builder, builder.frameMarkerWidth(60));
        assertSame(builder, builder.frameMarkerHeight(40));
        assertSame(builder, builder.showPlaybackButton(true));
        assertSame(builder, builder.showAddFrameButton(true));
        assertSame(builder, builder.onFrameSelect(frameNum -> {}));
        assertSame(builder, builder.onPlaybackToggle(playing -> {}));
        assertSame(builder, builder.styleClasses("class1"));
    }
    
    @Test
    void testFrameMarkerDimensionsMethod() {
        // When: Setting dimensions using combined method
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .frameMarkerDimensions(80, 60)
            .build();
        
        // Then: Both width and height should be set
        assertEquals(80.0, config.getFrameMarkerWidth());
        assertEquals(60.0, config.getFrameMarkerHeight());
    }
    
    @Test
    void testFrameMarkerWidthMethod() {
        // When: Setting width individually
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .frameMarkerWidth(100)
            .build();
        
        // Then: Width should be set, height should be default
        assertEquals(100.0, config.getFrameMarkerWidth());
        assertEquals(40.0, config.getFrameMarkerHeight());
    }
    
    @Test
    void testFrameMarkerHeightMethod() {
        // When: Setting height individually
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .frameMarkerHeight(80)
            .build();
        
        // Then: Height should be set, width should be default
        assertEquals(60.0, config.getFrameMarkerWidth());
        assertEquals(80.0, config.getFrameMarkerHeight());
    }
    
    // ==================== Validation Tests (Requirements 6.7, 9.4) ====================
    
    @Test
    void testValidConfiguration_accepted() {
        // When: Creating config with valid parameters (minimum positive values)
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .frameMarkerDimensions(0.1, 0.1)
            .build();
        
        // Then: No exception should be thrown
        assertEquals(0.1, config.getFrameMarkerWidth());
        assertEquals(0.1, config.getFrameMarkerHeight());
    }
    
    @Test
    void testZeroFrameMarkerWidth_throwsException() {
        // When: Frame marker width is zero
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimelinePanelConfig.builder()
                .frameMarkerWidth(0)
                .build()
        );
        
        // Then: Exception message should include component name and actual value
        assertTrue(exception.getMessage().contains("[TimelinePanelConfig]"), 
            "Error message should include component name");
        assertTrue(exception.getMessage().contains("0"), 
            "Error message should include the invalid value");
        assertTrue(exception.getMessage().toLowerCase().contains("positive"), 
            "Error message should mention 'positive' constraint");
    }
    
    @Test
    void testNegativeFrameMarkerWidth_throwsException() {
        // When: Frame marker width is negative
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimelinePanelConfig.builder()
                .frameMarkerWidth(-10)
                .build()
        );
        
        // Then: Exception message should indicate positive constraint
        assertTrue(exception.getMessage().contains("[TimelinePanelConfig]"));
        assertTrue(exception.getMessage().contains("-10"));
        assertTrue(exception.getMessage().toLowerCase().contains("positive"));
    }
    
    @Test
    void testZeroFrameMarkerHeight_throwsException() {
        // When: Frame marker height is zero
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimelinePanelConfig.builder()
                .frameMarkerHeight(0)
                .build()
        );
        
        // Then: Exception message should include component name and actual value
        assertTrue(exception.getMessage().contains("[TimelinePanelConfig]"), 
            "Error message should include component name");
        assertTrue(exception.getMessage().contains("0"), 
            "Error message should include the invalid value");
        assertTrue(exception.getMessage().toLowerCase().contains("positive"), 
            "Error message should mention 'positive' constraint");
    }
    
    @Test
    void testNegativeFrameMarkerHeight_throwsException() {
        // When: Frame marker height is negative
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimelinePanelConfig.builder()
                .frameMarkerHeight(-5)
                .build()
        );
        
        // Then: Exception message should indicate positive constraint
        assertTrue(exception.getMessage().contains("[TimelinePanelConfig]"));
        assertTrue(exception.getMessage().contains("-5"));
        assertTrue(exception.getMessage().toLowerCase().contains("positive"));
    }
    
    @Test
    void testNegativeDimensionsViaCombinedMethod_throwsException() {
        // When: Setting negative dimensions using combined method
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimelinePanelConfig.builder()
                .frameMarkerDimensions(-60, -40)
                .build()
        );
        
        // Then: Exception should be thrown for invalid dimensions
        assertTrue(exception.getMessage().contains("[TimelinePanelConfig]"));
    }
    
    // ==================== Callback Tests (Requirements 4.4, 4.7) ====================
    
    @Test
    void testOnFrameSelectCallback_canBeNull() {
        // When: Not setting frame select callback (leaving as default null)
        TimelinePanelConfig config = TimelinePanelConfig.builder().build();
        
        // Then: Callback should be null
        assertNull(config.getOnFrameSelect());
    }
    
    @Test
    void testOnFrameSelectCallback_canBeSet() {
        // Given: A frame select callback
        AtomicInteger capturedFrameNum = new AtomicInteger(-1);
        Consumer<Integer> callback = frameNum -> capturedFrameNum.set(frameNum);
        
        // When: Setting frame select callback
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .onFrameSelect(callback)
            .build();
        
        // Then: Callback should be stored and functional
        assertNotNull(config.getOnFrameSelect());
        config.getOnFrameSelect().accept(42);
        assertEquals(42, capturedFrameNum.get());
    }
    
    @Test
    void testOnPlaybackToggleCallback_canBeNull() {
        // When: Not setting playback toggle callback (leaving as default null)
        TimelinePanelConfig config = TimelinePanelConfig.builder().build();
        
        // Then: Callback should be null
        assertNull(config.getOnPlaybackToggle());
    }
    
    @Test
    void testOnPlaybackToggleCallback_canBeSet() {
        // Given: A playback toggle callback
        AtomicReference<Boolean> capturedState = new AtomicReference<>(null);
        Consumer<Boolean> callback = playing -> capturedState.set(playing);
        
        // When: Setting playback toggle callback
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .onPlaybackToggle(callback)
            .build();
        
        // Then: Callback should be stored and functional
        assertNotNull(config.getOnPlaybackToggle());
        config.getOnPlaybackToggle().accept(true);
        assertTrue(capturedState.get());
        config.getOnPlaybackToggle().accept(false);
        assertFalse(capturedState.get());
    }
    
    @Test
    void testCallbacksCanBeSetToNull() {
        // When: Explicitly setting callbacks to null
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .onFrameSelect(frameNum -> {})
            .onPlaybackToggle(playing -> {})
            .onFrameSelect(null)  // Override with null
            .onPlaybackToggle(null)  // Override with null
            .build();
        
        // Then: Callbacks should be null
        assertNull(config.getOnFrameSelect());
        assertNull(config.getOnPlaybackToggle());
    }
    
    // ==================== Boolean Flag Tests (Requirement 6.6) ====================
    
    @Test
    void testShowPlaybackButton_defaultTrue() {
        // When: Not setting showPlaybackButton
        TimelinePanelConfig config = TimelinePanelConfig.builder().build();
        
        // Then: Should default to true
        assertTrue(config.isShowPlaybackButton());
    }
    
    @Test
    void testShowPlaybackButton_canBeSetFalse() {
        // When: Setting showPlaybackButton to false
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .showPlaybackButton(false)
            .build();
        
        // Then: Should be false
        assertFalse(config.isShowPlaybackButton());
    }
    
    @Test
    void testShowAddFrameButton_defaultTrue() {
        // When: Not setting showAddFrameButton
        TimelinePanelConfig config = TimelinePanelConfig.builder().build();
        
        // Then: Should default to true
        assertTrue(config.isShowAddFrameButton());
    }
    
    @Test
    void testShowAddFrameButton_canBeSetFalse() {
        // When: Setting showAddFrameButton to false
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .showAddFrameButton(false)
            .build();
        
        // Then: Should be false
        assertFalse(config.isShowAddFrameButton());
    }
    
    // ==================== Style Classes Tests ====================
    
    @Test
    void testStyleClasses_defensiveCopy() {
        // When: Creating config with style classes
        TimelinePanelConfig config = TimelinePanelConfig.builder()
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
        TimelinePanelConfig config = TimelinePanelConfig.builder()
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
        TimelinePanelConfig config = TimelinePanelConfig.builder().build();
        
        // Then: Style classes list should be empty
        assertNotNull(config.getStyleClasses());
        assertTrue(config.getStyleClasses().isEmpty());
    }
    
    // ==================== Integration Tests ====================
    
    @Test
    void testCompleteConfiguration() {
        // Given: All configuration options
        AtomicInteger selectedFrame = new AtomicInteger(-1);
        AtomicReference<Boolean> playbackState = new AtomicReference<>(null);
        
        // When: Building a complete configuration
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .frameMarkerDimensions(100, 80)
            .showPlaybackButton(true)
            .showAddFrameButton(true)
            .onFrameSelect(frameNum -> selectedFrame.set(frameNum))
            .onPlaybackToggle(playing -> playbackState.set(playing))
            .styleClasses("timeline", "horizontal", "dark-mode")
            .build();
        
        // Then: All configuration should be correct
        assertEquals(100.0, config.getFrameMarkerWidth());
        assertEquals(80.0, config.getFrameMarkerHeight());
        assertTrue(config.isShowPlaybackButton());
        assertTrue(config.isShowAddFrameButton());
        assertNotNull(config.getOnFrameSelect());
        assertNotNull(config.getOnPlaybackToggle());
        assertEquals(3, config.getStyleClasses().size());
        
        // And: Callbacks should be functional
        config.getOnFrameSelect().accept(10);
        assertEquals(10, selectedFrame.get());
        
        config.getOnPlaybackToggle().accept(true);
        assertTrue(playbackState.get());
    }
    
    @Test
    void testMinimalConfiguration() {
        // When: Building with only defaults
        TimelinePanelConfig config = TimelinePanelConfig.builder().build();
        
        // Then: Should have all default values and be valid
        assertNotNull(config);
        assertEquals(60.0, config.getFrameMarkerWidth());
        assertEquals(40.0, config.getFrameMarkerHeight());
        assertTrue(config.isShowPlaybackButton());
        assertTrue(config.isShowAddFrameButton());
    }
}
