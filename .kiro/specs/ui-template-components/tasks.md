# Implementation Plan: UI Template Components

## Overview

This plan implements reusable JavaFX template components for modal dialogs and interface panels (Tools Panel, Scene Panel, Timeline Panel, Status Panel). The implementation follows the existing JavaFX patterns in Scenory (FXML loading, Stage management, CSS styling) while providing flexible configuration APIs using builder patterns and consumer callbacks.

## Tasks

- [x] 1. Set up package structure and configuration objects
  - [x] 1.1 Create package structure for templates
    - Create `com.example.scenory.view.templates` package
    - Create `com.example.scenory.view.templates.config` subpackage
    - Create `com.example.scenory.view.templates.panels` subpackage
    - _Requirements: 6.1, 7.1_
  
  - [x] 1.2 Implement PanelConfig base class
    - Create abstract `PanelConfig` class with styleClasses field
    - Implement defensive copy constructor and getter
    - Add abstract validate() method for subclass validation
    - _Requirements: 6.1, 6.3, 6.7_
  
  - [x] 1.3 Implement ModalConfig with builder pattern
    - Create `ModalConfig` class with all configuration fields (title, width, height, minWidth, minHeight, resizable, closeable, ownerWindow, styleClasses, onClose)
    - Implement private constructor for builder pattern
    - Implement static Builder class with fluent API methods
    - Add validation logic in build() method for dimensions and constraints
    - Provide default values (title: "Dialog", width: 600, height: 400, resizable: true, closeable: true)
    - _Requirements: 1.1, 6.1, 6.2, 6.3, 6.4, 6.6, 6.7, 9.4_
  
  - [x] 1.4 Write unit tests for ModalConfig validation
    - Test valid configuration is accepted
    - Test invalid dimensions throw IllegalArgumentException
    - Test min dimensions validation (minWidth > width, minHeight > height)
    - Test default values are applied correctly
    - Test builder pattern produces correct configuration
    - _Requirements: 6.7, 9.4_

- [x] 2. Implement ModalTemplate component
  - [x] 2.1 Create ModalTemplate class with Stage initialization
    - Create `ModalTemplate` class with Stage and Scene fields
    - Implement factory method `create(ModalConfig)` that initializes Stage with APPLICATION_MODAL
    - Apply title, dimensions, resizable, and owner window from config
    - Set minimum dimensions if specified in config
    - Center stage on screen
    - Apply custom CSS classes from config
    - _Requirements: 1.1, 1.2, 1.4, 1.7, 1.8, 6.2, 6.5, 6.6, 7.4_
  
  - [x] 2.2 Add FXML and CSS loading support to ModalTemplate
    - Implement `createWithFXML(String fxmlPath, ModalConfig)` factory method
    - Load FXML using FXMLLoader with resource path
    - Wrap IOException in RuntimeException with descriptive message including path
    - Implement CSS loading from `/com/example/scenory/styles.css`
    - Log warning and continue if CSS loading fails (non-fatal)
    - Store FXML controller for retrieval via getController() method
    - _Requirements: 1.3, 1.5, 7.1, 7.2, 7.5, 7.6, 9.1, 9.2, 9.5, 9.6_
  
  - [x] 2.3 Add programmatic content and lifecycle methods to ModalTemplate
    - Implement `createWithContent(Node content, ModalConfig)` factory method
    - Implement `show()` and `showAndWait()` methods
    - Implement `close()` method that invokes onClose callback if provided
    - Wrap callback exceptions in try-catch with error logging
    - Implement `getStage()`, `getScene()`, and `getController()` getters
    - _Requirements: 1.6, 1.9, 1.10, 9.3, 9.6_
  
  - [x] 2.4 Write unit tests for ModalTemplate
    - Test Stage initialization with APPLICATION_MODAL
    - Test dimensions and minimum dimensions are applied
    - Test owner window is set correctly
    - Test center on screen behavior
    - Test CSS loading follows RichTextModal pattern
    - Test FXML loading error throws RuntimeException with path
    - Test CSS loading error logs warning but doesn't fail
    - Test close callback is invoked
    - Test callback exception is caught and logged
    - _Requirements: 1.2, 1.3, 1.4, 1.7, 1.8, 1.9, 1.10, 7.2, 7.6, 9.1, 9.2, 9.3_

- [~] 3. Checkpoint - Verify modal template functionality
  - Ensure all tests pass, ask the user if questions arise.

- [x] 4. Implement configuration objects for panel templates
  - [x] 4.1 Implement ToolsPanelConfig with builder pattern
    - Create `ToolsPanelConfig` extending `PanelConfig`
    - Add fields: toolSpacing (default: 8), showLabels (default: true), iconSize (default: 24), selectionMode enum (SINGLE, MULTIPLE, NONE), initialSelection
    - Implement Builder class with fluent API
    - Add validation in validate() method
    - _Requirements: 2.7, 6.1, 6.2, 6.3, 6.4, 6.6_
  
  - [x] 4.2 Implement ScenePanelConfig with builder pattern
    - Create `ScenePanelConfig` extending `PanelConfig`
    - Add fields: thumbnailWidth (default: 120), thumbnailHeight (default: 90), showFrameLabels (default: true), enableLayerMode (default: false), onFrameSelect callback, onFrameDoubleClick callback
    - Implement Builder class with fluent API
    - Add validation in validate() method
    - _Requirements: 3.2, 3.4, 3.9, 6.1, 6.2, 6.3, 6.4_
  
  - [x] 4.3 Implement TimelinePanelConfig with builder pattern
    - Create `TimelinePanelConfig` extending `PanelConfig`
    - Add fields: frameMarkerWidth (default: 60), frameMarkerHeight (default: 40), showPlaybackButton (default: true), showAddFrameButton (default: true), onFrameSelect callback, onPlaybackToggle callback
    - Implement Builder class with fluent API
    - Add validation in validate() method
    - _Requirements: 4.2, 4.3, 4.4, 4.7, 6.1, 6.2, 6.3, 6.4_
  
  - [x] 4.4 Implement StatusPanelConfig with builder pattern
    - Create `StatusPanelConfig` extending `PanelConfig`
    - Add fields: initialZoom (default: 100.0), initialFPS (default: 24), initialCurrentFrame (default: 1), initialTotalFrames (default: 1), showZoom (default: true), showFPS (default: true), showFrameInfo (default: true)
    - Implement Builder class with fluent API
    - Add validation in validate() method
    - _Requirements: 5.9, 6.1, 6.2, 6.3, 6.4_
  
  - [x] 4.5 Write unit tests for panel configuration objects
    - Test default values for all config classes
    - Test builder pattern produces correct configurations
    - Test validation logic for each config class
    - Test invalid values throw IllegalArgumentException
    - _Requirements: 6.3, 6.4, 6.7, 9.4_

- [x] 5. Implement data model classes
  - [x] 5.1 Create ToolItem data model
    - Create `ToolItem` class with fields: id, icon (Node), label, onSelect callback
    - Implement constructor with null validation for required fields (id, icon)
    - Implement getters for all fields
    - _Requirements: 2.2, 2.3, 9.4_
  
  - [x] 5.2 Create FrameItem data model
    - Create `FrameItem` class with fields: frameId, thumbnail (Image), label
    - Implement constructor with null validation for frameId
    - Implement getters and setters for thumbnail and label
    - _Requirements: 3.3, 3.4_
  
  - [x] 5.3 Write unit tests for data models
    - Test ToolItem creation with valid parameters
    - Test ToolItem null validation for id and icon
    - Test FrameItem creation with valid parameters
    - Test FrameItem null validation for frameId
    - Test FrameItem setters update values correctly
    - _Requirements: 9.4_

- [x] 6. Implement ToolsPanelTemplate
  - [x] 6.1 Create ToolsPanelTemplate base class structure
    - Create `ToolsPanelTemplate` extending `VBox`
    - Add fields: config, toolItems map, selectedToolId
    - Implement constructor accepting `ToolsPanelConfig`
    - Apply tool spacing from config
    - Load and apply CSS stylesheet
    - Apply custom CSS classes from config
    - _Requirements: 2.1, 2.7, 2.9, 6.2, 6.5, 7.2, 7.3_
  
  - [x] 6.2 Implement tool management methods in ToolsPanelTemplate
    - Implement `addTool(String id, Node icon, String label, Consumer<String> onSelect)` method
    - Create styled button with icon and optional label (based on showLabels config)
    - Store ToolItem in toolItems map
    - Apply iconSize from config to icon
    - Implement `addToolGroup(String groupName)` method
    - Implement `addSeparator()` method
    - _Requirements: 2.2, 2.3, 2.7, 2.8_
  
  - [x] 6.3 Implement selection state management in ToolsPanelTemplate
    - Implement `selectTool(String toolId)` method with visual highlighting
    - Update selectedToolId field
    - Deselect previously selected tool (remove highlight)
    - Invoke onSelect callback with proper exception handling
    - Implement `getSelectedTool()` method
    - Implement `clearSelection()` method
    - Support initial selection from config
    - Implement protected `onToolSelected(String previousId, String newId)` hook for subclasses
    - _Requirements: 2.4, 2.5, 2.6, 7.3, 9.3, 10.1, 10.3, 10.4_
  
  - [x] 6.4 Write unit tests for ToolsPanelTemplate
    - Test tool items are added correctly
    - Test tool selection updates selectedToolId
    - Test previously selected tool is deselected
    - Test callback is invoked on selection
    - Test callback exception is caught and logged
    - Test initial selection from config is applied
    - Test tool groups and separators are added
    - Test CSS classes are applied
    - _Requirements: 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 9.3_

- [ ] 7. Implement ScenePanelTemplate
  - [ ] 7.1 Create ScenePanelTemplate base class structure
    - Create `ScenePanelTemplate` extending `VBox`
    - Add fields: config, frameItems map, selectedFrameId, scrollPane for content
    - Implement constructor accepting `ScenePanelConfig`
    - Create scrollable content area with ScrollPane
    - Load and apply CSS stylesheet
    - Apply custom CSS classes from config
    - _Requirements: 3.1, 3.2, 3.8, 6.2, 6.5, 7.2_
  
  - [~] 7.2 Implement frame management methods in ScenePanelTemplate
    - Implement `addFrame(String frameId, Image thumbnail, String label)` method
    - Create frame item UI with thumbnail, optional label (based on showFrameLabels config)
    - Apply thumbnail dimensions from config
    - Store FrameItem in frameItems map
    - Implement `removeFrame(String frameId)` method
    - Implement `updateFrameThumbnail(String frameId, Image thumbnail)` method
    - _Requirements: 3.3, 3.4_
  
  - [~] 7.3 Implement selection and action button support in ScenePanelTemplate
    - Implement `selectFrame(String frameId)` method with visual highlighting
    - Invoke onFrameSelect callback with proper exception handling
    - Support double-click detection and invoke onFrameDoubleClick callback
    - Implement `getSelectedFrame()` method
    - Implement `addActionButton(String label, Node icon, Runnable action)` method
    - Wrap action callback in try-catch with error logging
    - _Requirements: 3.5, 3.6, 3.7, 7.3, 9.3_
  
  - [~] 7.4 Implement layer view mode support in ScenePanelTemplate
    - Implement `setLayerViewMode(boolean enabled)` method
    - Implement `isLayerViewMode()` method
    - Support initial layer mode from config
    - Implement protected hook for subclasses to customize layer view rendering
    - _Requirements: 3.9, 10.1, 10.3_
  
  - [~] 7.5 Write unit tests for ScenePanelTemplate
    - Test frames are added and displayed correctly
    - Test frame removal works correctly
    - Test thumbnail update works correctly
    - Test frame selection updates state and invokes callback
    - Test action buttons invoke callbacks correctly
    - Test callback exceptions are caught and logged
    - Test layer view mode toggle
    - Test CSS classes are applied
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9, 9.3_

- [ ] 8. Implement TimelinePanelTemplate
  - [~] 8.1 Create TimelinePanelTemplate base class structure
    - Create `TimelinePanelTemplate` extending `HBox`
    - Add fields: config, frameMarkers map, selectedFrameNumber, playbackButton, addFrameButton, scrollPane
    - Implement constructor accepting `TimelinePanelConfig`
    - Create scrollable horizontal content area with ScrollPane
    - Load and apply CSS stylesheet
    - Apply custom CSS classes from config
    - _Requirements: 4.1, 4.8, 4.9, 6.2, 6.5, 7.2_
  
  - [~] 8.2 Implement frame marker management in TimelinePanelTemplate
    - Implement `addFrameMarker(int frameNumber)` method
    - Create frame marker UI with frame number label
    - Apply frameMarkerWidth and frameMarkerHeight from config
    - Store marker in frameMarkers map
    - Implement `removeFrameMarker(int frameNumber)` method
    - Implement `clearFrameMarkers()` method
    - _Requirements: 4.2_
  
  - [~] 8.3 Implement playback controls in TimelinePanelTemplate
    - Create playback button if showPlaybackButton config is true
    - Implement `setPlaybackState(boolean playing)` method to toggle play/pause state
    - Update button icon/text based on playback state
    - Invoke onPlaybackToggle callback when button is clicked
    - Wrap callback in try-catch with error logging
    - Implement `isPlaying()` method
    - _Requirements: 4.3, 4.4, 7.3, 9.3_
  
  - [~] 8.4 Implement frame selection and add frame button in TimelinePanelTemplate
    - Implement `selectFrame(int frameNumber)` method with visual highlighting
    - Invoke onFrameSelect callback with proper exception handling
    - Implement `getSelectedFrame()` method
    - Create add frame button if showAddFrameButton config is true
    - Implement `setOnAddFrame(Runnable action)` method
    - Wrap action callback in try-catch with error logging
    - _Requirements: 4.5, 4.6, 4.7, 7.3, 9.3_
  
  - [~] 8.5 Write unit tests for TimelinePanelTemplate
    - Test frame markers are added and displayed correctly
    - Test frame marker removal works correctly
    - Test clearFrameMarkers works correctly
    - Test playback state toggle updates UI and invokes callback
    - Test frame selection updates state and invokes callback
    - Test add frame button invokes callback
    - Test callback exceptions are caught and logged
    - Test CSS classes are applied
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8, 4.9, 9.3_

- [ ] 9. Implement StatusPanelTemplate
  - [~] 9.1 Create StatusPanelTemplate class structure
    - Create `StatusPanelTemplate` extending `HBox`
    - Add fields: config, zoomLabel, fpsLabel, frameInfoLabel, currentZoom, currentFPS, currentFrame, totalFrames
    - Implement constructor accepting `StatusPanelConfig`
    - Create label containers with consistent spacing
    - Apply initial values from config (initialZoom, initialFPS, initialCurrentFrame, initialTotalFrames)
    - Show/hide labels based on config flags (showZoom, showFPS, showFrameInfo)
    - Load and apply CSS stylesheet
    - Apply custom CSS classes from config
    - _Requirements: 5.1, 5.2, 5.3, 5.7, 5.8, 5.9, 6.2, 6.5, 7.2_
  
  - [~] 9.2 Implement value update methods in StatusPanelTemplate
    - Implement `setZoom(double zoomPercent)` method that updates zoomLabel text
    - Implement `setFPS(int fps)` method that updates fpsLabel text
    - Implement `setFrameInfo(int currentFrame, int totalFrames)` method that updates frameInfoLabel text
    - Implement getters: `getZoom()`, `getFPS()`, `getCurrentFrame()`, `getTotalFrames()`
    - Format zoom as percentage (e.g., "100%")
    - Format FPS as integer (e.g., "24 FPS")
    - Format frame info as "currentFrame / totalFrames" (e.g., "1 / 10")
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6_
  
  - [~] 9.3 Write unit tests for StatusPanelTemplate
    - Test initial values are displayed correctly
    - Test setZoom updates label text with correct format
    - Test setFPS updates label text with correct format
    - Test setFrameInfo updates label text with correct format
    - Test getters return correct values
    - Test labels are shown/hidden based on config flags
    - Test CSS classes are applied
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 5.8, 5.9_

- [~] 10. Checkpoint - Verify all template components
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 11. Add JavaDoc documentation
  - [~] 11.1 Document ModalTemplate and ModalConfig
    - Add class-level JavaDoc describing purpose and usage
    - Document all public methods with @param, @return, @throws tags
    - Include usage examples in class JavaDoc
    - Document configuration parameters with expected types and defaults
    - Document callback signatures and expected behavior
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 8.6_
  
  - [~] 11.2 Document panel templates and configuration classes
    - Add class-level JavaDoc for ToolsPanelTemplate, ScenePanelTemplate, TimelinePanelTemplate, StatusPanelTemplate
    - Document all public methods with @param, @return, @throws tags
    - Include usage examples in class JavaDoc
    - Document configuration parameters for all panel config classes
    - Document callback signatures and expected behavior
    - Document protected methods intended for extension
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 8.6, 10.7_
  
  - [~] 11.3 Add inline comments for complex logic
    - Add comments explaining Stage initialization in ModalTemplate
    - Add comments explaining CSS loading mechanism
    - Add comments explaining selection state management in panel templates
    - Add comments explaining error handling and callback exception wrapping
    - _Requirements: 8.7, 9.1, 9.2, 9.3_

- [ ] 12. Final integration and cleanup
  - [~] 12.1 Verify error handling consistency
    - Review all error messages follow format: "[ComponentName] operation: descriptive message (context)"
    - Verify FXML loading failures throw RuntimeException with path
    - Verify CSS loading failures log warning and continue
    - Verify callback exceptions are caught and logged
    - Verify configuration validation throws IllegalArgumentException with descriptive messages
    - Verify resource cleanup on initialization failure
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7_
  
  - [~] 12.2 Verify integration with existing patterns
    - Verify CSS loading follows RichTextModal pattern
    - Verify Stage initialization matches existing modality patterns
    - Verify resource paths follow `/com/example/scenory/` convention
    - Verify Consumer callback usage is consistent with existing code
    - Verify FXML loading pattern matches existing components
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7_
  
  - [~] 12.3 Verify extensibility support
    - Verify protected methods are available for subclass customization
    - Verify layout structure is separated from content population
    - Verify hooks exist for subclass overrides (e.g., onToolSelected)
    - Verify protected getters expose necessary internal components
    - Verify documentation identifies methods intended for extension
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6, 10.7_

- [~] 13. Final checkpoint - Complete implementation verification
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional test tasks and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation and user feedback opportunities
- Unit tests validate specific examples and edge cases
- Property-based testing is not applicable to JavaFX UI components (see design document)
- All template components follow existing JavaFX patterns in the Scenory application
- Configuration objects use builder pattern for flexible customization
- Consumer callbacks provide extensibility without subclassing
- Error handling follows fail-safe approach with clear diagnostic messages

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["1.2", "1.3"] },
    { "id": 2, "tasks": ["1.4", "2.1"] },
    { "id": 3, "tasks": ["2.2"] },
    { "id": 4, "tasks": ["2.3", "2.4"] },
    { "id": 5, "tasks": ["4.1", "4.2", "4.3", "4.4"] },
    { "id": 6, "tasks": ["4.5", "5.1", "5.2"] },
    { "id": 7, "tasks": ["5.3", "6.1"] },
    { "id": 8, "tasks": ["6.2"] },
    { "id": 9, "tasks": ["6.3", "6.4"] },
    { "id": 10, "tasks": ["7.1"] },
    { "id": 11, "tasks": ["7.2"] },
    { "id": 12, "tasks": ["7.3"] },
    { "id": 13, "tasks": ["7.4", "7.5"] },
    { "id": 14, "tasks": ["8.1"] },
    { "id": 15, "tasks": ["8.2"] },
    { "id": 16, "tasks": ["8.3", "8.4"] },
    { "id": 17, "tasks": ["8.5", "9.1"] },
    { "id": 18, "tasks": ["9.2", "9.3"] },
    { "id": 19, "tasks": ["11.1", "11.2", "11.3"] },
    { "id": 20, "tasks": ["12.1", "12.2", "12.3"] }
  ]
}
```
