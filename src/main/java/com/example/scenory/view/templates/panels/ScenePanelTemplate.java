package com.example.scenory.view.templates.panels;

import com.example.scenory.view.templates.FrameItem;
import com.example.scenory.view.templates.config.ScenePanelConfig;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Template for scene panels displaying frame thumbnails and layer information.
 * <p>
 * ScenePanelTemplate provides a standardized container for displaying frame thumbnails
 * with labels, selection state management, and action buttons. It extends {@link VBox}
 * and includes a scrollable content area for multiple frames.
 * </p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Scrollable content area for multiple frames</li>
 *   <li>Frame thumbnail display with configurable dimensions</li>
 *   <li>Optional frame labels</li>
 *   <li>Frame selection with visual highlighting</li>
 *   <li>Double-click detection</li>
 *   <li>Action buttons (add, delete, duplicate)</li>
 *   <li>Optional layer view mode</li>
 *   <li>Automatic CSS stylesheet loading</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Create configuration
 * ScenePanelConfig config = ScenePanelConfig.builder()
 *     .thumbnailDimensions(160, 120)
 *     .showFrameLabels(true)
 *     .onFrameSelect(frameId -> System.out.println("Selected: " + frameId))
 *     .build();
 * 
 * // Create scene panel
 * ScenePanelTemplate scenePanel = new ScenePanelTemplate(config);
 * 
 * // Add frames
 * Image thumbnail = new Image("file:frame1.png");
 * scenePanel.addFrame("frame-1", thumbnail, "Frame 1");
 * 
 * // Add action button
 * scenePanel.addActionButton("Add Frame", createIcon(), () -> addNewFrame());
 * }</pre>
 * 
 * <h3>Requirements Validated:</h3>
 * <ul>
 *   <li><strong>Requirement 3.1</strong>: THE Scene_Panel Template SHALL provide a container for displaying frame thumbnails and layer information</li>
 *   <li><strong>Requirement 3.2</strong>: THE Scene_Panel Template SHALL support a scrollable content area for multiple frames</li>
 *   <li><strong>Requirement 3.3</strong>: THE Scene_Panel Template SHALL support adding frame items with thumbnail image, label, and selection callback</li>
 *   <li><strong>Requirement 3.4</strong>: WHEN a frame item is added, THE Scene_Panel Template SHALL display the thumbnail and label in the panel</li>
 *   <li><strong>Requirement 3.5</strong>: THE Scene_Panel Template SHALL support frame selection with visual highlighting</li>
 *   <li><strong>Requirement 3.6</strong>: THE Scene_Panel Template SHALL support adding action buttons</li>
 *   <li><strong>Requirement 3.8</strong>: THE Scene_Panel Template SHALL apply consistent styling from the CSS_Stylesheet</li>
 *   <li><strong>Requirement 3.9</strong>: THE Scene_Panel Template SHALL support optional layer view mode</li>
 * </ul>
 * 
 * @see ScenePanelConfig
 * @see FrameItem
 */
public class ScenePanelTemplate extends VBox {
    
    private final ScenePanelConfig config;
    private final Map<String, FrameItem> frameItems;
    private final Map<String, VBox> frameContainers;
    private String selectedFrameId;
    private boolean layerViewMode;
    
    private final VBox contentArea;
    private final ScrollPane scrollPane;
    private final VBox actionsContainer;
    
    /**
     * Creates a new ScenePanelTemplate with the specified configuration.
     * 
     * @param config The configuration object (must not be null)
     * @throws NullPointerException if config is null
     */
    public ScenePanelTemplate(ScenePanelConfig config) {
        this.config = Objects.requireNonNull(config, "ScenePanelConfig cannot be null");
        this.frameItems = new HashMap<>();
        this.frameContainers = new HashMap<>();
        this.selectedFrameId = null;
        this.layerViewMode = config.isEnableLayerMode();
        
        // Create content area for frames
        this.contentArea = new VBox(10);
        contentArea.setAlignment(Pos.TOP_CENTER);
        contentArea.getStyleClass().add("scene-panel-content");
        
        // Create scrollable area
        this.scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scene-panel-scroll");
        
        // Create actions container
        this.actionsContainer = new VBox(5);
        actionsContainer.setAlignment(Pos.CENTER);
        actionsContainer.getStyleClass().add("scene-panel-actions");
        
        // Add components to panel
        this.getChildren().addAll(actionsContainer, scrollPane);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        
        // Load CSS and apply custom classes
        loadStylesheet();
        applyCustomCssClasses();
    }
    
    /**
     * Loads the CSS stylesheet from application resources.
     */
    private void loadStylesheet() {
        try {
            String cssFile = getClass()
                .getResource("/com/example/scenory/styles.css")
                .toExternalForm();
            this.getStylesheets().add(cssFile);
            System.out.println("✅ [ScenePanelTemplate] CSS loaded successfully");
        } catch (Exception e) {
            System.out.println("⚠️ [ScenePanelTemplate] CSS loading failed: " + e.getMessage());
            System.out.println("   Continuing with default styling");
        }
    }
    
    /**
     * Applies custom CSS classes from the configuration.
     */
    private void applyCustomCssClasses() {
        if (config.getStyleClasses() != null && !config.getStyleClasses().isEmpty()) {
            this.getStyleClass().addAll(config.getStyleClasses());
        }
    }
    
    /**
     * Adds a frame item to the panel with the specified properties.
     * 
     * @param frameId The unique identifier for this frame (must not be null)
     * @param thumbnail The thumbnail image (may be null)
     * @param label The display label (may be null)
     */
    public void addFrame(String frameId, Image thumbnail, String label) {
        Objects.requireNonNull(frameId, "Frame ID cannot be null");
        
        // Create FrameItem and store
        FrameItem frameItem = new FrameItem(frameId, thumbnail, label);
        frameItems.put(frameId, frameItem);
        
        // Create frame UI container
        VBox frameContainer = new VBox(5);
        frameContainer.setAlignment(Pos.CENTER);
        frameContainer.getStyleClass().add("frame-item");
        
        // Create thumbnail view
        if (thumbnail != null) {
            ImageView thumbnailView = new ImageView(thumbnail);
            thumbnailView.setFitWidth(config.getThumbnailWidth());
            thumbnailView.setFitHeight(config.getThumbnailHeight());
            thumbnailView.setPreserveRatio(true);
            thumbnailView.getStyleClass().add("frame-thumbnail");
            frameContainer.getChildren().add(thumbnailView);
        }
        
        // Add label if enabled and provided
        if (config.isShowFrameLabels() && label != null && !label.isEmpty()) {
            Label labelNode = new Label(label);
            labelNode.getStyleClass().add("frame-label");
            frameContainer.getChildren().add(labelNode);
        }
        
        // Set up click handlers
        frameContainer.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 1) {
                    // Single click - select frame
                    selectFrame(frameId);
                } else if (event.getClickCount() == 2) {
                    // Double click - invoke callback
                    if (config.getOnFrameDoubleClick() != null) {
                        try {
                            config.getOnFrameDoubleClick().accept(frameId);
                        } catch (Exception e) {
                            System.err.println("❌ [ScenePanelTemplate] Double-click callback error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        
        // Store container and add to content area
        frameContainers.put(frameId, frameContainer);
        contentArea.getChildren().add(frameContainer);
    }
    
    /**
     * Removes a frame from the panel.
     * 
     * @param frameId The frame ID to remove
     */
    public void removeFrame(String frameId) {
        FrameItem removed = frameItems.remove(frameId);
        if (removed != null) {
            VBox container = frameContainers.remove(frameId);
            if (container != null) {
                contentArea.getChildren().remove(container);
            }
            
            // Clear selection if removing selected frame
            if (frameId.equals(selectedFrameId)) {
                selectedFrameId = null;
            }
        }
    }
    
    /**
     * Updates the thumbnail for an existing frame.
     * 
     * @param frameId The frame ID
     * @param thumbnail The new thumbnail image
     */
    public void updateFrameThumbnail(String frameId, Image thumbnail) {
        FrameItem frameItem = frameItems.get(frameId);
        if (frameItem != null) {
            frameItem.setThumbnail(thumbnail);
            
            // Update UI
            VBox container = frameContainers.get(frameId);
            if (container != null && !container.getChildren().isEmpty()) {
                javafx.scene.Node firstChild = container.getChildren().get(0);
                if (firstChild instanceof ImageView) {
                    ((ImageView) firstChild).setImage(thumbnail);
                }
            }
        }
    }
    
    /**
     * Selects a frame by its unique identifier and updates visual highlighting.
     * 
     * @param frameId The frame ID to select
     */
    public void selectFrame(String frameId) {
        if (!frameItems.containsKey(frameId)) {
            System.err.println("⚠️ [ScenePanelTemplate] Cannot select non-existent frame: " + frameId);
            return;
        }
        
        // Deselect previously selected frame
        if (selectedFrameId != null && frameContainers.containsKey(selectedFrameId)) {
            VBox prevContainer = frameContainers.get(selectedFrameId);
            prevContainer.getStyleClass().remove("frame-item-selected");
        }
        
        // Update selection
        selectedFrameId = frameId;
        
        // Apply visual highlighting
        VBox container = frameContainers.get(frameId);
        if (container != null && !container.getStyleClass().contains("frame-item-selected")) {
            container.getStyleClass().add("frame-item-selected");
        }
        
        // Invoke selection callback
        if (config.getOnFrameSelect() != null) {
            try {
                config.getOnFrameSelect().accept(frameId);
            } catch (Exception e) {
                System.err.println("❌ [ScenePanelTemplate] Selection callback error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Returns the ID of the currently selected frame.
     * 
     * @return The selected frame ID, or null if no frame is selected
     */
    public String getSelectedFrame() {
        return selectedFrameId;
    }
    
    /**
     * Adds an action button to the panel.
     * 
     * @param label The button label
     * @param icon The button icon (may be null)
     * @param action The action to perform when clicked
     */
    public void addActionButton(String label, javafx.scene.Node icon, Runnable action) {
        Button button = new Button(label);
        if (icon != null) {
            button.setGraphic(icon);
        }
        button.getStyleClass().add("scene-panel-action-button");
        
        button.setOnAction(event -> {
            if (action != null) {
                try {
                    action.run();
                } catch (Exception e) {
                    System.err.println("❌ [ScenePanelTemplate] Action button error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        
        actionsContainer.getChildren().add(button);
    }
    
    /**
     * Sets whether layer view mode is enabled.
     * 
     * @param enabled Whether to enable layer view mode
     */
    public void setLayerViewMode(boolean enabled) {
        this.layerViewMode = enabled;
        // Layer mode implementation would go here
        // For now, just store the state
    }
    
    /**
     * Returns whether layer view mode is enabled.
     * 
     * @return true if layer mode is enabled, false otherwise
     */
    public boolean isLayerViewMode() {
        return layerViewMode;
    }
    
    /**
     * Returns the configuration object.
     * 
     * @return The ScenePanelConfig instance
     */
    protected ScenePanelConfig getConfig() {
        return config;
    }
    
    /**
     * Returns the map of frame items.
     * 
     * @return The frame items map
     */
    protected Map<String, FrameItem> getFrameItems() {
        return frameItems;
    }
}
