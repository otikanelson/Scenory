package com.example.scenory.view.templates.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Configuration object for timeline panel customization using the builder pattern.
 * <p>
 * This class provides a fluent API for configuring timeline panels with validation
 * and sensible default values. Timeline panels display horizontal frame markers with
 * playback controls and frame navigation.
 * </p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * TimelinePanelConfig config = TimelinePanelConfig.builder()
 *     .frameMarkerDimensions(70, 50)
 *     .showPlaybackButton(true)
 *     .showAddFrameButton(true)
 *     .onFrameSelect(frameNum -> System.out.println("Selected frame: " + frameNum))
 *     .onPlaybackToggle(playing -> System.out.println("Playing: " + playing))
 *     .styleClasses("custom-timeline")
 *     .build();
 * }</pre>
 * 
 * <h3>Default Values:</h3>
 * <ul>
 *   <li>frameMarkerWidth: 60</li>
 *   <li>frameMarkerHeight: 40</li>
 *   <li>showPlaybackButton: true</li>
 *   <li>showAddFrameButton: true</li>
 *   <li>onFrameSelect: null (no callback)</li>
 *   <li>onPlaybackToggle: null (no callback)</li>
 *   <li>styleClasses: empty list</li>
 * </ul>
 * 
 * <h3>Requirements Mapping:</h3>
 * <ul>
 *   <li>Requirement 4.2: Frame marker display with dimensions</li>
 *   <li>Requirement 4.3: Playback button display and toggle</li>
 *   <li>Requirement 4.4: Frame selection callback</li>
 *   <li>Requirement 4.7: Add frame button callback</li>
 *   <li>Requirement 6.1: Configuration object support</li>
 *   <li>Requirement 6.2: Default value provision</li>
 *   <li>Requirement 6.3: Custom CSS class support</li>
 *   <li>Requirement 6.4: Configuration validation</li>
 * </ul>
 * 
 * @see PanelConfig
 * @see Builder
 */
public class TimelinePanelConfig extends PanelConfig {
    
    // Configuration fields
    private final double frameMarkerWidth;
    private final double frameMarkerHeight;
    private final boolean showPlaybackButton;
    private final boolean showAddFrameButton;
    private final Consumer<Integer> onFrameSelect;
    private final Consumer<Boolean> onPlaybackToggle;
    
    /**
     * Private constructor - use Builder to create instances.
     * 
     * @param builder The builder instance containing configuration values
     */
    private TimelinePanelConfig(Builder builder) {
        super(builder.styleClasses);
        this.frameMarkerWidth = builder.frameMarkerWidth;
        this.frameMarkerHeight = builder.frameMarkerHeight;
        this.showPlaybackButton = builder.showPlaybackButton;
        this.showAddFrameButton = builder.showAddFrameButton;
        this.onFrameSelect = builder.onFrameSelect;
        this.onPlaybackToggle = builder.onPlaybackToggle;
        
        // Validate configuration
        validate();
    }
    
    /**
     * Creates a new Builder instance for constructing TimelinePanelConfig objects.
     * 
     * @return A new Builder instance with default values
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Validates the configuration parameters.
     * <p>
     * This method ensures that:
     * <ul>
     *   <li>Frame marker width is positive</li>
     *   <li>Frame marker height is positive</li>
     * </ul>
     * </p>
     * 
     * @throws IllegalArgumentException if any configuration parameter is invalid
     */
    @Override
    protected void validate() {
        if (frameMarkerWidth <= 0) {
            throw new IllegalArgumentException(
                "[TimelinePanelConfig] Frame marker width must be positive (got: " + frameMarkerWidth + ")"
            );
        }
        if (frameMarkerHeight <= 0) {
            throw new IllegalArgumentException(
                "[TimelinePanelConfig] Frame marker height must be positive (got: " + frameMarkerHeight + ")"
            );
        }
    }
    
    // Getters
    
    /**
     * @return The width of frame markers in pixels
     */
    public double getFrameMarkerWidth() {
        return frameMarkerWidth;
    }
    
    /**
     * @return The height of frame markers in pixels
     */
    public double getFrameMarkerHeight() {
        return frameMarkerHeight;
    }
    
    /**
     * @return Whether the playback button should be displayed
     */
    public boolean isShowPlaybackButton() {
        return showPlaybackButton;
    }
    
    /**
     * @return Whether the add frame button should be displayed
     */
    public boolean isShowAddFrameButton() {
        return showAddFrameButton;
    }
    
    /**
     * @return The frame selection callback, or null if no callback
     */
    public Consumer<Integer> getOnFrameSelect() {
        return onFrameSelect;
    }
    
    /**
     * @return The playback toggle callback, or null if no callback
     */
    public Consumer<Boolean> getOnPlaybackToggle() {
        return onPlaybackToggle;
    }
    
    /**
     * Builder class for constructing TimelinePanelConfig instances with a fluent API.
     * <p>
     * The builder provides default values for all properties and validates
     * the configuration when build() is called.
     * </p>
     */
    public static class Builder {
        // Default values
        private double frameMarkerWidth = 60;
        private double frameMarkerHeight = 40;
        private boolean showPlaybackButton = true;
        private boolean showAddFrameButton = true;
        private Consumer<Integer> onFrameSelect = null;
        private Consumer<Boolean> onPlaybackToggle = null;
        private List<String> styleClasses = new ArrayList<>();
        
        /**
         * Private constructor - use TimelinePanelConfig.builder() to create instances.
         */
        private Builder() {
        }
        
        /**
         * Sets the frame marker dimensions.
         * 
         * @param width The frame marker width in pixels (default: 60)
         * @param height The frame marker height in pixels (default: 40)
         * @return This builder instance for method chaining
         */
        public Builder frameMarkerDimensions(double width, double height) {
            this.frameMarkerWidth = width;
            this.frameMarkerHeight = height;
            return this;
        }
        
        /**
         * Sets the frame marker width.
         * 
         * @param width The frame marker width in pixels (default: 60)
         * @return This builder instance for method chaining
         */
        public Builder frameMarkerWidth(double width) {
            this.frameMarkerWidth = width;
            return this;
        }
        
        /**
         * Sets the frame marker height.
         * 
         * @param height The frame marker height in pixels (default: 40)
         * @return This builder instance for method chaining
         */
        public Builder frameMarkerHeight(double height) {
            this.frameMarkerHeight = height;
            return this;
        }
        
        /**
         * Sets whether the playback button should be displayed.
         * 
         * @param show Whether to show the playback button (default: true)
         * @return This builder instance for method chaining
         */
        public Builder showPlaybackButton(boolean show) {
            this.showPlaybackButton = show;
            return this;
        }
        
        /**
         * Sets whether the add frame button should be displayed.
         * 
         * @param show Whether to show the add frame button (default: true)
         * @return This builder instance for method chaining
         */
        public Builder showAddFrameButton(boolean show) {
            this.showAddFrameButton = show;
            return this;
        }
        
        /**
         * Sets the callback to invoke when a frame is selected.
         * <p>
         * The callback receives the selected frame number as an Integer parameter.
         * </p>
         * 
         * @param callback The frame selection callback (default: null)
         * @return This builder instance for method chaining
         */
        public Builder onFrameSelect(Consumer<Integer> callback) {
            this.onFrameSelect = callback;
            return this;
        }
        
        /**
         * Sets the callback to invoke when playback state is toggled.
         * <p>
         * The callback receives a Boolean parameter indicating whether playback
         * is now playing (true) or paused (false).
         * </p>
         * 
         * @param callback The playback toggle callback (default: null)
         * @return This builder instance for method chaining
         */
        public Builder onPlaybackToggle(Consumer<Boolean> callback) {
            this.onPlaybackToggle = callback;
            return this;
        }
        
        /**
         * Adds custom CSS style class names to the timeline panel.
         * 
         * @param classes One or more CSS class names to add
         * @return This builder instance for method chaining
         */
        public Builder styleClasses(String... classes) {
            this.styleClasses.addAll(Arrays.asList(classes));
            return this;
        }
        
        /**
         * Builds and validates the TimelinePanelConfig instance.
         * 
         * @return A new TimelinePanelConfig instance with the configured values
         * @throws IllegalArgumentException if any configuration parameter is invalid
         */
        public TimelinePanelConfig build() {
            return new TimelinePanelConfig(this);
        }
    }
}
