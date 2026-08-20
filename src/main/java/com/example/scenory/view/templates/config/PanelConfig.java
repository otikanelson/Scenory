package com.example.scenory.view.templates.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base configuration class for panel template customization.
 * <p>
 * This class provides common configuration properties and validation logic
 * for all panel templates. Subclasses should extend this to add panel-specific
 * configuration options.
 * </p>
 * 
 * <h3>Common Properties:</h3>
 * <ul>
 *   <li>styleClasses: Custom CSS class names for styling customization</li>
 * </ul>
 * 
 * <h3>Subclass Responsibilities:</h3>
 * <ul>
 *   <li>Implement the abstract {@link #validate()} method to validate panel-specific configuration</li>
 *   <li>Call the superclass constructor with style classes</li>
 *   <li>Provide builder pattern for configuration construction</li>
 * </ul>
 * 
 * @see ToolsPanelConfig
 * @see ScenePanelConfig
 * @see TimelinePanelConfig
 * @see StatusPanelConfig
 */
public abstract class PanelConfig {
    
    /**
     * List of custom CSS class names to apply to the panel.
     * This list is defensively copied in the constructor and getter
     * to prevent external modification.
     */
    protected final List<String> styleClasses;
    
    /**
     * Protected constructor for subclass use.
     * Creates a defensive copy of the provided style classes list.
     * 
     * @param styleClasses List of CSS class names to apply to the panel.
     *                     If null, an empty list is used.
     */
    protected PanelConfig(List<String> styleClasses) {
        // Defensive copy: create a new list to prevent external modification
        this.styleClasses = styleClasses != null 
            ? new ArrayList<>(styleClasses) 
            : new ArrayList<>();
    }
    
    /**
     * Returns a defensive copy of the style classes list.
     * <p>
     * This prevents external code from modifying the internal list,
     * maintaining immutability of the configuration object.
     * </p>
     * 
     * @return A new list containing all style class names
     */
    public List<String> getStyleClasses() {
        // Defensive copy: return a new list to prevent external modification
        return new ArrayList<>(styleClasses);
    }
    
    /**
     * Validates the configuration parameters for this panel.
     * <p>
     * Subclasses must implement this method to validate their specific
     * configuration parameters. This method is typically called during
     * construction to ensure the configuration is valid before use.
     * </p>
     * 
     * <h3>Implementation Guidelines:</h3>
     * <ul>
     *   <li>Throw {@link IllegalArgumentException} for invalid parameters</li>
     *   <li>Include descriptive error messages with parameter names and values</li>
     *   <li>Use the format: "[ClassName] parameter description (got: value)"</li>
     *   <li>Validate all constraints (positive values, min/max ranges, etc.)</li>
     * </ul>
     * 
     * @throws IllegalArgumentException if any configuration parameter is invalid
     */
    protected abstract void validate();
}
