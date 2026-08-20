package com.example.scenory.view.templates.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration object for tools panel customization using the builder pattern.
 * <p>
 * This class provides a fluent API for configuring tools panels with validation
 * and sensible default values. It extends {@link PanelConfig} to inherit common
 * panel configuration properties.
 * </p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * ToolsPanelConfig config = ToolsPanelConfig.builder()
 *     .toolSpacing(10)
 *     .showLabels(true)
 *     .iconSize(32)
 *     .selectionMode(SelectionMode.SINGLE)
 *     .initialSelection("pencil")
 *     .styleClasses("custom-tools-panel", "dark-theme")
 *     .build();
 * }</pre>
 * 
 * <h3>Default Values:</h3>
 * <ul>
 *   <li>toolSpacing: 8 pixels</li>
 *   <li>showLabels: true</li>
 *   <li>iconSize: 24 pixels</li>
 *   <li>selectionMode: SelectionMode.SINGLE</li>
 *   <li>initialSelection: null (no initial selection)</li>
 *   <li>styleClasses: empty list</li>
 * </ul>
 * 
 * <h3>Validated Requirements:</h3>
 * <ul>
 *   <li><strong>Requirement 2.7</strong>: THE Tools_Panel Template SHALL apply consistent spacing between tool items</li>
 *   <li><strong>Requirement 6.1</strong>: THE Template_Component SHALL accept a Configuration_Object containing customization parameters</li>
 *   <li><strong>Requirement 6.2</strong>: WHEN a Configuration_Object is provided, THE Template_Component SHALL apply the specified title, dimensions, and styling options</li>
 *   <li><strong>Requirement 6.3</strong>: THE Template_Component SHALL provide default values for all configuration parameters</li>
 *   <li><strong>Requirement 6.4</strong>: WHEN a configuration parameter is not specified, THE Template_Component SHALL use the default value</li>
 *   <li><strong>Requirement 6.6</strong>: THE Configuration_Object SHALL support boolean flags for optional features (resizable, closeable, minimizable)</li>
 * </ul>
 * 
 * @see PanelConfig
 * @see Builder
 * @see SelectionMode
 */
public class ToolsPanelConfig extends PanelConfig {
    
    /**
     * Enum representing the tool selection mode for the tools panel.
     * <p>
     * This determines how tools can be selected within the panel.
     * </p>
     */
    public enum SelectionMode {
        /**
         * Only one tool can be selected at a time (radio-button behavior).
         * Selecting a new tool automatically deselects the previous one.
         */
        SINGLE,
        
        /**
         * Multiple tools can be selected simultaneously (checkbox behavior).
         * Each tool maintains its own selection state independently.
         */
        MULTIPLE,
        
        /**
         * No tools can be selected. Tools act as simple action buttons
         * without maintaining selection state.
         */
        NONE
    }
    
    // Configuration fields
    private final double toolSpacing;
    private final boolean showLabels;
    private final double iconSize;
    private final SelectionMode selectionMode;
    private final String initialSelection;
    
    /**
     * Private constructor - use Builder to create instances.
     * 
     * @param builder The builder instance containing configuration values
     */
    private ToolsPanelConfig(Builder builder) {
        super(builder.styleClasses);
        this.toolSpacing = builder.toolSpacing;
        this.showLabels = builder.showLabels;
        this.iconSize = builder.iconSize;
        this.selectionMode = builder.selectionMode;
        this.initialSelection = builder.initialSelection;
        
        // Validate configuration
        validate();
    }
    
    /**
     * Creates a new Builder instance for constructing ToolsPanelConfig objects.
     * 
     * @return A new Builder instance with default values
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Validates the configuration parameters for the tools panel.
     * <p>
     * This method checks that all configuration values are valid according to
     * the following rules:
     * </p>
     * <ul>
     *   <li>toolSpacing must be non-negative (>= 0)</li>
     *   <li>iconSize must be positive (> 0)</li>
     *   <li>selectionMode must not be null</li>
     * </ul>
     * 
     * @throws IllegalArgumentException if any configuration parameter is invalid
     */
    @Override
    protected void validate() {
        if (toolSpacing < 0) {
            throw new IllegalArgumentException(
                "[ToolsPanelConfig] Tool spacing must be non-negative (got: " + toolSpacing + ")"
            );
        }
        if (iconSize <= 0) {
            throw new IllegalArgumentException(
                "[ToolsPanelConfig] Icon size must be positive (got: " + iconSize + ")"
            );
        }
        if (selectionMode == null) {
            throw new IllegalArgumentException(
                "[ToolsPanelConfig] Selection mode cannot be null"
            );
        }
    }
    
    // Getters
    
    /**
     * Returns the spacing between tool items in pixels.
     * 
     * @return The tool spacing (default: 8)
     */
    public double getToolSpacing() {
        return toolSpacing;
    }
    
    /**
     * Returns whether tool labels should be displayed.
     * 
     * @return true if labels should be shown, false otherwise (default: true)
     */
    public boolean isShowLabels() {
        return showLabels;
    }
    
    /**
     * Returns the icon size in pixels.
     * 
     * @return The icon size (default: 24)
     */
    public double getIconSize() {
        return iconSize;
    }
    
    /**
     * Returns the selection mode for tools.
     * 
     * @return The selection mode (default: SelectionMode.SINGLE)
     */
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }
    
    /**
     * Returns the initial tool selection ID.
     * 
     * @return The tool ID to select initially, or null if no initial selection (default: null)
     */
    public String getInitialSelection() {
        return initialSelection;
    }
    
    /**
     * Builder class for constructing ToolsPanelConfig instances with a fluent API.
     * <p>
     * The builder provides default values for all properties and validates
     * the configuration when build() is called.
     * </p>
     */
    public static class Builder {
        // Default values
        private double toolSpacing = 8.0;
        private boolean showLabels = true;
        private double iconSize = 24.0;
        private SelectionMode selectionMode = SelectionMode.SINGLE;
        private String initialSelection = null;
        private List<String> styleClasses = new ArrayList<>();
        
        /**
         * Private constructor - use ToolsPanelConfig.builder() to create instances.
         */
        private Builder() {
        }
        
        /**
         * Sets the spacing between tool items.
         * 
         * @param toolSpacing The spacing in pixels (default: 8)
         * @return This builder instance for method chaining
         */
        public Builder toolSpacing(double toolSpacing) {
            this.toolSpacing = toolSpacing;
            return this;
        }
        
        /**
         * Sets whether tool labels should be displayed.
         * 
         * @param showLabels Whether to show labels (default: true)
         * @return This builder instance for method chaining
         */
        public Builder showLabels(boolean showLabels) {
            this.showLabels = showLabels;
            return this;
        }
        
        /**
         * Sets the icon size in pixels.
         * 
         * @param iconSize The icon size (default: 24)
         * @return This builder instance for method chaining
         */
        public Builder iconSize(double iconSize) {
            this.iconSize = iconSize;
            return this;
        }
        
        /**
         * Sets the tool selection mode.
         * 
         * @param selectionMode The selection mode (default: SelectionMode.SINGLE)
         * @return This builder instance for method chaining
         */
        public Builder selectionMode(SelectionMode selectionMode) {
            this.selectionMode = selectionMode;
            return this;
        }
        
        /**
         * Sets the initial tool selection.
         * 
         * @param initialSelection The tool ID to select initially (default: null)
         * @return This builder instance for method chaining
         */
        public Builder initialSelection(String initialSelection) {
            this.initialSelection = initialSelection;
            return this;
        }
        
        /**
         * Adds custom CSS style class names to the tools panel.
         * 
         * @param classes One or more CSS class names to add
         * @return This builder instance for method chaining
         */
        public Builder styleClasses(String... classes) {
            this.styleClasses.addAll(Arrays.asList(classes));
            return this;
        }
        
        /**
         * Builds and validates the ToolsPanelConfig instance.
         * 
         * @return A new ToolsPanelConfig instance with the configured values
         * @throws IllegalArgumentException if any configuration parameter is invalid
         */
        public ToolsPanelConfig build() {
            return new ToolsPanelConfig(this);
        }
    }
}
