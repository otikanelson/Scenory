package com.example.scenory.view.templates.panels;

import com.example.scenory.view.templates.config.StatusPanelConfig;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.Objects;

/**
 * Template for status bar panels displaying zoom, FPS, and frame information.
 * <p>
 * StatusPanelTemplate provides a standardized horizontal container for displaying
 * application status information with consistent formatting and dynamic updates.
 * </p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Zoom level display as percentage</li>
 *   <li>FPS (frames per second) display</li>
 *   <li>Current frame / total frames display</li>
 *   <li>Dynamic value updates</li>
 *   <li>Configurable visibility for each status item</li>
 *   <li>Horizontal layout with consistent spacing</li>
 *   <li>Automatic CSS stylesheet loading</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Create configuration
 * StatusPanelConfig config = StatusPanelConfig.builder()
 *     .initialZoom(150.0)
 *     .initialFPS(30)
 *     .initialFrameInfo(5, 100)
 *     .showZoom(true)
 *     .showFPS(true)
 *     .showFrameInfo(true)
 *     .build();
 * 
 * // Create status panel
 * StatusPanelTemplate statusPanel = new StatusPanelTemplate(config);
 * 
 * // Update values
 * statusPanel.setZoom(200.0);
 * statusPanel.setFPS(60);
 * statusPanel.setFrameInfo(10, 100);
 * }</pre>
 * 
 * <h3>Requirements Validated:</h3>
 * <ul>
 *   <li><strong>Requirement 5.1</strong>: THE Frame_Count_Panel Template SHALL display zoom level as a percentage value</li>
 *   <li><strong>Requirement 5.2</strong>: THE Frame_Count_Panel Template SHALL display FPS (frames per second) as an integer value</li>
 *   <li><strong>Requirement 5.3</strong>: THE Frame_Count_Panel Template SHALL display current frame number and total frame count</li>
 *   <li><strong>Requirement 5.4</strong>: WHEN zoom level is updated, THE Frame_Count_Panel Template SHALL refresh the displayed zoom percentage</li>
 *   <li><strong>Requirement 5.5</strong>: WHEN FPS is updated, THE Frame_Count_Panel Template SHALL refresh the displayed FPS value</li>
 *   <li><strong>Requirement 5.6</strong>: WHEN frame information is updated, THE Frame_Count_Panel Template SHALL refresh the current and total frame display</li>
 *   <li><strong>Requirement 5.7</strong>: THE Frame_Count_Panel Template SHALL arrange zoom, FPS, and frame count horizontally with consistent spacing</li>
 *   <li><strong>Requirement 5.8</strong>: THE Frame_Count_Panel Template SHALL apply consistent styling from the CSS_Stylesheet</li>
 *   <li><strong>Requirement 5.9</strong>: THE Frame_Count_Panel Template SHALL support setting initial values through a Configuration_Object</li>
 * </ul>
 * 
 * @see StatusPanelConfig
 */
public class StatusPanelTemplate extends HBox {
    
    private final StatusPanelConfig config;
    
    private double zoom;
    private int fps;
    private int currentFrame;
    private int totalFrames;
    
    private Label zoomLabel;
    private Label fpsLabel;
    private Label frameInfoLabel;
    
    /**
     * Creates a new StatusPanelTemplate with the specified configuration.
     * 
     * @param config The configuration object (must not be null)
     * @throws NullPointerException if config is null
     */
    public StatusPanelTemplate(StatusPanelConfig config) {
        this.config = Objects.requireNonNull(config, "StatusPanelConfig cannot be null");
        
        // Initialize values from config
        this.zoom = config.getInitialZoom();
        this.fps = config.getInitialFPS();
        this.currentFrame = config.getInitialCurrentFrame();
        this.totalFrames = config.getInitialTotalFrames();
        
        // Set up layout
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().add("status-panel");
        
        // Create status items
        createStatusItems();
        
        // Load CSS and apply custom classes
        loadStylesheet();
        applyCustomCssClasses();
    }
    
    /**
     * Creates the status item labels based on configuration.
     */
    private void createStatusItems() {
        // Add zoom display
        if (config.isShowZoom()) {
            Label zoomTitleLabel = new Label("Zoom:");
            zoomTitleLabel.getStyleClass().add("status-item-title");
            
            zoomLabel = new Label(formatZoom(zoom));
            zoomLabel.getStyleClass().add("status-item-value");
            
            this.getChildren().addAll(zoomTitleLabel, zoomLabel);
        }
        
        // Add separator if needed
        if (config.isShowZoom() && (config.isShowFPS() || config.isShowFrameInfo())) {
            this.getChildren().add(createSeparator());
        }
        
        // Add FPS display
        if (config.isShowFPS()) {
            Label fpsTitleLabel = new Label("FPS:");
            fpsTitleLabel.getStyleClass().add("status-item-title");
            
            fpsLabel = new Label(formatFPS(fps));
            fpsLabel.getStyleClass().add("status-item-value");
            
            this.getChildren().addAll(fpsTitleLabel, fpsLabel);
        }
        
        // Add separator if needed
        if (config.isShowFPS() && config.isShowFrameInfo()) {
            this.getChildren().add(createSeparator());
        }
        
        // Add frame info display
        if (config.isShowFrameInfo()) {
            Label frameTitleLabel = new Label("Frame:");
            frameTitleLabel.getStyleClass().add("status-item-title");
            
            frameInfoLabel = new Label(formatFrameInfo(currentFrame, totalFrames));
            frameInfoLabel.getStyleClass().add("status-item-value");
            
            this.getChildren().addAll(frameTitleLabel, frameInfoLabel);
        }
    }
    
    /**
     * Creates a visual separator for status items.
     * 
     * @return A separator region
     */
    private Region createSeparator() {
        Region separator = new Region();
        separator.getStyleClass().add("status-separator");
        separator.setPrefWidth(1);
        separator.setPrefHeight(20);
        return separator;
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
            System.out.println("✅ [StatusPanelTemplate] CSS loaded successfully");
        } catch (Exception e) {
            System.out.println("⚠️ [StatusPanelTemplate] CSS loading failed: " + e.getMessage());
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
     * Sets the zoom level and updates the display.
     * 
     * @param zoomPercent The zoom level as a percentage (e.g., 100.0 = 100%)
     */
    public void setZoom(double zoomPercent) {
        this.zoom = zoomPercent;
        if (zoomLabel != null) {
            zoomLabel.setText(formatZoom(zoom));
        }
    }
    
    /**
     * Returns the current zoom level.
     * 
     * @return The zoom level as a percentage
     */
    public double getZoom() {
        return zoom;
    }
    
    /**
     * Sets the FPS and updates the display.
     * 
     * @param fps The frames per second value
     */
    public void setFPS(int fps) {
        this.fps = fps;
        if (fpsLabel != null) {
            fpsLabel.setText(formatFPS(fps));
        }
    }
    
    /**
     * Returns the current FPS.
     * 
     * @return The frames per second value
     */
    public int getFPS() {
        return fps;
    }
    
    /**
     * Sets the frame information and updates the display.
     * 
     * @param currentFrame The current frame number
     * @param totalFrames The total number of frames
     */
    public void setFrameInfo(int currentFrame, int totalFrames) {
        this.currentFrame = currentFrame;
        this.totalFrames = totalFrames;
        if (frameInfoLabel != null) {
            frameInfoLabel.setText(formatFrameInfo(currentFrame, totalFrames));
        }
    }
    
    /**
     * Returns the current frame number.
     * 
     * @return The current frame number
     */
    public int getCurrentFrame() {
        return currentFrame;
    }
    
    /**
     * Returns the total number of frames.
     * 
     * @return The total frame count
     */
    public int getTotalFrames() {
        return totalFrames;
    }
    
    /**
     * Formats the zoom value for display.
     * 
     * @param zoom The zoom percentage
     * @return Formatted zoom string (e.g., "100%")
     */
    private String formatZoom(double zoom) {
        return String.format("%.0f%%", zoom);
    }
    
    /**
     * Formats the FPS value for display.
     * 
     * @param fps The FPS value
     * @return Formatted FPS string (e.g., "30")
     */
    private String formatFPS(int fps) {
        return String.valueOf(fps);
    }
    
    /**
     * Formats the frame information for display.
     * 
     * @param current The current frame number
     * @param total The total frame count
     * @return Formatted frame info string (e.g., "5 / 100")
     */
    private String formatFrameInfo(int current, int total) {
        return current + " / " + total;
    }
    
    /**
     * Returns the configuration object.
     * 
     * @return The StatusPanelConfig instance
     */
    protected StatusPanelConfig getConfig() {
        return config;
    }
}
