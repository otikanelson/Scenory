package com.example.scenory.view.templates.panels;

import com.example.scenory.view.templates.ToolItem;
import com.example.scenory.view.templates.config.ToolsPanelConfig;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Template for vertical tool panels with selection state management.
 * <p>
 * ToolsPanelTemplate provides a standardized container for tool buttons with
 * consistent layout, styling, and selection behavior. It extends {@link VBox}
 * to provide a vertical layout and manages tool items with configurable spacing,
 * icons, labels, and selection callbacks.
 * </p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Vertical layout with configurable spacing between tools</li>
 *   <li>Single-selection, multi-selection, or no-selection modes</li>
 *   <li>Visual highlighting for selected tools</li>
 *   <li>Support for tool groups with separators</li>
 *   <li>Automatic CSS stylesheet loading</li>
 *   <li>Custom CSS class support</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Create configuration
 * ToolsPanelConfig config = ToolsPanelConfig.builder()
 *     .toolSpacing(10)
 *     .showLabels(true)
 *     .iconSize(24)
 *     .selectionMode(SelectionMode.SINGLE)
 *     .styleClasses("custom-tools-panel")
 *     .build();
 * 
 * // Create tools panel
 * ToolsPanelTemplate toolsPanel = new ToolsPanelTemplate(config);
 * 
 * // Add tools
 * ImageView pencilIcon = new ImageView("pencil.png");
 * toolsPanel.addTool("pencil", pencilIcon, "Pencil", id -> System.out.println("Pencil selected"));
 * 
 * // Select a tool
 * toolsPanel.selectTool("pencil");
 * }</pre>
 * 
 * <h3>Requirements Validated:</h3>
 * <ul>
 *   <li><strong>Requirement 2.1</strong>: THE Tools_Panel Template SHALL provide a vertical layout container for tool buttons</li>
 *   <li><strong>Requirement 2.7</strong>: THE Tools_Panel Template SHALL apply consistent spacing between tool items</li>
 *   <li><strong>Requirement 2.9</strong>: THE Tools_Panel Template SHALL load styling from the CSS_Stylesheet</li>
 *   <li><strong>Requirement 6.2</strong>: WHEN a Configuration_Object is provided, THE Template_Component SHALL apply the specified title, dimensions, and styling options</li>
 *   <li><strong>Requirement 6.5</strong>: THE Configuration_Object SHALL support specifying custom CSS class names for styling customization</li>
 *   <li><strong>Requirement 7.2</strong>: THE Template_Component SHALL support the existing CSS_Stylesheet loading mechanism</li>
 *   <li><strong>Requirement 7.3</strong>: THE Template_Component SHALL use Consumer_Callback interfaces compatible with existing callback patterns</li>
 * </ul>
 * 
 * @see ToolsPanelConfig
 * @see ToolItem
 * @see com.example.scenory.view.templates.config.ToolsPanelConfig.SelectionMode
 */
public class ToolsPanelTemplate extends VBox {
    
    /**
     * The configuration object containing customization parameters.
     */
    private final ToolsPanelConfig config;
    
    /**
     * Map storing tool items by their unique identifiers.
     * <p>
     * This map enables quick lookup of tool items for selection management
     * and state updates. The key is the tool ID, and the value is the ToolItem.
     * </p>
     */
    private final Map<String, ToolItem> toolItems;
    
    /**
     * Map storing button nodes by their tool identifiers.
     * <p>
     * This map enables quick lookup of button nodes for visual state updates
     * such as adding/removing the selected CSS class. The key is the tool ID,
     * and the value is the Button node.
     * </p>
     */
    private final Map<String, Button> toolButtons;
    
    /**
     * The ID of the currently selected tool, or null if no tool is selected.
     * <p>
     * In SINGLE selection mode, this tracks which tool is currently selected.
     * When a new tool is selected, this field is updated and the previous
     * selection is cleared.
     * </p>
     */
    private String selectedToolId;
    
    /**
     * Creates a new ToolsPanelTemplate with the specified configuration.
     * <p>
     * This constructor initializes the panel by:
     * </p>
     * <ol>
     *   <li>Validating the configuration object (non-null check)</li>
     *   <li>Initializing internal data structures (toolItems map)</li>
     *   <li>Applying tool spacing from the configuration</li>
     *   <li>Loading and applying the CSS stylesheet</li>
     *   <li>Applying custom CSS classes from the configuration</li>
     * </ol>
     * 
     * <h3>Configuration Application:</h3>
     * <ul>
     *   <li><strong>Tool Spacing</strong>: Sets the vertical spacing between tool items</li>
     *   <li><strong>CSS Stylesheet</strong>: Loads the application's standard stylesheet</li>
     *   <li><strong>Custom Classes</strong>: Applies additional CSS classes for customization</li>
     * </ul>
     * 
     * <h3>Error Handling:</h3>
     * <ul>
     *   <li>Throws NullPointerException if config is null</li>
     *   <li>Logs warning if CSS stylesheet cannot be loaded (non-fatal)</li>
     * </ul>
     * 
     * @param config The configuration object containing customization parameters (must not be null)
     * @throws NullPointerException if config is null
     * 
     * @see ToolsPanelConfig
     * @see #loadStylesheet()
     * @see #applyCustomCssClasses()
     */
    public ToolsPanelTemplate(ToolsPanelConfig config) {
        // Validate configuration (Requirement 9.4)
        this.config = Objects.requireNonNull(config, "ToolsPanelConfig cannot be null");
        
        // Initialize tool items map
        this.toolItems = new HashMap<>();
        
        // Initialize tool buttons map
        this.toolButtons = new HashMap<>();
        
        // Initialize selection state
        this.selectedToolId = null;
        
        // Apply tool spacing from config (Requirement 2.7)
        this.setSpacing(config.getToolSpacing());
        
        // Load and apply CSS stylesheet (Requirement 2.9, 7.2)
        loadStylesheet();
        
        // Apply custom CSS classes from config (Requirement 6.5)
        applyCustomCssClasses();
    }
    
    /**
     * Loads and applies the CSS stylesheet to this panel.
     * <p>
     * This method attempts to load the application's standard stylesheet from
     * the resource path {@code /com/example/scenory/styles.css}. The stylesheet
     * is applied to the scene's stylesheets list.
     * </p>
     * 
     * <h3>Error Handling:</h3>
     * <p>
     * If the stylesheet cannot be loaded (file not found, resource path invalid),
     * this method logs a warning and continues without custom styling. This is
     * a non-fatal error that allows the panel to function with default JavaFX styling.
     * </p>
     * 
     * <h3>Requirements:</h3>
     * <ul>
     *   <li><strong>Requirement 2.9</strong>: THE Tools_Panel Template SHALL load styling from the CSS_Stylesheet</li>
     *   <li><strong>Requirement 7.2</strong>: THE Template_Component SHALL support the existing CSS_Stylesheet loading mechanism</li>
     *   <li><strong>Requirement 9.2</strong>: WHEN CSS_Stylesheet loading fails, THE Template_Component SHALL log a warning and continue without styling</li>
     * </ul>
     * 
     * @see ToolsPanelConfig
     */
    private void loadStylesheet() {
        try {
            String cssFile = getClass()
                .getResource("/com/example/scenory/styles.css")
                .toExternalForm();
            this.getStylesheets().add(cssFile);
            System.out.println("✅ [ToolsPanelTemplate] CSS loaded successfully");
        } catch (Exception e) {
            System.out.println("⚠️ [ToolsPanelTemplate] CSS loading failed: " + e.getMessage());
            System.out.println("   Continuing with default styling");
        }
    }
    
    /**
     * Applies custom CSS classes from the configuration to this panel.
     * <p>
     * This method reads the style classes list from the configuration and adds
     * them to the panel's CSS style classes. This allows users to customize the
     * panel's appearance without modifying the template code.
     * </p>
     * 
     * <h3>Configuration Source:</h3>
     * <p>
     * The style classes are retrieved from {@link ToolsPanelConfig#getStyleClasses()},
     * which returns a defensive copy of the configuration's style classes list.
     * </p>
     * 
     * <h3>Requirements:</h3>
     * <ul>
     *   <li><strong>Requirement 6.2</strong>: WHEN a Configuration_Object is provided, THE Template_Component SHALL apply the specified styling options</li>
     *   <li><strong>Requirement 6.5</strong>: THE Configuration_Object SHALL support specifying custom CSS class names for styling customization</li>
     * </ul>
     * 
     * @see ToolsPanelConfig#getStyleClasses()
     */
    private void applyCustomCssClasses() {
        if (config.getStyleClasses() != null && !config.getStyleClasses().isEmpty()) {
            this.getStyleClass().addAll(config.getStyleClasses());
        }
    }
    
    /**
     * Returns the configuration object used by this panel.
     * 
     * @return The ToolsPanelConfig instance (never null)
     */
    protected ToolsPanelConfig getConfig() {
        return config;
    }
    
    /**
     * Returns the map of tool items managed by this panel.
     * <p>
     * This method provides protected access to the internal tool items map
     * for subclasses that need to customize tool management behavior.
     * </p>
     * 
     * @return The map of tool items keyed by tool ID (never null)
     */
    protected Map<String, ToolItem> getToolItems() {
        return toolItems;
    }
    
    /**
     * Returns the ID of the currently selected tool.
     * 
     * @return The selected tool ID, or null if no tool is selected
     */
    protected String getSelectedToolId() {
        return selectedToolId;
    }
    
    /**
     * Sets the currently selected tool ID.
     * <p>
     * This method is intended for subclass use when customizing selection behavior.
     * It does not perform selection logic or trigger callbacks - it only updates
     * the internal state.
     * </p>
     * 
     * @param selectedToolId The tool ID to set as selected, or null to clear selection
     */
    protected void setSelectedToolId(String selectedToolId) {
        this.selectedToolId = selectedToolId;
    }
    
    /**
     * Adds a tool item to the panel with the specified properties.
     * <p>
     * This method creates a styled button with the provided icon and optional label
     * (based on the showLabels configuration), stores the ToolItem in the internal
     * toolItems map, and applies the iconSize from the configuration to the icon.
     * </p>
     * 
     * <h3>Button Creation:</h3>
     * <p>
     * The method creates a JavaFX button with:
     * </p>
     * <ul>
     *   <li><strong>Icon</strong>: The provided Node, scaled to the configured iconSize</li>
     *   <li><strong>Label</strong>: The provided label text (only if showLabels is true in config)</li>
     *   <li><strong>Action</strong>: Click handler that invokes the onSelect callback with the tool ID</li>
     *   <li><strong>Styling</strong>: CSS class "tool-button" for consistent styling</li>
     * </ul>
     * 
     * <h3>Icon Sizing:</h3>
     * <p>
     * If the provided icon Node is an {@link javafx.scene.image.ImageView}, its
     * fitWidth and fitHeight properties are set to the configured iconSize. For other
     * Node types, the method attempts to apply scaling using the scaleX and scaleY
     * properties.
     * </p>
     * 
     * <h3>Tool Storage:</h3>
     * <p>
     * The ToolItem is stored in the internal toolItems map using the tool ID as the key.
     * This enables quick lookup for selection management and state updates.
     * </p>
     * 
     * <h3>Error Handling:</h3>
     * <p>
     * If the onSelect callback throws an exception when invoked, the exception is
     * caught and logged, preventing the error from disrupting the application.
     * </p>
     * 
     * <h3>Requirements:</h3>
     * <ul>
     *   <li><strong>Requirement 2.2</strong>: THE Tools_Panel Template SHALL support adding tool items with icon, label, and action callback</li>
     *   <li><strong>Requirement 2.3</strong>: WHEN a tool item is added, THE Tools_Panel Template SHALL create a styled button with the provided icon and label</li>
     *   <li><strong>Requirement 2.7</strong>: THE Tools_Panel Template SHALL apply consistent spacing between tool items</li>
     *   <li><strong>Requirement 2.8</strong>: THE Tools_Panel Template SHALL support optional tool groups with separators</li>
     *   <li><strong>Requirement 7.3</strong>: THE Template_Component SHALL use Consumer_Callback interfaces compatible with existing callback patterns</li>
     *   <li><strong>Requirement 9.3</strong>: WHEN a Consumer_Callback throws an exception, THE Template_Component SHALL catch the exception and log an error</li>
     * </ul>
     * 
     * @param id The unique identifier for this tool (must not be null)
     * @param icon The JavaFX Node representing the tool's icon (must not be null)
     * @param label The display label for the tool (may be null or empty)
     * @param onSelect The callback invoked when the tool is selected (may be null)
     * @throws NullPointerException if id or icon is null
     * 
     * @see ToolItem
     * @see ToolsPanelConfig#isShowLabels()
     * @see ToolsPanelConfig#getIconSize()
     */
    public void addTool(String id, Node icon, String label, java.util.function.Consumer<String> onSelect) {
        // Create ToolItem and store in map
        ToolItem toolItem = new ToolItem(id, icon, label, onSelect);
        toolItems.put(id, toolItem);
        
        // Apply icon size from config
        applyIconSize(icon);
        
        // Create button with icon and optional label
        Button button = new Button();
        button.setGraphic(icon);
        
        // Add label if showLabels is enabled in config
        if (config.isShowLabels() && label != null && !label.isEmpty()) {
            button.setText(label);
        }
        
        // Add CSS class for styling
        button.getStyleClass().add("tool-button");
        
        // Store button reference for selection management
        toolButtons.put(id, button);
        
        // Set up click handler
        button.setOnAction(event -> {
            // Handle selection based on selection mode
            if (config.getSelectionMode() == ToolsPanelConfig.SelectionMode.SINGLE) {
                selectTool(id);
            }
            
            // Invoke callback with error handling (Requirement 9.3)
            if (onSelect != null) {
                try {
                    onSelect.accept(id);
                } catch (Exception e) {
                    System.err.println("❌ [ToolsPanelTemplate] Callback error for tool '" + id + "': " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        
        // Add button to the panel
        this.getChildren().add(button);
        
        // Apply initial selection if this is the configured initial selection
        if (id.equals(config.getInitialSelection()) && selectedToolId == null) {
            selectTool(id);
        }
    }
    
    /**
     * Applies the configured icon size to the provided icon Node.
     * <p>
     * This method adjusts the size of the icon based on its type:
     * </p>
     * <ul>
     *   <li><strong>ImageView</strong>: Sets fitWidth and fitHeight to iconSize, preserves aspect ratio</li>
     *   <li><strong>Region</strong>: Sets prefWidth and prefHeight to iconSize</li>
     *   <li><strong>Other Nodes</strong>: Applies scale transformation based on iconSize/default ratio</li>
     * </ul>
     * 
     * @param icon The icon Node to resize (must not be null)
     */
    private void applyIconSize(javafx.scene.Node icon) {
        double iconSize = config.getIconSize();
        
        if (icon instanceof javafx.scene.image.ImageView) {
            javafx.scene.image.ImageView imageView = (javafx.scene.image.ImageView) icon;
            imageView.setFitWidth(iconSize);
            imageView.setFitHeight(iconSize);
            imageView.setPreserveRatio(true);
        } else if (icon instanceof javafx.scene.layout.Region) {
            javafx.scene.layout.Region region = (javafx.scene.layout.Region) icon;
            region.setPrefWidth(iconSize);
            region.setPrefHeight(iconSize);
        } else {
            // For other node types, apply scale transformation
            double scale = iconSize / 24.0; // Assume default size is 24
            icon.setScaleX(scale);
            icon.setScaleY(scale);
        }
    }
    
    /**
     * Adds a tool group separator with an optional group name label.
     * <p>
     * This method creates a visual separator to organize tools into logical groups.
     * If a group name is provided (non-null and non-empty), a label is added above
     * the separator line.
     * </p>
     * 
     * <h3>Visual Structure:</h3>
     * <p>
     * The tool group consists of:
     * </p>
     * <ul>
     *   <li><strong>Group Label</strong> (optional): A styled label displaying the group name</li>
     *   <li><strong>Separator Line</strong>: A horizontal line or visual divider</li>
     * </ul>
     * 
     * <h3>Styling:</h3>
     * <p>
     * The group label receives the CSS class "tool-group-label" for styling.
     * The separator receives the CSS class "tool-group-separator" for styling.
     * </p>
     * 
     * <h3>Requirements:</h3>
     * <ul>
     *   <li><strong>Requirement 2.8</strong>: THE Tools_Panel Template SHALL support optional tool groups with separators</li>
     * </ul>
     * 
     * @param groupName The name of the tool group (may be null or empty for unlabeled separator)
     * 
     * @see #addSeparator()
     */
    public void addToolGroup(String groupName) {
        // Add group label if name is provided
        if (groupName != null && !groupName.isEmpty()) {
            javafx.scene.control.Label groupLabel = new javafx.scene.control.Label(groupName);
            groupLabel.getStyleClass().add("tool-group-label");
            this.getChildren().add(groupLabel);
        }
        
        // Add separator line
        addSeparator();
    }
    
    /**
     * Adds a visual separator to the panel.
     * <p>
     * This method creates a horizontal separator line to visually divide tool items
     * or tool groups. The separator is implemented as a JavaFX {@link javafx.scene.control.Separator}
     * with consistent styling.
     * </p>
     * 
     * <h3>Visual Appearance:</h3>
     * <p>
     * The separator is a horizontal line that spans the width of the panel.
     * It receives the CSS class "tool-separator" for styling customization.
     * </p>
     * 
     * <h3>Requirements:</h3>
     * <ul>
     *   <li><strong>Requirement 2.8</strong>: THE Tools_Panel Template SHALL support optional tool groups with separators</li>
     * </ul>
     * 
     * @see #addToolGroup(String)
     */
    public void addSeparator() {
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
        separator.getStyleClass().add("tool-separator");
        this.getChildren().add(separator);
    }
    
    /**
     * Selects a tool by its unique identifier and updates visual highlighting.
     * <p>
     * This method implements the single-selection behavior for tools. When a tool
     * is selected:
     * </p>
     * <ol>
     *   <li>The previously selected tool (if any) is deselected and its visual highlight removed</li>
     *   <li>The new tool is marked as selected and receives visual highlighting</li>
     *   <li>The protected {@link #onToolSelected(String, String)} hook is invoked for subclass customization</li>
     * </ol>
     * 
     * <h3>Visual Highlighting:</h3>
     * <p>
     * Visual highlighting is achieved by adding the CSS class "tool-button-selected" to
     * the selected tool's button and removing it from the previously selected button.
     * </p>
     * 
     * <h3>Selection Mode:</h3>
     * <p>
     * This method respects the configured selection mode:
     * </p>
     * <ul>
     *   <li><strong>SINGLE</strong>: Normal behavior - one tool selected at a time</li>
     *   <li><strong>MULTIPLE</strong>: Not fully supported by this method (use for SINGLE mode only)</li>
     *   <li><strong>NONE</strong>: Method does nothing (selection disabled)</li>
     * </ul>
     * 
     * <h3>Error Handling:</h3>
     * <p>
     * If the specified tool ID does not exist, this method does nothing (no-op).
     * No exception is thrown for invalid tool IDs.
     * </p>
     * 
     * <h3>Requirements:</h3>
     * <ul>
     *   <li><strong>Requirement 2.4</strong>: THE Tools_Panel Template SHALL support tool selection state management (single selection)</li>
     *   <li><strong>Requirement 2.5</strong>: WHEN a tool is selected, THE Tools_Panel Template SHALL visually highlight the selected tool</li>
     *   <li><strong>Requirement 2.6</strong>: WHEN a different tool is selected, THE Tools_Panel Template SHALL deselect the previously selected tool</li>
     * </ul>
     * 
     * @param toolId The unique identifier of the tool to select (must not be null)
     * 
     * @see #getSelectedTool()
     * @see #clearSelection()
     * @see #onToolSelected(String, String)
     */
    public void selectTool(String toolId) {
        // Do nothing if selection mode is NONE
        if (config.getSelectionMode() == ToolsPanelConfig.SelectionMode.NONE) {
            return;
        }
        
        // Validate that the tool exists
        if (!toolItems.containsKey(toolId)) {
            System.err.println("⚠️ [ToolsPanelTemplate] Cannot select non-existent tool: " + toolId);
            return;
        }
        
        // Store previous selection for the hook
        String previousId = selectedToolId;
        
        // Deselect previously selected tool (Requirement 2.6)
        if (selectedToolId != null && toolButtons.containsKey(selectedToolId)) {
            Button previousButton = toolButtons.get(selectedToolId);
            previousButton.getStyleClass().remove("tool-button-selected");
        }
        
        // Update selection state
        selectedToolId = toolId;
        
        // Apply visual highlighting to the new selection (Requirement 2.5)
        if (toolButtons.containsKey(toolId)) {
            Button newButton = toolButtons.get(toolId);
            if (!newButton.getStyleClass().contains("tool-button-selected")) {
                newButton.getStyleClass().add("tool-button-selected");
            }
        }
        
        // Invoke protected hook for subclass customization
        try {
            onToolSelected(previousId, toolId);
        } catch (Exception e) {
            System.err.println("❌ [ToolsPanelTemplate] Error in onToolSelected hook: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the ID of the currently selected tool.
     * <p>
     * This method provides access to the current selection state. It returns the
     * tool ID that was most recently selected via {@link #selectTool(String)} or
     * through user interaction (clicking a tool button).
     * </p>
     * 
     * <h3>Return Value:</h3>
     * <ul>
     *   <li>Returns the tool ID if a tool is currently selected</li>
     *   <li>Returns null if no tool is selected</li>
     *   <li>Returns null if selection mode is NONE</li>
     * </ul>
     * 
     * @return The ID of the currently selected tool, or null if no tool is selected
     * 
     * @see #selectTool(String)
     * @see #clearSelection()
     */
    public String getSelectedTool() {
        return selectedToolId;
    }
    
    /**
     * Clears the current tool selection and removes visual highlighting.
     * <p>
     * This method deselects the currently selected tool (if any) by:
     * </p>
     * <ol>
     *   <li>Removing the visual highlight CSS class from the selected button</li>
     *   <li>Setting the selectedToolId to null</li>
     *   <li>Invoking the {@link #onToolSelected(String, String)} hook with null as the new selection</li>
     * </ol>
     * 
     * <h3>Use Cases:</h3>
     * <ul>
     *   <li>Resetting the panel to no selection state</li>
     *   <li>Programmatically deselecting tools</li>
     *   <li>Implementing custom selection logic in subclasses</li>
     * </ul>
     * 
     * <h3>Behavior:</h3>
     * <p>
     * If no tool is currently selected, this method does nothing (no-op).
     * </p>
     * 
     * @see #selectTool(String)
     * @see #getSelectedTool()
     * @see #onToolSelected(String, String)
     */
    public void clearSelection() {
        // Store previous selection for the hook
        String previousId = selectedToolId;
        
        // Remove visual highlighting from the currently selected tool
        if (selectedToolId != null && toolButtons.containsKey(selectedToolId)) {
            Button selectedButton = toolButtons.get(selectedToolId);
            selectedButton.getStyleClass().remove("tool-button-selected");
        }
        
        // Clear selection state
        selectedToolId = null;
        
        // Invoke protected hook for subclass customization
        try {
            onToolSelected(previousId, null);
        } catch (Exception e) {
            System.err.println("❌ [ToolsPanelTemplate] Error in onToolSelected hook: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Protected hook method invoked when tool selection changes.
     * <p>
     * This method is called whenever the selection state changes, including:
     * </p>
     * <ul>
     *   <li>When a tool is selected via {@link #selectTool(String)}</li>
     *   <li>When a tool is selected via user interaction (clicking)</li>
     *   <li>When selection is cleared via {@link #clearSelection()}</li>
     * </ul>
     * 
     * <h3>Parameters:</h3>
     * <ul>
     *   <li><strong>previousId</strong>: The ID of the previously selected tool, or null if no tool was selected</li>
     *   <li><strong>newId</strong>: The ID of the newly selected tool, or null if selection is being cleared</li>
     * </ul>
     * 
     * <h3>Customization:</h3>
     * <p>
     * Subclasses can override this method to implement custom behavior when selection
     * changes, such as:
     * </p>
     * <ul>
     *   <li>Logging selection changes</li>
     *   <li>Updating related UI components</li>
     *   <li>Triggering additional side effects</li>
     *   <li>Implementing custom validation logic</li>
     * </ul>
     * 
     * <h3>Default Implementation:</h3>
     * <p>
     * The default implementation does nothing. Subclasses should override this method
     * to provide custom behavior.
     * </p>
     * 
     * <h3>Exception Handling:</h3>
     * <p>
     * Any exceptions thrown by this method are caught and logged by the caller,
     * preventing selection logic from being disrupted.
     * </p>
     * 
     * @param previousId The ID of the previously selected tool, or null
     * @param newId The ID of the newly selected tool, or null
     */
    protected void onToolSelected(String previousId, String newId) {
        // Default implementation does nothing
        // Subclasses can override to customize selection behavior
    }
}
