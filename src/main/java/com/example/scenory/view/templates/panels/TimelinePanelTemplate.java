package com.example.scenory.view.templates.panels;

import com.example.scenory.view.templates.config.TimelinePanelConfig;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Template for horizontal timeline panels with frame navigation and playback controls.
 * <p>
 * TimelinePanelTemplate provides a standardized container for displaying frame markers
 * horizontally with playback button, add frame button, and frame selection support.
 * </p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Horizontal layout for frame markers</li>
 *   <li>Playback button with play/pause toggle state</li>
 *   <li>Add frame button</li>
 *   <li>Frame selection with visual highlighting</li>
 *   <li>Scrollable view for timelines with many frames</li>
 *   <li>Configurable frame marker dimensions</li>
 *   <li>Automatic CSS stylesheet loading</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Create configuration
 * TimelinePanelConfig config = TimelinePanelConfig.builder()
 *     .frameMarkerDimensions(70, 50)
 *     .showPlaybackButton(true)
 *     .onFrameSelect(frameNum -> System.out.println("Frame: " + frameNum))
 *     .onPlaybackToggle(playing -> System.out.println("Playing: " + playing))
 *     .build();
 * 
 * // Create timeline panel
 * TimelinePanelTemplate timeline = new TimelinePanelTemplate(config);
 * 
 * // Add frame markers
 * for (int i = 1; i <= 10; i++) {
 *     timeline.addFrameMarker(i);
 * }
 * 
 * // Set up add frame action
 * timeline.setOnAddFrame(() -> addNewFrame());
 * }</pre>
 * 
 * <h3>Requirements Validated:</h3>
 * <ul>
 *   <li><strong>Requirement 4.1</strong>: THE Timeline_Panel Template SHALL provide a horizontal container for frame navigation</li>
 *   <li><strong>Requirement 4.2</strong>: THE Timeline_Panel Template SHALL support adding frame markers with frame number labels</li>
 *   <li><strong>Requirement 4.3</strong>: THE Timeline_Panel Template SHALL display a playback button with play/pause toggle state</li>
 *   <li><strong>Requirement 4.4</strong>: WHEN the playback button is clicked, THE Timeline_Panel Template SHALL invoke the playback Consumer_Callback</li>
 *   <li><strong>Requirement 4.5</strong>: THE Timeline_Panel Template SHALL support frame selection by clicking on frame markers</li>
 *   <li><strong>Requirement 4.6</strong>: WHEN a frame marker is selected, THE Timeline_Panel Template SHALL visually highlight the selected frame</li>
 *   <li><strong>Requirement 4.7</strong>: THE Timeline_Panel Template SHALL support an add frame button that invokes a Consumer_Callback when clicked</li>
 *   <li><strong>Requirement 4.8</strong>: THE Timeline_Panel Template SHALL provide a scrollable view for timelines with many frames</li>
 *   <li><strong>Requirement 4.9</strong>: THE Timeline_Panel Template SHALL apply consistent styling from the CSS_Stylesheet</li>
 * </ul>
 * 
 * @see TimelinePanelConfig
 */
public class TimelinePanelTemplate extends HBox {
    
    private final TimelinePanelConfig config;
    private final Map<Integer, VBox> frameMarkers;
    private Integer selectedFrame;
    private boolean playing;
    
    private final HBox controlsContainer;
    private final HBox framesContainer;
    private final ScrollPane scrollPane;
    private Button playbackButton;
    private Runnable onAddFrame;
    
    /**
     * Creates a new TimelinePanelTemplate with the specified configuration.
     * 
     * @param config The configuration object (must not be null)
     * @throws NullPointerException if config is null
     */
    public TimelinePanelTemplate(TimelinePanelConfig config) {
        this.config = Objects.requireNonNull(config, "TimelinePanelConfig cannot be null");
        this.frameMarkers = new HashMap<>();
        this.selectedFrame = null;
        this.playing = false;
        
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);
        
        // Create controls container (playback, add frame buttons)
        this.controlsContainer = new HBox(5);
        controlsContainer.setAlignment(Pos.CENTER);
        controlsContainer.getStyleClass().add("timeline-controls");
        
        // Add playback button if enabled
        if (config.isShowPlaybackButton()) {
            playbackButton = new Button("▶");
            playbackButton.getStyleClass().add("playback-button");
            playbackButton.setOnAction(event -> togglePlayback());
            controlsContainer.getChildren().add(playbackButton);
        }
        
        // Add frame button if enabled
        if (config.isShowAddFrameButton()) {
            Button addFrameButton = new Button("+");
            addFrameButton.getStyleClass().add("add-frame-button");
            addFrameButton.setOnAction(event -> {
                if (onAddFrame != null) {
                    try {
                        onAddFrame.run();
                    } catch (Exception e) {
                        System.err.println("❌ [TimelinePanelTemplate] Add frame error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            controlsContainer.getChildren().add(addFrameButton);
        }
        
        // Create frames container
        this.framesContainer = new HBox(5);
        framesContainer.setAlignment(Pos.CENTER_LEFT);
        framesContainer.getStyleClass().add("timeline-frames");
        
        // Create scrollable area for frames
        this.scrollPane = new ScrollPane(framesContainer);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("timeline-scroll");
        HBox.setHgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        
        // Add components to panel
        this.getChildren().addAll(controlsContainer, scrollPane);
        
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
            System.out.println("✅ [TimelinePanelTemplate] CSS loaded successfully");
        } catch (Exception e) {
            System.out.println("⚠️ [TimelinePanelTemplate] CSS loading failed: " + e.getMessage());
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
     * Adds a frame marker to the timeline.
     * 
     * @param frameNumber The frame number (must be positive)
     */
    public void addFrameMarker(int frameNumber) {
        if (frameNumber <= 0) {
            throw new IllegalArgumentException("Frame number must be positive: " + frameNumber);
        }
        
        // Create frame marker container
        VBox frameMarker = new VBox(2);
        frameMarker.setAlignment(Pos.CENTER);
        frameMarker.setPrefWidth(config.getFrameMarkerWidth());
        frameMarker.setPrefHeight(config.getFrameMarkerHeight());
        frameMarker.getStyleClass().add("frame-marker");
        
        // Add frame number label
        Label numberLabel = new Label(String.valueOf(frameNumber));
        numberLabel.getStyleClass().add("frame-number-label");
        frameMarker.getChildren().add(numberLabel);
        
        // Set up click handler
        frameMarker.setOnMouseClicked(event -> selectFrame(frameNumber));
        
        // Store and add to container
        frameMarkers.put(frameNumber, frameMarker);
        
        // Insert in sorted order
        List<Integer> sortedFrames = new ArrayList<>(frameMarkers.keySet());
        Collections.sort(sortedFrames);
        
        framesContainer.getChildren().clear();
        for (Integer frameNum : sortedFrames) {
            framesContainer.getChildren().add(frameMarkers.get(frameNum));
        }
    }
    
    /**
     * Removes a frame marker from the timeline.
     * 
     * @param frameNumber The frame number to remove
     */
    public void removeFrameMarker(int frameNumber) {
        VBox marker = frameMarkers.remove(frameNumber);
        if (marker != null) {
            framesContainer.getChildren().remove(marker);
            
            // Clear selection if removing selected frame
            if (Integer.valueOf(frameNumber).equals(selectedFrame)) {
                selectedFrame = null;
            }
        }
    }
    
    /**
     * Clears all frame markers from the timeline.
     */
    public void clearFrameMarkers() {
        frameMarkers.clear();
        framesContainer.getChildren().clear();
        selectedFrame = null;
    }
    
    /**
     * Selects a frame by its frame number and updates visual highlighting.
     * 
     * @param frameNumber The frame number to select
     */
    public void selectFrame(int frameNumber) {
        if (!frameMarkers.containsKey(frameNumber)) {
            System.err.println("⚠️ [TimelinePanelTemplate] Cannot select non-existent frame: " + frameNumber);
            return;
        }
        
        // Deselect previously selected frame
        if (selectedFrame != null && frameMarkers.containsKey(selectedFrame)) {
            VBox prevMarker = frameMarkers.get(selectedFrame);
            prevMarker.getStyleClass().remove("frame-marker-selected");
        }
        
        // Update selection
        selectedFrame = frameNumber;
        
        // Apply visual highlighting
        VBox marker = frameMarkers.get(frameNumber);
        if (marker != null && !marker.getStyleClass().contains("frame-marker-selected")) {
            marker.getStyleClass().add("frame-marker-selected");
        }
        
        // Invoke selection callback
        if (config.getOnFrameSelect() != null) {
            try {
                config.getOnFrameSelect().accept(frameNumber);
            } catch (Exception e) {
                System.err.println("❌ [TimelinePanelTemplate] Frame selection callback error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Returns the currently selected frame number.
     * 
     * @return The selected frame number, or null if no frame is selected
     */
    public Integer getSelectedFrame() {
        return selectedFrame;
    }
    
    /**
     * Toggles the playback state and invokes the callback.
     */
    private void togglePlayback() {
        playing = !playing;
        updatePlaybackButton();
        
        // Invoke playback toggle callback
        if (config.getOnPlaybackToggle() != null) {
            try {
                config.getOnPlaybackToggle().accept(playing);
            } catch (Exception e) {
                System.err.println("❌ [TimelinePanelTemplate] Playback toggle callback error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Sets the playback state without invoking the callback.
     * 
     * @param playing Whether playback is active
     */
    public void setPlaybackState(boolean playing) {
        this.playing = playing;
        updatePlaybackButton();
    }
    
    /**
     * Updates the playback button text based on the current state.
     */
    private void updatePlaybackButton() {
        if (playbackButton != null) {
            playbackButton.setText(playing ? "⏸" : "▶");
        }
    }
    
    /**
     * Returns whether playback is currently active.
     * 
     * @return true if playing, false if paused
     */
    public boolean isPlaying() {
        return playing;
    }
    
    /**
     * Sets the action to perform when the add frame button is clicked.
     * 
     * @param action The action to perform
     */
    public void setOnAddFrame(Runnable action) {
        this.onAddFrame = action;
    }
    
    /**
     * Returns the configuration object.
     * 
     * @return The TimelinePanelConfig instance
     */
    protected TimelinePanelConfig getConfig() {
        return config;
    }
    
    /**
     * Returns the map of frame markers.
     * 
     * @return The frame markers map
     */
    public Map<Integer, VBox> getFrameMarkers() {
        return frameMarkers;
    }
}
