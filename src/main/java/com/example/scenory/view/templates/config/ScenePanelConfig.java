package com.example.scenory.view.templates.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Configuration object for scene panel customization using the builder pattern.
 * <p>
 * This class provides a fluent API for configuring scene panels that display
 * frame thumbnails and layer information. It extends {@link PanelConfig} to
 * inherit common panel configuration properties.
 * </p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * ScenePanelConfig config = ScenePanelConfig.builder()
 *     .thumbnailDimensions(160, 120)
 *     .showFrameLabels(true)
 *     .enableLayerMode(false)
 *     .onFrameSelect(frameId -> System.out.println("Selected: " + frameId))
 *     .onFrameDoubleClick(frameId -> System.out.println("Double-clicked: " + frameId))
 *     .styleClasses("custom-scene-panel", "dark-theme")
 *     .build();
 * }</pre>
 * 
 * <h3>Default Values:</h3>
 * <ul>
 *   <li>thumbnailWidth: 120 pixels</li>
 *   <li>thumbnailHeight: 90 pixels</li>
 *   <li>showFrameLabels: true</li>
 *   <li>enableLayerMode: false</li>
 *   <li>onFrameSelect: null (no callback)</li>
 *   <li>onFrameDoubleClick: null (no callback)</li>
 *   <li>styleClasses: empty list</li>
 * </ul>
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
 * </ul>
 * 
 * @see PanelConfig
 * @see Builder
 */
public class ScenePanelConfig extends PanelConfig {
    
    // Configuration fields
    private final double thumbnailWidth;
    private final double thumbnailHeight;
    private final boolean showFrameLabels;
    private final boolean enableLayerMode;
    private final Consumer<String> onFrameSelect;
    private final Consumer<String> onFrameDoubleClick;
    
    /**
     * Private constructor - use Builder to create instances.
     * 
     * @param builder The builder instance containing configuration values
     */
    private ScenePanelConfig(Builder builder) {
        super(builder.styleClasses);
        this.thumbnailWidth = builder.thumbnailWidth;
        this.thumbnailHeight = builder.thumbnailHeight;
        this.showFrameLabels = builder.showFrameLabels;
        this.enableLayerMode = builder.enableLayerMode;
        this.onFrameSelect = builder.onFrameSelect;
        this.onFrameDoubleClick = builder.onFrameDoubleClick;
        
        // Validate configuration - inline validation to avoid overridable method call in constructor
        if (this.thumbnailWidth <= 0) {
            throw new IllegalArgumentException(
                "[ScenePanelConfig] Thumbnail width must be positive (got: " + this.thumbnailWidth + ")"
            );
        }
        if (this.thumbnailHeight <= 0) {
            throw new IllegalArgumentException(
                "[ScenePanelConfig] Thumbnail height must be positive (got: " + this.thumbnailHeight + ")"
            );
        }
    }
    
    /**
     * Creates a new Builder instance for constructing ScenePanelConfig objects.
     * 
     * @return A new Builder instance with default values
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Validates the configuration parameters for the scene panel.
     * <p>
     * This method checks that all configuration values are valid according to
     * the following rules:
     * </p>
     * <ul>
     *   <li>thumbnailWidth must be positive (> 0)</li>
     *   <li>thumbnailHeight must be positive (> 0)</li>
     * </ul>
     * 
     * @throws IllegalArgumentException if any configuration parameter is invalid
     */
    @Override
    protected void validate() {
        if (thumbnailWidth <= 0) {
            throw new IllegalArgumentException(
                "[ScenePanelConfig] Thumbnail width must be positive (got: " + thumbnailWidth + ")"
            );
        }
        if (thumbnailHeight <= 0) {
            throw new IllegalArgumentException(
                "[ScenePanelConfig] Thumbnail height must be positive (got: " + thumbnailHeight + ")"
            );
        }
    }
    
    // Getters
    
    /**
     * Returns the thumbnail width in pixels.
     * 
     * @return The thumbnail width (default: 120)
     */
    public double getThumbnailWidth() {
        return thumbnailWidth;
    }
    
    /**
     * Returns the thumbnail height in pixels.
     * 
     * @return The thumbnail height (default: 90)
     */
    public double getThumbnailHeight() {
        return thumbnailHeight;
    }
    
    /**
     * Returns whether frame labels should be displayed.
     * 
     * @return true if labels should be shown, false otherwise (default: true)
     */
    public boolean isShowFrameLabels() {
        return showFrameLabels;
    }
    
    /**
     * Returns whether layer view mode is enabled.
     * 
     * @return true if layer mode is enabled, false otherwise (default: false)
     */
    public boolean isEnableLayerMode() {
        return enableLayerMode;
    }
    
    /**
     * Returns the frame selection callback.
     * 
     * @return The callback to invoke when a frame is selected, or null if no callback (default: null)
     */
    public Consumer<String> getOnFrameSelect() {
        return onFrameSelect;
    }
    
    /**
     * Returns the frame double-click callback.
     * 
     * @return The callback to invoke when a frame is double-clicked, or null if no callback (default: null)
     */
    public Consumer<String> getOnFrameDoubleClick() {
        return onFrameDoubleClick;
    }
    
    /**
     * Builder class for constructing ScenePanelConfig instances with a fluent API.
     * <p>
     * The builder provides default values for all properties and validates
     * the configuration when build() is called.
     * </p>
     */
    public static class Builder {
        // Default values
        private double thumbnailWidth = 120.0;
        private double thumbnailHeight = 90.0;
        private boolean showFrameLabels = true;
        private boolean enableLayerMode = false;
        private Consumer<String> onFrameSelect = null;
        private Consumer<String> onFrameDoubleClick = null;
        private List<String> styleClasses = new ArrayList<>();
        
        /**
         * Private constructor - use ScenePanelConfig.builder() to create instances.
         */
        private Builder() {
        }
        
        /**
         * Sets the thumbnail width in pixels.
         * 
         * @param thumbnailWidth The thumbnail width (default: 120)
         * @return This builder instance for method chaining
         */
        public Builder thumbnailWidth(double thumbnailWidth) {
            this.thumbnailWidth = thumbnailWidth;
            return this;
        }
        
        /**
         * Sets the thumbnail height in pixels.
         * 
         * @param thumbnailHeight The thumbnail height (default: 90)
         * @return This builder instance for method chaining
         */
        public Builder thumbnailHeight(double thumbnailHeight) {
            this.thumbnailHeight = thumbnailHeight;
            return this;
        }
        
        /**
         * Sets both thumbnail width and height in pixels.
         * 
         * @param width The thumbnail width (default: 120)
         * @param height The thumbnail height (default: 90)
         * @return This builder instance for method chaining
         */
        public Builder thumbnailDimensions(double width, double height) {
            this.thumbnailWidth = width;
            this.thumbnailHeight = height;
            return this;
        }
        
        /**
         * Sets whether frame labels should be displayed.
         * 
         * @param showFrameLabels Whether to show labels (default: true)
         * @return This builder instance for method chaining
         */
        public Builder showFrameLabels(boolean showFrameLabels) {
            this.showFrameLabels = showFrameLabels;
            return this;
        }
        
        /**
         * Sets whether layer view mode is enabled.
         * 
         * @param enableLayerMode Whether to enable layer mode (default: false)
         * @return This builder instance for method chaining
         */
        public Builder enableLayerMode(boolean enableLayerMode) {
            this.enableLayerMode = enableLayerMode;
            return this;
        }
        
        /**
         * Sets the callback to invoke when a frame is selected.
         * 
         * @param onFrameSelect The callback consumer accepting frame ID (default: null)
         * @return This builder instance for method chaining
         */
        public Builder onFrameSelect(Consumer<String> onFrameSelect) {
            this.onFrameSelect = onFrameSelect;
            return this;
        }
        
        /**
         * Sets the callback to invoke when a frame is double-clicked.
         * 
         * @param onFrameDoubleClick The callback consumer accepting frame ID (default: null)
         * @return This builder instance for method chaining
         */
        public Builder onFrameDoubleClick(Consumer<String> onFrameDoubleClick) {
            this.onFrameDoubleClick = onFrameDoubleClick;
            return this;
        }
        
        /**
         * Adds custom CSS style class names to the scene panel.
         * 
         * @param classes One or more CSS class names to add
         * @return This builder instance for method chaining
         */
        public Builder styleClasses(String... classes) {
            this.styleClasses.addAll(Arrays.asList(classes));
            return this;
        }
        
        /**
         * Builds and validates the ScenePanelConfig instance.
         * 
         * @return A new ScenePanelConfig instance with the configured values
         * @throws IllegalArgumentException if any configuration parameter is invalid
         */
        public ScenePanelConfig build() {
            return new ScenePanelConfig(this);
        }
    }
}
