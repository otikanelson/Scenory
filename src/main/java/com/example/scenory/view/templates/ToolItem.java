package com.example.scenory.view.templates;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.scene.Node;

/**
 * Data model representing a tool item in a tools panel.
 * <p>
 * A tool item consists of an identifier, an icon (JavaFX Node), an optional label,
 * and an optional selection callback. This class is used by the ToolsPanelTemplate
 * to create and manage tool buttons with standardized behavior.
 * </p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Create a tool item with icon and label
 * ImageView pencilIcon = new ImageView("pencil.png");
 * ToolItem pencilTool = new ToolItem(
 *     "pencil",
 *     pencilIcon,
 *     "Pencil",
 *     id -> System.out.println("Pencil selected: " + id)
 * );
 * 
 * // Add to tools panel
 * toolsPanel.addTool(pencilTool);
 * }</pre>
 * 
 * <h3>Requirements:</h3>
 * <ul>
 *   <li>Requirement 2.2: Tool items must have id and icon</li>
 *   <li>Requirement 2.3: Tool items support labels and selection callbacks</li>
 *   <li>Requirement 9.4: Required fields are validated with null checks</li>
 * </ul>
 * 
 * @see com.example.scenory.view.templates.panels.ToolsPanelTemplate
 */
public class ToolItem {
    
    private final String id;
    private final Node icon;
    private final String label;
    private final Consumer<String> onSelect;
    
    /**
     * Creates a new ToolItem with the specified properties.
     * <p>
     * The id and icon are required fields and cannot be null. The label and
     * onSelect callback are optional and may be null.
     * </p>
     * 
     * @param id The unique identifier for this tool (required, must not be null)
     * @param icon The JavaFX Node representing the tool's icon (required, must not be null)
     * @param label The display label for the tool (optional, may be null)
     * @param onSelect The callback invoked when the tool is selected (optional, may be null)
     * @throws NullPointerException if id or icon is null
     */
    public ToolItem(String id, Node icon, String label, Consumer<String> onSelect) {
        this.id = Objects.requireNonNull(id, "Tool ID cannot be null");
        this.icon = Objects.requireNonNull(icon, "Icon cannot be null");
        this.label = label;
        this.onSelect = onSelect;
    }
    
    /**
     * Returns the unique identifier for this tool.
     * 
     * @return The tool ID (never null)
     */
    public String getId() {
        return id;
    }
    
    /**
     * Returns the icon Node for this tool.
     * <p>
     * The icon is a JavaFX Node that can be any visual component such as
     * ImageView, SVGPath, or custom graphic.
     * </p>
     * 
     * @return The icon Node (never null)
     */
    public Node getIcon() {
        return icon;
    }
    
    /**
     * Returns the display label for this tool.
     * 
     * @return The tool label, or null if no label was provided
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Returns the selection callback for this tool.
     * <p>
     * The callback is invoked when the tool is selected, receiving the tool's
     * ID as a parameter. This allows the callback to identify which tool was selected.
     * </p>
     * 
     * @return The selection callback Consumer, or null if no callback was provided
     */
    public Consumer<String> getOnSelect() {
        return onSelect;
    }
}
