package com.example.scenory.view.templates.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration object for status panel customization using the builder pattern.
 * <p>
 * This class provides a fluent API for configuring status panels that display
 * zoom level, FPS, and frame information with validation and sensible default values.
 * </p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * StatusPanelConfig config = StatusPanelConfig.builder()
 *     .initialZoom(150.0)
 *     .initialFPS(30)
 *     .initialFrameInfo(5, 100)
 *     .showZoom(true)
 *     .showFPS(true)
 *     .showFrameInfo(true)
 *     .styleClasses("custom-status-panel")
 *     .build();
 * }</pre>
 * 
 * <h3>Default Values:</h3>
 * <ul>
 *   <li>initialZoom: 100.0 (100%)</li>
 *   <li>initialFPS: 24</li>
 *   <li>initialCurrentFrame: 1</li>
 *   <li>initialTotalFrames: 1</li>
 *   <li>showZoom: true</li>
 *   <li>showFPS: true</li>
 *   <li>showFrameInfo: true</li>
 *   <li>styleClasses: empty list</li>
 * </ul>
 * 
 * @see Builder
 * @see PanelConfig
 */
public class StatusPanelConfig extends PanelConfig {
    
    // Configuration fields
    private final double initialZoom;
    private final int initialFPS;
    private final int initialCurrentFrame;
    private final int initialTotalFrames;
    private final boolean showZoom;
    private final boolean showFPS;
    private final boolean showFrameInfo;
    
    /**
     * Private constructor - use Builder to create instances.
     * 
     * @param builder The builder instance containing configuration values
     */
    private StatusPanelConfig(Builder builder) {
        super(builder.styleClasses);
        this.initialZoom = builder.initialZoom;
        this.initialFPS = builder.initialFPS;
        this.initialCurrentFrame = builder.initialCurrentFrame;
        this.initialTotalFrames = builder.initialTotalFrames;
        this.showZoom = builder.showZoom;
        this.showFPS = builder.showFPS;
        this.showFrameInfo = builder.showFrameInfo;
        
        // Validate configuration
        validate();
    }
    
    /**
     * Creates a new Builder instance for constructing StatusPanelConfig objects.
     * 
     * @return A new Builder instance with default values
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Validates the configuration parameters.
     * 
     * @throws IllegalArgumentException if any configuration parameter is invalid
     */
    @Override
    protected void validate() {
        if (initialZoom <= 0) {
            throw new IllegalArgumentException(
                "[StatusPanelConfig] Initial zoom must be positive (got: " + initialZoom + ")"
            );
        }
        if (initialFPS <= 0) {
            throw new IllegalArgumentException(
                "[StatusPanelConfig] Initial FPS must be positive (got: " + initialFPS + ")"
            );
        }
        if (initialCurrentFrame <= 0) {
            throw new IllegalArgumentException(
                "[StatusPanelConfig] Initial current frame must be positive (got: " + initialCurrentFrame + ")"
            );
        }
        if (initialTotalFrames <= 0) {
            throw new IllegalArgumentException(
                "[StatusPanelConfig] Initial total frames must be positive (got: " + initialTotalFrames + ")"
            );
        }
        if (initialCurrentFrame > initialTotalFrames) {
            throw new IllegalArgumentException(
                "[StatusPanelConfig] Initial current frame cannot exceed total frames " +
                "(currentFrame: " + initialCurrentFrame + ", totalFrames: " + initialTotalFrames + ")"
            );
        }
    }
    
    // Getters
    
    /**
     * @return The initial zoom level as a percentage (e.g., 100.0 = 100%)
     */
    public double getInitialZoom() {
        return initialZoom;
    }
    
    /**
     * @return The initial FPS (frames per second) value
     */
    public int getInitialFPS() {
        return initialFPS;
    }
    
    /**
     * @return The initial current frame number
     */
    public int getInitialCurrentFrame() {
        return initialCurrentFrame;
    }
    
    /**
     * @return The initial total number of frames
     */
    public int getInitialTotalFrames() {
        return initialTotalFrames;
    }
    
    /**
     * @return Whether the zoom level should be displayed
     */
    public boolean isShowZoom() {
        return showZoom;
    }
    
    /**
     * @return Whether the FPS should be displayed
     */
    public boolean isShowFPS() {
        return showFPS;
    }
    
    /**
     * @return Whether the frame information should be displayed
     */
    public boolean isShowFrameInfo() {
        return showFrameInfo;
    }
    
    /**
     * Builder class for constructing StatusPanelConfig instances with a fluent API.
     * <p>
     * The builder provides default values for all properties and validates
     * the configuration when build() is called.
     * </p>
     */
    public static class Builder {
        // Default values
        private double initialZoom = 100.0;
        private int initialFPS = 24;
        private int initialCurrentFrame = 1;
        private int initialTotalFrames = 1;
        private boolean showZoom = true;
        private boolean showFPS = true;
        private boolean showFrameInfo = true;
        private List<String> styleClasses = new ArrayList<>();
        
        /**
         * Private constructor - use StatusPanelConfig.builder() to create instances.
         */
        private Builder() {
        }
        
        /**
         * Sets the initial zoom level.
         * 
         * @param zoom The initial zoom level as a percentage (e.g., 100.0 = 100%, default: 100.0)
         * @return This builder instance for method chaining
         */
        public Builder initialZoom(double zoom) {
            this.initialZoom = zoom;
            return this;
        }
        
        /**
         * Sets the initial FPS value.
         * 
         * @param fps The initial frames per second value (default: 24)
         * @return This builder instance for method chaining
         */
        public Builder initialFPS(int fps) {
            this.initialFPS = fps;
            return this;
        }
        
        /**
         * Sets the initial frame information.
         * 
         * @param currentFrame The initial current frame number (default: 1)
         * @param totalFrames The initial total number of frames (default: 1)
         * @return This builder instance for method chaining
         */
        public Builder initialFrameInfo(int currentFrame, int totalFrames) {
            this.initialCurrentFrame = currentFrame;
            this.initialTotalFrames = totalFrames;
            return this;
        }
        
        /**
         * Sets the initial current frame.
         * 
         * @param currentFrame The initial current frame number (default: 1)
         * @return This builder instance for method chaining
         */
        public Builder initialCurrentFrame(int currentFrame) {
            this.initialCurrentFrame = currentFrame;
            return this;
        }
        
        /**
         * Sets the initial total frames.
         * 
         * @param totalFrames The initial total number of frames (default: 1)
         * @return This builder instance for method chaining
         */
        public Builder initialTotalFrames(int totalFrames) {
            this.initialTotalFrames = totalFrames;
            return this;
        }
        
        /**
         * Sets whether to display the zoom level.
         * 
         * @param show Whether to show the zoom level (default: true)
         * @return This builder instance for method chaining
         */
        public Builder showZoom(boolean show) {
            this.showZoom = show;
            return this;
        }
        
        /**
         * Sets whether to display the FPS.
         * 
         * @param show Whether to show the FPS (default: true)
         * @return This builder instance for method chaining
         */
        public Builder showFPS(boolean show) {
            this.showFPS = show;
            return this;
        }
        
        /**
         * Sets whether to display the frame information.
         * 
         * @param show Whether to show the frame information (default: true)
         * @return This builder instance for method chaining
         */
        public Builder showFrameInfo(boolean show) {
            this.showFrameInfo = show;
            return this;
        }
        
        /**
         * Adds custom CSS style class names to the status panel.
         * 
         * @param classes One or more CSS class names to add
         * @return This builder instance for method chaining
         */
        public Builder styleClasses(String... classes) {
            this.styleClasses.addAll(Arrays.asList(classes));
            return this;
        }
        
        /**
         * Builds and validates the StatusPanelConfig instance.
         * 
         * @return A new StatusPanelConfig instance with the configured values
         * @throws IllegalArgumentException if any configuration parameter is invalid
         */
        public StatusPanelConfig build() {
            return new StatusPanelConfig(this);
        }
    }
}
