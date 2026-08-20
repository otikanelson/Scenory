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

/**
 * Unit tests for {@link ToolsPanelConfig} class.
 * <p>
 * Tests configuration validation, default values, builder pattern,
 * and the SelectionMode enum.
 * </p>
 * 
 * <h3>Validated Requirements:</h3>
 * <ul>
 *   <li><strong>Requirement 2.7</strong>: THE Tools_Panel Template SHALL apply consistent spacing between tool items</li>
 *   <li><strong>Requirement 6.1</strong>: THE Template_Component SHALL accept a Configuration_Object containing customization parameters</li>
 *   <li><strong>Requirement 6.2</strong>: WHEN a Configuration_Object is provided, THE Template_Component SHALL apply the specified title, dimensions, and styling options</li>
 *   <li><strong>Requirement 6.3</strong>: THE Template_Component SHALL provide default values for all configuration parameters</li>
 *   <li><strong>Requirement 6.4</strong>: WHEN a configuration parameter is not specified, THE Template_Component SHALL use the default value</li>
 *   <li><strong>Requirement 6.6</strong>: THE Configuration_Object SHALL support boolean flags for optional features (resizable, closeable, minimizable)</li>
 *   <li><strong>Requirement 6.7</strong>: WHEN a Template_Component is instantiated, THE Template_Component SHALL validate the Configuration_Object parameters</li>
 *   <li><strong>Requirement 9.4</strong>: THE Template_Component SHALL validate required Configuration_Object parameters and throw IllegalArgumentException for invalid values</li>
 * </ul>
 */
class ToolsPanelConfigTest {

    // ==================== Default Values Tests (Requirements 6.3, 6.4) ====================
    
    @Test
    void testDefaultValues() {
        // When: Creating config with no parameters
        ToolsPanelConfig config = ToolsPanelConfig.builder().build();
        
        // Then: All default values should be applied
        assertEquals(8.0, config.getToolSpacing(), 
            "Default tool spacing should be 8");
        assertTrue(config.isShowLabels(), 
            "Default showLabels should be true");
        assertEquals(24.0, config.getIconSize(), 
            "Default icon size should be 24");
        assertEquals(ToolsPanelConfig.SelectionMode.SINGLE, config.getSelectionMode(), 
            "Default selection mode should be SINGLE");
        assertNull(config.getInitialSelection(), 
            "Default initial selection should be null");
        assertTrue(config.getStyleClasses().isEmpty(), 
            "Default style classes should be empty list");
    }
    
    // ==================== Builder Pattern Tests (Requirement 6.1, 6.2) ====================
    
    @Test
    void testBuilderProducesCorrectConfiguration() {
        // When: Building config with custom values
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .toolSpacing(10.0)
            .showLabels(false)
            .iconSize(32.0)
            .selectionMode(ToolsPanelConfig.SelectionMode.MULTIPLE)
            .initialSelection("pencil")
            .styleClasses("custom-class", "dark-theme")
            .build();
        
        // Then: All values should match what was configured
        assertEquals(10.0, config.getToolSpacing());
        assertFalse(config.isShowLabels());
        assertEquals(32.0, config.getIconSize());
        assertEquals(ToolsPanelConfig.SelectionMode.MULTIPLE, config.getSelectionMode());
        assertEquals("pencil", config.getInitialSelection());
        assertEquals(2, config.getStyleClasses().size());
        assertTrue(config.getStyleClasses().contains("custom-class"));
        assertTrue(config.getStyleClasses().contains("dark-theme"));
    }
    
    @Test
    void testBuilderFluentAPI() {
        // When: Using builder in fluent style
        ToolsPanelConfig.Builder builder = ToolsPanelConfig.builder();
        
        // Then: Each method should return the builder for chaining
        assertSame(builder, builder.toolSpacing(10));
        assertSame(builder, builder.showLabels(false));
        assertSame(builder, builder.iconSize(32));
        assertSame(builder, builder.selectionMode(ToolsPanelConfig.SelectionMode.NONE));
        assertSame(builder, builder.initialSelection("eraser"));
        assertSame(builder, builder.styleClasses("class1"));
    }
    
    // ==================== Validation Tests (Requirements 6.7, 9.4) ====================
    
    @Test
    void testValidConfiguration_accepted() {
        // When: Creating config with valid parameters
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .toolSpacing(0)  // Zero is valid (non-negative)
            .iconSize(1)     // Minimum valid positive value
            .build();
        
        // Then: No exception should be thrown
        assertEquals(0, config.getToolSpacing());
        assertEquals(1, config.getIconSize());
    }
    
    @Test
    void testNegativeToolSpacing_throwsException() {
        // When: Tool spacing is negative
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ToolsPanelConfig.builder()
                .toolSpacing(-1.0)
                .build()
        );
        
        // Then: Exception message should include component name and actual value
        assertTrue(exception.getMessage().contains("[ToolsPanelConfig]"), 
            "Error message should include component name");
        assertTrue(exception.getMessage().contains("-1"), 
            "Error message should include the invalid value");
        assertTrue(exception.getMessage().toLowerCase().contains("non-negative"), 
            "Error message should mention 'non-negative' constraint");
    }
    
    @Test
    void testZeroIconSize_throwsException() {
        // When: Icon size is zero
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ToolsPanelConfig.builder()
                .iconSize(0)
                .build()
        );
        
        // Then: Exception message should include component name and actual value
        assertTrue(exception.getMessage().contains("[ToolsPanelConfig]"), 
            "Error message should include component name");
        assertTrue(exception.getMessage().contains("0"), 
            "Error message should include the invalid value");
        assertTrue(exception.getMessage().toLowerCase().contains("positive"), 
            "Error message should mention 'positive' constraint");
    }
    
    @Test
    void testNegativeIconSize_throwsException() {
        // When: Icon size is negative
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ToolsPanelConfig.builder()
                .iconSize(-24)
                .build()
        );
        
        // Then: Exception message should indicate positive constraint
        assertTrue(exception.getMessage().contains("[ToolsPanelConfig]"));
        assertTrue(exception.getMessage().contains("-24"));
    }
    
    @Test
    void testNullSelectionMode_throwsException() {
        // When: Selection mode is explicitly set to null
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ToolsPanelConfig.builder()
                .selectionMode(null)
                .build()
        );
        
        // Then: Exception message should mention null constraint
        assertTrue(exception.getMessage().contains("[ToolsPanelConfig]"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    // ==================== SelectionMode Enum Tests ====================
    
    @Test
    void testSelectionModeEnum_hasExpectedValues() {
        // Then: Enum should have exactly SINGLE, MULTIPLE, and NONE
        ToolsPanelConfig.SelectionMode[] modes = ToolsPanelConfig.SelectionMode.values();
        assertEquals(3, modes.length, "SelectionMode should have exactly 3 values");
        
        // Verify each expected value exists
        assertNotNull(ToolsPanelConfig.SelectionMode.valueOf("SINGLE"));
        assertNotNull(ToolsPanelConfig.SelectionMode.valueOf("MULTIPLE"));
        assertNotNull(ToolsPanelConfig.SelectionMode.valueOf("NONE"));
    }
    
    @Test
    void testSelectionModeEnum_singleMode() {
        // When: Using SINGLE selection mode
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .selectionMode(ToolsPanelConfig.SelectionMode.SINGLE)
            .build();
        
        // Then: Config should store SINGLE mode
        assertEquals(ToolsPanelConfig.SelectionMode.SINGLE, config.getSelectionMode());
    }
    
    @Test
    void testSelectionModeEnum_multipleMode() {
        // When: Using MULTIPLE selection mode
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .selectionMode(ToolsPanelConfig.SelectionMode.MULTIPLE)
            .build();
        
        // Then: Config should store MULTIPLE mode
        assertEquals(ToolsPanelConfig.SelectionMode.MULTIPLE, config.getSelectionMode());
    }
    
    @Test
    void testSelectionModeEnum_noneMode() {
        // When: Using NONE selection mode
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .selectionMode(ToolsPanelConfig.SelectionMode.NONE)
            .build();
        
        // Then: Config should store NONE mode
        assertEquals(ToolsPanelConfig.SelectionMode.NONE, config.getSelectionMode());
    }
    
    // ==================== Style Classes Tests ====================
    
    @Test
    void testStyleClasses_defensiveCopy() {
        // When: Creating config with style classes
        ToolsPanelConfig config = ToolsPanelConfig.builder()
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
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .styleClasses("class1", "class2")
            .styleClasses("class3")
            .build();
        
        // Then: All classes should be accumulated
        assertEquals(3, config.getStyleClasses().size());
        assertTrue(config.getStyleClasses().contains("class1"));
        assertTrue(config.getStyleClasses().contains("class2"));
        assertTrue(config.getStyleClasses().contains("class3"));
    }
    
    // ==================== Initial Selection Tests ====================
    
    @Test
    void testInitialSelection_canBeNull() {
        // When: Not setting initial selection (leaving as default null)
        ToolsPanelConfig config = ToolsPanelConfig.builder().build();
        
        // Then: Initial selection should be null
        assertNull(config.getInitialSelection());
    }
    
    @Test
    void testInitialSelection_canBeSet() {
        // When: Setting initial selection
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .initialSelection("pencil")
            .build();
        
        // Then: Initial selection should be stored
        assertEquals("pencil", config.getInitialSelection());
    }
    
    @Test
    void testInitialSelection_canBeExplicitlyNull() {
        // When: Explicitly setting initial selection to null
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .initialSelection("pencil")
            .initialSelection(null)  // Override with null
            .build();
        
        // Then: Initial selection should be null
        assertNull(config.getInitialSelection());
    }
}
