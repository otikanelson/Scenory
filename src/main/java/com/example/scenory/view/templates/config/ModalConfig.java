package com.example.scenory.view.templates.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javafx.stage.Window;

/**
 * Configuration object for modal dialog customization using the builder pattern.
 * <p>
 * This class provides a fluent API for configuring modal dialogs with validation
 * and sensible default values.
 * </p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * ModalConfig config = ModalConfig.builder()
 *     .title("My Dialog")
 *     .dimensions(800, 600)
 *     .minDimensions(600, 400)
 *     .resizable(true)
 *     .closeable(true)
 *     .styleClasses("custom-modal", "dark-theme")
 *     .onClose(v -> System.out.println("Dialog closed"))
 *     .build();
 * }</pre>
 * 
 * <h3>Default Values:</h3>
 * <ul>
 *   <li>title: "Dialog"</li>
 *   <li>width: 600</li>
 *   <li>height: 400</li>
 *   <li>minWidth: null (no minimum constraint)</li>
 *   <li>minHeight: null (no minimum constraint)</li>
 *   <li>resizable: true</li>
 *   <li>closeable: true</li>
 *   <li>ownerWindow: null (no owner)</li>
 *   <li>styleClasses: empty list</li>
 *   <li>onClose: null (no callback)</li>
 * </ul>
 * 
 * @see Builder
 */
public class ModalConfig {
    
    // Configuration fields
    private final String title;
    private final double width;
    private final double height;
    private final Double minWidth;
    private final Double minHeight;
    private final boolean resizable;
    private final boolean closeable;
    private final Window ownerWindow;
    private final List<String> styleClasses;
    private final Consumer<Void> onClose;
    
    /**
     * Private constructor - use Builder to create instances.
     * 
     * @param builder The builder instance containing configuration values
     */
    private ModalConfig(Builder builder) {
        this.title = builder.title;
        this.width = builder.width;
        this.height = builder.height;
        this.minWidth = builder.minWidth;
        this.minHeight = builder.minHeight;
        this.resizable = builder.resizable;
        this.closeable = builder.closeable;
        this.ownerWindow = builder.ownerWindow;
        this.styleClasses = new ArrayList<>(builder.styleClasses);
        this.onClose = builder.onClose;
        
        // Validate configuration
        validate();
    }
    
    /**
     * Creates a new Builder instance for constructing ModalConfig objects.
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
    private void validate() {
        if (width <= 0) {
            throw new IllegalArgumentException(
                "[ModalConfig] Width must be positive (got: " + width + ")"
            );
        }
        if (height <= 0) {
            throw new IllegalArgumentException(
                "[ModalConfig] Height must be positive (got: " + height + ")"
            );
        }
        if (minWidth != null && minWidth <= 0) {
            throw new IllegalArgumentException(
                "[ModalConfig] Min width must be positive (got: " + minWidth + ")"
            );
        }
        if (minHeight != null && minHeight <= 0) {
            throw new IllegalArgumentException(
                "[ModalConfig] Min height must be positive (got: " + minHeight + ")"
            );
        }
        if (minWidth != null && minWidth > width) {
            throw new IllegalArgumentException(
                "[ModalConfig] Min width cannot exceed width (minWidth: " + minWidth + ", width: " + width + ")"
            );
        }
        if (minHeight != null && minHeight > height) {
            throw new IllegalArgumentException(
                "[ModalConfig] Min height cannot exceed height (minHeight: " + minHeight + ", height: " + height + ")"
            );
        }
    }
    
    // Getters with defensive copies where applicable
    
    /**
     * @return The dialog title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * @return The dialog width in pixels
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * @return The dialog height in pixels
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * @return The minimum width constraint, or null if no constraint
     */
    public Double getMinWidth() {
        return minWidth;
    }
    
    /**
     * @return The minimum height constraint, or null if no constraint
     */
    public Double getMinHeight() {
        return minHeight;
    }
    
    /**
     * @return Whether the dialog is resizable
     */
    public boolean isResizable() {
        return resizable;
    }
    
    /**
     * @return Whether the dialog is closeable
     */
    public boolean isCloseable() {
        return closeable;
    }
    
    /**
     * @return The owner window, or null if no owner
     */
    public Window getOwnerWindow() {
        return ownerWindow;
    }
    
    /**
     * Returns a defensive copy of the style classes list.
     * 
     * @return A new list containing all style class names
     */
    public List<String> getStyleClasses() {
        return new ArrayList<>(styleClasses);
    }
    
    /**
     * @return The close callback, or null if no callback
     */
    public Consumer<Void> getOnClose() {
        return onClose;
    }
    
    /**
     * Builder class for constructing ModalConfig instances with a fluent API.
     * <p>
     * The builder provides default values for all properties and validates
     * the configuration when build() is called.
     * </p>
     */
    public static class Builder {
        // Default values
        private String title = "Dialog";
        private double width = 600;
        private double height = 400;
        private Double minWidth = null;
        private Double minHeight = null;
        private boolean resizable = true;
        private boolean closeable = true;
        private Window ownerWindow = null;
        private List<String> styleClasses = new ArrayList<>();
        private Consumer<Void> onClose = null;
        
        /**
         * Private constructor - use ModalConfig.builder() to create instances.
         */
        private Builder() {
        }
        
        /**
         * Sets the dialog title.
         * 
         * @param title The dialog title (default: "Dialog")
         * @return This builder instance for method chaining
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        /**
         * Sets the dialog dimensions.
         * 
         * @param width The dialog width in pixels (default: 600)
         * @param height The dialog height in pixels (default: 400)
         * @return This builder instance for method chaining
         */
        public Builder dimensions(double width, double height) {
            this.width = width;
            this.height = height;
            return this;
        }
        
        /**
         * Sets the minimum dialog dimensions.
         * 
         * @param minWidth The minimum width in pixels
         * @param minHeight The minimum height in pixels
         * @return This builder instance for method chaining
         */
        public Builder minDimensions(double minWidth, double minHeight) {
            this.minWidth = minWidth;
            this.minHeight = minHeight;
            return this;
        }
        
        /**
         * Sets whether the dialog is resizable.
         * 
         * @param resizable Whether the dialog can be resized (default: true)
         * @return This builder instance for method chaining
         */
        public Builder resizable(boolean resizable) {
            this.resizable = resizable;
            return this;
        }
        
        /**
         * Sets whether the dialog is closeable.
         * 
         * @param closeable Whether the dialog can be closed (default: true)
         * @return This builder instance for method chaining
         */
        public Builder closeable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }
        
        /**
         * Sets the owner window for the dialog.
         * 
         * @param owner The owner window for modal behavior (default: null)
         * @return This builder instance for method chaining
         */
        public Builder owner(Window owner) {
            this.ownerWindow = owner;
            return this;
        }
        
        /**
         * Adds custom CSS style class names to the dialog.
         * 
         * @param classes One or more CSS class names to add
         * @return This builder instance for method chaining
         */
        public Builder styleClasses(String... classes) {
            this.styleClasses.addAll(Arrays.asList(classes));
            return this;
        }
        
        /**
         * Sets the callback to invoke when the dialog is closed.
         * 
         * @param callback The callback consumer (default: null)
         * @return This builder instance for method chaining
         */
        public Builder onClose(Consumer<Void> callback) {
            this.onClose = callback;
            return this;
        }
        
        /**
         * Builds and validates the ModalConfig instance.
         * 
         * @return A new ModalConfig instance with the configured values
         * @throws IllegalArgumentException if any configuration parameter is invalid
         */
        public ModalConfig build() {
            return new ModalConfig(this);
        }
    }
}
