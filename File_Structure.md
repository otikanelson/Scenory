# Scenory Project File Structure Reference
## Complete Directory and Class Organization

---

## **📁 PROJECT ROOT STRUCTURE**

```
Scenory-main/
├── .idea/                          # IntelliJ IDEA configuration
│   ├── .gitignore
│   ├── compiler.xml
│   ├── encodings.xml
│   ├── jarRepositories.xml
│   ├── misc.xml
│   ├── uiDesigner.xml
│   ├── vcs.xml
│   └── workspace.xml
├── .mvn/                           # Maven wrapper
├── fonts/                          # Font assets
├── src/                            # Source code
└── target/                         # Build output
```

---

## **📂 SOURCE CODE STRUCTURE**

### **src/main/java/com.example.scenory/**

#### **🎯 Core Application**
```
com.example.scenory/
├── ScenoryApplication.java         # Main application entry point
└── module-info.java               # Java module configuration
```

#### **📋 Commands Package** (`commands/`)
```
commands/
├── CanvasStateCommand.java        # Canvas state management command
├── ClearCanvasCommand.java        # Clear canvas operation
├── CommandManager.java            # Command pattern manager (undo/redo)
├── DrawingCommand.java            # Base drawing command interface
├── ShapeCommand.java              # Shape drawing commands
└── StrokeCommand.java             # Stroke/brush commands
```

#### **🎮 Controller Package** (`controller/`)
```
controller/
├── MainController.java            # Primary application controller
├── ModalController.java           # Modal dialog controller
├── NavigationManager.java         # Navigation between views
├── PanelNavigator.java            # Panel switching logic
├── PanelSystemManager.java        # Panel layout management
├── ProjectManager.java            # Project lifecycle management
├── ToolManager.java               # Drawing tool management
├── UIManager.java                 # UI state management
└── WelcomeController.java         # Welcome screen controller
```

#### **🗄️ Database Package** (`database/`)
```
database/
├── DatabaseManager.java           # Database connection management
├── PanelDAO.java                  # Panel data access object
├── PanelLayoutDAO.java            # UI layout persistence
├── ProjectDAO.java                # Project data access object
└── SceneDAO.java                  # Scene data access object
```

#### **🔧 Enums Package** (`enums/`)
```
enums/
├── DrawingTool.java               # Drawing tool enumeration
└── StrokeType.java                # Stroke type definitions
```

#### **⌨️ Input Package** (`input/`)
```
input/
└── KeyboardShortcutManager.java   # Keyboard shortcut handling
```

#### **👥 Managers Package** (`managers/`)
*Note: This appears to be a placeholder directory based on the visible structure*

#### **🏗️ Model Package** (`model/`)
```
model/
├── DrawingElement.java            # Individual drawing element
├── Panel.java                     # Storyboard panel model
├── Project.java                   # Project model
└── Scene.java                     # Scene model
```

#### **💾 Persistence Package** (`persistence/`)
```
persistence/
├── PanelLayoutPersistence.java    # Panel layout persistence
└── UserPreferences.java           # User preference management
```

#### **🛠️ Utils Package** (`utils/`)
```
utils/
├── CanvasPersistence.java         # Canvas data persistence utilities
├── DragAndDropHandler.java        # Drag and drop functionality
└── ThumbnailGenerator.java        # Thumbnail generation utilities
```

#### **🖼️ View Package** (`view/`)
```
view/
├── components/
│   ├── DrawingCanvas.java         # Main drawing canvas component
│   └── ToolPanel.java             # Tool selection panel
├── dialogs/
│   ├── PanelPropertiesDialog.java # Panel properties dialog
│   ├── RichTextModal.java         # Rich text editing modal
│   ├── RichTextModalController.java # Rich text modal controller
│   └── TimingControlDialog.java   # Panel timing dialog
└── panels/
    ├── CollapsiblePanel.java      # Generic collapsible panel
    ├── EnhancedDualPanelGroup.java # Dual panel system
    ├── ResizablePanelSystem.java  # Resizable panel framework
    ├── SceneConstructor.java      # Scene constructor panel
    ├── SmartTabbedPanelGroup.java # Tabbed panel group
    └── ToolSelectionPanel.java    # Tool selection panel
```

---

## **📂 RESOURCES STRUCTURE**

### **src/main/resources/com.example.scenory/**

#### **🎨 CSS Stylesheets** (`css/`)
```
css/
├── base/
│   ├── fonts.css                  # Font definitions
│   ├── global.css                 # Global styles
│   └── variables.css              # CSS variables
├── components/
│   ├── buttons.css                # Button styling
│   ├── canvas.css                 # Canvas component styles
│   ├── dialogs.css                # Dialog/modal styles
│   ├── menus.css                  # Menu styling
│   ├── panels.css                 # Panel component styles
│   ├── thumbnails.css             # Thumbnail styling
│   └── tools.css                  # Tool panel styles
├── layout/
│   ├── main-interface.css         # Main interface layout
│   └── welcome.css                # Welcome screen layout
└── themes/
    └── glassmorphism.css          # Glassmorphism theme
```

#### **🖼️ Images** (`images/`)
```
images/
├── img.png                        # Generic image asset
└── Scenory_bg.png                # Background image
```

#### **📱 View Dialogs** (`view.dialogs/`)
```
view.dialogs/
├── RichTextModal.fxml             # Rich text modal layout
├── about-modal.fxml               # About dialog layout
├── main-view.fxml                 # Main application view
├── modal.fxml                     # Generic modal layout
├── settings-modal.fxml            # Settings dialog layout
└── welcome-view.fxml              # Welcome screen layout
```

#### **⚙️ Configuration Files**
```
resources/
├── database.properties            # Database configuration
├── database-local.properties      # Local database config
├── font-size.fxml                 # Font size configuration
├── icon.png                       # Application icon
└── styles.css                     # Main stylesheet
```

---

## **📋 BUILD & CONFIGURATION FILES**

### **Maven Configuration**
```
pom.xml                            # Maven project configuration
mvnw                               # Maven wrapper script
mvnw.cmd                           # Maven wrapper (Windows)
```

### **Documentation**
```
DB_setup Guide.md                  # Database setup instructions
ProjectDoc.md                      # Project documentation
```

### **Git Configuration**
```
.gitignore                         # Git ignore rules
target/                            # Maven build output (ignored)
```

---

## **🎯 KEY ARCHITECTURAL COMPONENTS**

### **Core Application Flow**
```
ScenoryApplication.java
    ↓
WelcomeController.java (Project Creation)
    ↓
MainController.java (Main Interface)
    ↓
ProjectManager.java + PanelNavigator.java (Project Management)
    ↓
DrawingCanvas.java + ToolManager.java (Drawing Interface)
```

### **Command System Architecture**
```
CommandManager.java
    ├── DrawingCommand.java (Interface)
    ├── StrokeCommand.java (Drawing operations)
    ├── ShapeCommand.java (Shape operations)
    ├── CanvasStateCommand.java (State management)
    └── ClearCanvasCommand.java (Clear operations)
```

### **Database Layer Architecture**
```
DatabaseManager.java (Connection Management)
    ├── ProjectDAO.java (Project persistence)
    ├── SceneDAO.java (Scene persistence)
    ├── PanelDAO.java (Panel persistence)
    └── PanelLayoutDAO.java (UI layout persistence)
```

### **UI Panel System Architecture**
```
PanelSystemManager.java (Panel coordination)
    ├── EnhancedDualPanelGroup.java (Left panels)
    ├── SceneConstructor.java (Right panel)
    ├── ResizablePanelSystem.java (Resizing framework)
    └── ToolSelectionPanel.java (Tool selection)
```

---

## **📊 STATISTICS**

### **File Count Summary**
- **Java Classes**: ~45 files
- **FXML Files**: 7 files
- **CSS Files**: 15+ files
- **Configuration Files**: 5 files
- **Total Source Files**: ~70+ files

### **Package Distribution**
- **Controller Package**: 8 classes (Application control logic)
- **Model Package**: 4 classes (Data models)
- **View Package**: 12 classes (UI components)
- **Database Package**: 5 classes (Data persistence)
- **Commands Package**: 6 classes (Command pattern implementation)
- **Utils Package**: 3 classes (Utility functions)

### **Architecture Patterns Used**
- **MVC (Model-View-Controller)**: Primary architectural pattern
- **Command Pattern**: For undo/redo functionality
- **DAO Pattern**: For database operations
- **Observer Pattern**: For UI updates and event handling
- **Singleton Pattern**: For managers and utilities

---

## **🔧 DEVELOPMENT NOTES**

### **Key Entry Points**
- **Application Start**: `ScenoryApplication.java`
- **Main Interface**: `MainController.java`
- **Drawing System**: `DrawingCanvas.java`
- **Project Management**: `ProjectManager.java`

### **Configuration Files**
- **Database**: `database.properties`, `database-local.properties`
- **Styling**: `styles.css` (main), various CSS files in `/css/`
- **Maven**: `pom.xml`

### **Asset Directories**
- **Images**: `src/main/resources/com/example/scenory/images/`
- **Fonts**: `fonts/` (root level)
- **Icons**: `icon.png` in resources

This file structure shows a well-organized JavaFX application with clear separation of concerns and professional architecture patterns.