# Design Document: UI Template Components

## Overview

This document describes the technical design for reusable UI template components in the Scenory JavaFX animation application. The design provides standardized templates for modals and interface panels (Tools Panel, Scene Panel, Timeline Panel, Frame Count Status Panel) that follow existing JavaFX patterns while offering flexible, configurable APIs.

### Design Goals

1. **Consistency**: Provide uniform styling and behavior across all modal dialogs and panels
2. **Reusability**: Enable developers to create new UI components quickly without duplicating code
3. **Flexibility**: Support customization through configuration objects and callback mechanisms
4. **Integration**: Seamlessly integrate with existing patterns (FXML loading, Stage management, CSS styling)
5. **Extensibility**: Allow subclassing and composition for specialized use cases

### Architectural Principles

- **Separation of Concerns**: Separate layout structure from content population
- **Composition over Inheritance**: Use builder/configuration patterns where appropriate
- **Fail-Safe Defaults**: Provide sensible default values for all configuration parameters
- **Defensive Programming**: Validate inputs and handle errors gracefully with clear diagnostics

## Architecture

### Component Hierarchy

```
com.example.scenory.view.templates
├── ModalTemplate              # Base modal dialog template
├── config/
│   ├── ModalConfig           # Configuration for modal dialogs
│   ├── PanelConfig           # Base configuration for panels
│   ├── ToolsPanelConfig      # Configuration for tools panel
│   ├── ScenePanelConfig      # Configuration for scene panel
│   ├── TimelinePanelConfig   # Configuration for timeline panel
│   └── StatusPanelConfig     # Configuration for status panel
└── panels/
    ├── ToolsPanelTemplate    # Template for tool panels
    ├── ScenePanelTemplate    # Template for scene/frame panels
    ├── TimelinePanelTemplate # Template for timeline panels
    └── StatusPanelTemplate   # Template for status bars
```

### Integration with Existing Architecture

The template components integrate with existing Scenory patterns:

1. **FXML Pattern**: Follow the `RichTextModal` pattern for loading FXML resources
2. **CSS Styling**: Use the existing `styles.css` resource loading mechanism
3. **Stage Management**: Use standard JavaFX Stage initialization (modality, owner, centering)
4. **Resource Loading**: Follow the `/com/example/scenory/` resource path convention
5. **Callback Pattern**: Use `Consumer<T>` functional interfaces for event callbacks

### Design Patterns Applied

1. **Builder Pattern**: Configuration objects act as builders for template components
2. **Template Method Pattern**: Base templates define structure; subclasses customize behavior
3. **Strategy Pattern**: Consumer callbacks allow customizable behavior without subclassing
4. **Factory Pattern**: Static factory methods for common template configurations

## Components and Interfaces

### 1. ModalTemplate

**Purpose**: Create modal dialogs with consistent styling and behavior

**Key Responsibilities**:
- Initialize Stage with APPLICATION_MODAL modality
- Load and apply CSS stylesheet
- Support both FXML-based and programmatic content
- Handle minimum dimensions and window centering
- Provide close callback support

**Public API**:

```java
public class ModalTemplate {
    // Factory methods
    public static ModalTemplate create(ModalConfig config);
    public static ModalTemplate createWithFXML(String fxmlPath, ModalConfig config);
    public static ModalTemplate createWithContent(Node content, ModalConfig config);
    
    // Display methods
    public void show();
    public void showAndWait();
    public void close();
    
    // Configuration
    public Stage getStage();
    public Scene getScene();
    public <T> T getController();
}
```

### 2. ModalConfig

**Purpose**: Configuration object for modal dialog customization

**Properties**:
- `title`: String (default: "Dialog")
- `width`: double (default: 600)
- `height`: double (default: 400)
- `minWidth`: Double (optional)
- `minHeight`: Double (optional)
- `resizable`: boolean (default: true)
- `closeable`: boolean (default: true)
- `ownerWindow`: Window (optional)
- `styleClasses`: List<String> (additional CSS classes)
- `onClose`: Consumer<Void> (close callback)

**Builder API**:

```java
public class ModalConfig {
    public static Builder builder();
    
    public static class Builder {
        public Builder title(String title);
        public Builder dimensions(double width, double height);
        public Builder minDimensions(double minWidth, double minHeight);
        public Builder resizable(boolean resizable);
        public Builder closeable(boolean closeable);
        public Builder owner(Window owner);
        public Builder styleClasses(String... classes);
        public Builder onClose(Consumer<Void> callback);
        public ModalConfig build();
    }
}
```

### 3. ToolsPanelTemplate

**Purpose**: Vertical panel for tool buttons with selection state management

**Key Responsibilities**:
- Provide vertical layout container for tool buttons
- Manage single-selection state (radio-button behavior)
- Support tool groups with visual separators
- Handle tool item clicks and selection callbacks

**Public API**:

```java
public class ToolsPanelTemplate extends VBox {
    public ToolsPanelTemplate(ToolsPanelConfig config);
    
    // Add tool items
    public void addTool(String id, Node icon, String label, Consumer<String> onSelect);
    public void addToolGroup(String groupName);
    public void addSeparator();
    
    // Selection management
    public void selectTool(String toolId);
    public String getSelectedTool();
    public void clearSelection();
    
    // Lifecycle
    protected void onToolSelected(String previousId, String newId);
}
```

### 4. ToolsPanelConfig

**Purpose**: Configuration for tools panel template

**Properties**:
- `toolSpacing`: double (spacing between tools, default: 8)
- `showLabels`: boolean (show/hide labels, default: true)
- `iconSize`: double (icon size in pixels, default: 24)
- `selectionMode`: SelectionMode enum (SINGLE, MULTIPLE, NONE)
- `styleClasses`: List<String>
- `initialSelection`: String (tool ID to select initially)

### 5. ScenePanelTemplate

**Purpose**: Panel for displaying frame thumbnails and layer information

**Key Responsibilities**:
- Provide scrollable container for frame items
- Display frame thumbnails with labels
- Support frame selection with visual highlighting
- Provide action buttons (add, delete, duplicate)
- Support optional layer view mode

**Public API**:

```java
public class ScenePanelTemplate extends VBox {
    public ScenePanelTemplate(ScenePanelConfig config);
    
    // Frame management
    public void addFrame(String frameId, Image thumbnail, String label);
    public void removeFrame(String frameId);
    public void updateFrameThumbnail(String frameId, Image thumbnail);
    
    // Selection
    public void selectFrame(String frameId);
    public String getSelectedFrame();
    
    // Actions
    public void addActionButton(String label, Node icon, Runnable action);
    
    // Layer mode
    public void setLayerViewMode(boolean enabled);
    public boolean isLayerViewMode();
}
```

### 6. ScenePanelConfig

**Purpose**: Configuration for scene panel template

**Properties**:
- `thumbnailWidth`: double (default: 120)
- `thumbnailHeight`: double (default: 90)
- `showFrameLabels`: boolean (default: true)
- `enableLayerMode`: boolean (default: false)
- `onFrameSelect`: Consumer<String> (frame selection callback)
- `onFrameDoubleClick`: Consumer<String> (double-click callback)
- `styleClasses`: List<String>

### 7. TimelinePanelTemplate

**Purpose**: Horizontal timeline panel for frame navigation and playback

**Key Responsibilities**:
- Display horizontal frame markers with labels
- Provide playback button with toggle state
- Support frame selection by clicking markers
- Provide scrollable view for long timelines
- Handle add frame button clicks

**Public API**:

```java
public class TimelinePanelTemplate extends HBox {
    public TimelinePanelTemplate(TimelinePanelConfig config);
    
    // Frame markers
    public void addFrameMarker(int frameNumber);
    public void removeFrameMarker(int frameNumber);
    public void clearFrameMarkers();
    
    // Selection
    public void selectFrame(int frameNumber);
    public int getSelectedFrame();
    
    // Playback
    public void setPlaybackState(boolean playing);
    public boolean isPlaying();
    
    // Add frame button
    public void setOnAddFrame(Runnable action);
}
```

### 8. TimelinePanelConfig

**Purpose**: Configuration for timeline panel template

**Properties**:
- `frameMarkerWidth`: double (default: 60)
- `frameMarkerHeight`: double (default: 40)
- `showPlaybackButton`: boolean (default: true)
- `showAddFrameButton`: boolean (default: true)
- `onFrameSelect`: Consumer<Integer> (frame selection callback)
- `onPlaybackToggle`: Consumer<Boolean> (playback state callback)
- `styleClasses`: List<String>

### 9. StatusPanelTemplate

**Purpose**: Status bar displaying zoom, FPS, and frame count information

**Key Responsibilities**:
- Display zoom level as percentage
- Display FPS as integer value
- Display current frame / total frames
- Update values dynamically
- Arrange information horizontally with consistent spacing

**Public API**:

```java
public class StatusPanelTemplate extends HBox {
    public StatusPanelTemplate(StatusPanelConfig config);
    
    // Update methods
    public void setZoom(double zoomPercent);
    public void setFPS(int fps);
    public void setFrameInfo(int currentFrame, int totalFrames);
    
    // Getters
    public double getZoom();
    public int getFPS();
    public int getCurrentFrame();
    public int getTotalFrames();
}
```

### 10. StatusPanelConfig

**Purpose**: Configuration for status panel template

**Properties**:
- `initialZoom`: double (default: 100.0)
- `initialFPS`: int (default: 24)
- `initialCurrentFrame`: int (default: 1)
- `initialTotalFrames`: int (default: 1)
- `showZoom`: boolean (default: true)
- `showFPS`: boolean (default: true)
- `showFrameInfo`: boolean (default: true)
- `styleClasses`: List<String>

## Data Models

### ModalConfig Data Model

```java
public class ModalConfig {
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
    
    // Private constructor - use Builder
    private ModalConfig(Builder builder) { /* ... */ }
    
    // Getters with defensive copies where applicable
    public String getTitle() { return title; }
    public double getWidth() { return width; }
    // ... other getters
    
    // Validation
    private void validate() {
        if (width <= 0) throw new IllegalArgumentException("Width must be positive");
        if (height <= 0) throw new IllegalArgumentException("Height must be positive");
        if (minWidth != null && minWidth > width) {
            throw new IllegalArgumentException("Min width cannot exceed width");
        }
        if (minHeight != null && minHeight > height) {
            throw new IllegalArgumentException("Min height cannot exceed height");
        }
    }
}
```

### PanelConfig Base Model

```java
public abstract class PanelConfig {
    protected final List<String> styleClasses;
    
    protected PanelConfig(List<String> styleClasses) {
        this.styleClasses = new ArrayList<>(styleClasses);
    }
    
    public List<String> getStyleClasses() {
        return new ArrayList<>(styleClasses);
    }
    
    // Template method for subclass validation
    protected abstract void validate();
}
```

### ToolItem Data Model

```java
public class ToolItem {
    private final String id;
    private final Node icon;
    private final String label;
    private final Consumer<String> onSelect;
    
    public ToolItem(String id, Node icon, String label, Consumer<String> onSelect) {
        this.id = requireNonNull(id, "Tool ID cannot be null");
        this.icon = requireNonNull(icon, "Icon cannot be null");
        this.label = label;
        this.onSelect = onSelect;
    }
    
    // Getters
    public String getId() { return id; }
    public Node getIcon() { return icon; }
    public String getLabel() { return label; }
    public Consumer<String> getOnSelect() { return onSelect; }
}
```

### FrameItem Data Model

```java
public class FrameItem {
    private final String frameId;
    private Image thumbnail;
    private String label;
    
    public FrameItem(String frameId, Image thumbnail, String label) {
        this.frameId = requireNonNull(frameId, "Frame ID cannot be null");
        this.thumbnail = thumbnail;
        this.label = label;
    }
    
    // Getters and setters
    public String getFrameId() { return frameId; }
    public Image getThumbnail() { return thumbnail; }
    public void setThumbnail(Image thumbnail) { this.thumbnail = thumbnail; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
```

## Error Handling

### Error Handling Strategy

The template components follow a fail-safe approach with clear error reporting:

1. **FXML Loading Failures**: Wrap IOException in RuntimeException with descriptive message
2. **CSS Loading Failures**: Log warning and continue without custom styling
3. **Callback Exceptions**: Catch and log exceptions from user-provided callbacks
4. **Configuration Validation**: Throw IllegalArgumentException for invalid configuration
5. **Resource Not Found**: Throw descriptive exception identifying the missing resource
6. **Partial Initialization Failures**: Clean up any created resources before rethrowing

### Error Message Format

All error messages follow this format:
```
[ComponentName] operation: descriptive message (context)
```

Example:
```
[ModalTemplate] FXML loading: Could not load /com/example/scenory/modal.fxml (file not found)
```

### Exception Handling Examples

#### FXML Loading Error

```java
public class ModalTemplate {
    private Parent loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(
                "[ModalTemplate] FXML loading: Could not load " + fxmlPath, e
            );
        }
    }
}
```

#### CSS Loading Error (non-fatal)

```java
private void loadCSS(Scene scene) {
    try {
        String cssFile = getClass()
            .getResource("/com/example/scenory/styles.css")
            .toExternalForm();
        scene.getStylesheets().add(cssFile);
        System.out.println("✅ [ModalTemplate] CSS loaded successfully");
    } catch (Exception e) {
        System.out.println("⚠️ [ModalTemplate] CSS loading failed: " + e.getMessage());
        System.out.println("   Continuing with default styling");
    }
}
```

#### Callback Error Handling

```java
private void invokeCallback(Consumer<String> callback, String value) {
    if (callback != null) {
        try {
            callback.accept(value);
        } catch (Exception e) {
            System.err.println("❌ [ToolsPanelTemplate] Callback error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

#### Configuration Validation

```java
public class ModalConfig {
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
        // ... other validations
    }
}
```

## Testing Strategy

### Testing Approach

The UI template components require a combination of unit tests and property-based tests. However, due to the nature of JavaFX UI components, property-based testing has limited applicability.

**Property-Based Testing (PBT) Applicability Assessment**:

PBT is **NOT appropriate** for these template components because:

1. **UI Rendering**: The primary functionality is UI rendering and layout, which cannot be effectively tested with PBT
2. **Configuration Validation**: While configuration objects can be tested with PBT, the behavior is simple validation logic better suited to example-based tests
3. **Callback Invocation**: Testing that callbacks are invoked correctly is scenario-specific, not universal across inputs
4. **JavaFX Platform Dependency**: UI components require JavaFX toolkit initialization, making property generation and execution complex

**Testing Strategy**:

1. **Unit Tests**: Verify specific examples, edge cases, and error conditions
   - Configuration object creation and validation
   - Error handling for invalid inputs
   - Callback invocation verification
   - CSS and FXML loading behavior
   - Selection state management

2. **Integration Tests**: Verify component interaction and lifecycle
   - Modal display and closing
   - Panel population with content
   - User interaction simulation (clicks, selections)
   - JavaFX platform integration

3. **Manual Testing**: Verify visual consistency and user experience
   - Styling and layout appearance
   - Animation smoothness
   - Responsive behavior
   - Cross-platform consistency

### Unit Test Coverage

Unit tests will cover:

1. **Configuration Validation**:
   - Valid configuration objects are accepted
   - Invalid dimensions throw IllegalArgumentException
   - Default values are applied correctly
   - Builder pattern produces correct configuration

2. **Error Handling**:
   - FXML loading errors throw RuntimeException
   - CSS loading errors log warning but don't fail
   - Callback exceptions are caught and logged
   - Resource not found errors include resource path

3. **Component Behavior**:
   - Tool selection updates state correctly
   - Frame selection highlights correct frame
   - Status panel updates display values
   - Timeline playback toggles state

4. **Integration with Existing Patterns**:
   - CSS stylesheet loading follows RichTextModal pattern
   - Stage initialization matches existing modality patterns
   - Resource paths follow convention

### Test Fixtures

Common test fixtures:

```java
// Test configuration builders
public class TestConfigs {
    public static ModalConfig defaultModal() {
        return ModalConfig.builder()
            .title("Test Modal")
            .dimensions(800, 600)
            .build();
    }
    
    public static ToolsPanelConfig defaultToolsPanel() {
        return ToolsPanelConfig.builder()
            .toolSpacing(10)
            .showLabels(true)
            .build();
    }
}

// Mock callback tracker
public class CallbackTracker<T> implements Consumer<T> {
    private final List<T> values = new ArrayList<>();
    
    @Override
    public void accept(T value) {
        values.add(value);
    }
    
    public List<T> getValues() { return new ArrayList<>(values); }
    public int getCallCount() { return values.size(); }
    public T getLastValue() { return values.isEmpty() ? null : values.get(values.size() - 1); }
}
```

### Test Examples

#### Configuration Validation Test

```java
@Test
void testModalConfig_invalidDimensions_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> {
        ModalConfig.builder()
            .title("Test")
            .dimensions(-100, 600)
            .build();
    });
}

@Test
void testModalConfig_defaultValues_applied() {
    ModalConfig config = ModalConfig.builder().build();
    
    assertEquals("Dialog", config.getTitle());
    assertEquals(600, config.getWidth());
    assertEquals(400, config.getHeight());
    assertTrue(config.isResizable());
}
```

#### Tool Selection Test

```java
@Test
void testToolsPanel_selectTool_updatesSelection() {
    ToolsPanelConfig config = ToolsPanelConfig.builder().build();
    ToolsPanelTemplate panel = new ToolsPanelTemplate(config);
    
    panel.addTool("pencil", createIcon(), "Pencil", null);
    panel.addTool("eraser", createIcon(), "Eraser", null);
    
    panel.selectTool("pencil");
    assertEquals("pencil", panel.getSelectedTool());
    
    panel.selectTool("eraser");
    assertEquals("eraser", panel.getSelectedTool());
}
```

#### Callback Invocation Test

```java
@Test
void testToolsPanel_toolClick_invokesCallback() {
    CallbackTracker<String> tracker = new CallbackTracker<>();
    ToolsPanelConfig config = ToolsPanelConfig.builder().build();
    ToolsPanelTemplate panel = new ToolsPanelTemplate(config);
    
    panel.addTool("pencil", createIcon(), "Pencil", tracker);
    
    // Simulate click (implementation-specific)
    simulateToolClick(panel, "pencil");
    
    assertEquals(1, tracker.getCallCount());
    assertEquals("pencil", tracker.getLastValue());
}
```

#### Error Handling Test

```java
@Test
void testModalTemplate_invalidFXMLPath_throwsRuntimeException() {
    ModalConfig config = ModalConfig.builder().build();
    
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        ModalTemplate.createWithFXML("/nonexistent/path.fxml", config);
    });
    
    assertTrue(exception.getMessage().contains("FXML loading"));
    assertTrue(exception.getMessage().contains("/nonexistent/path.fxml"));
}
```

### Testing Libraries

- **JUnit 5**: Primary testing framework
- **TestFX**: JavaFX-specific testing utilities
- **Mockito**: Mock objects for dependencies (if needed)
- **AssertJ**: Fluent assertions for readability

### Continuous Integration

Tests should run on CI with headless JavaFX support:

```bash
# Run tests with headless JavaFX
mvn test -Dtestfx.robot=glass -Dtestfx.headless=true -Dprism.order=sw
```

---

**Note**: Since property-based testing is not applicable to these UI components, the Correctness Properties section has been omitted from this design document. Testing will rely on comprehensive unit tests and integration tests as described above.
