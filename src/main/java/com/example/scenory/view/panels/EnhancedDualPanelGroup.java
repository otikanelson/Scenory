package com.example.scenory.view.panels;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Enhanced Dual Panel System - Claude.ai Style
 * Allows both tools and structure panels to be open simultaneously
 * Features labels beside icons when expanded, similar to Claude's sidebar
 */
public class EnhancedDualPanelGroup extends VBox {

    // Panel system properties
    private VBox iconContainer;
    private VBox contentArea;
    private Button toolsTab, structureTab;
    private Node toolsContent, structureContent;

    // Panel states - both can be open simultaneously
    private BooleanProperty toolsExpanded = new SimpleBooleanProperty(false);
    private BooleanProperty structureExpanded = new SimpleBooleanProperty(false);
    private BooleanProperty isExpanded = new SimpleBooleanProperty(false);

    // Layout properties
    private double collapsedWidth = 50;    // Just icons
    private double singlePanelWidth = 250; // One panel open
    private double dualPanelWidth = 400;   // Both panels open
    private Transition currentTransition;

    public EnhancedDualPanelGroup() {
        initializeComponent();
        createIconTabs();
        createContentArea();
        setupTabBehavior();
        applyInitialState();

        System.out.println("🔧 EnhancedDualPanelGroup created with Claude-style layout");
    }

    private void initializeComponent() {
        this.getStyleClass().add("enhanced-dual-panel-group");
        this.setSpacing(0);
        this.setAlignment(Pos.TOP_LEFT);
        this.setFillWidth(false);
    }

    private void createIconTabs() {
        iconContainer = new VBox(6); // Slightly more spacing
        iconContainer.setAlignment(Pos.TOP_LEFT);
        iconContainer.setPadding(new Insets(12, 6, 12, 6));
        iconContainer.getStyleClass().add("dual-panel-icon-container");

        // Tools tab with icon and optional label
        toolsTab = createTabButton("🛠️", "Drawing Tools", "Tools");
        toolsTab.setOnAction(e -> handleTabClick(toolsTab, "Tools"));

        // Structure tab with icon and optional label
        structureTab = createTabButton("📁", "Project Structure", "Structure");
        structureTab.setOnAction(e -> handleTabClick(structureTab, "Structure"));

        iconContainer.getChildren().addAll(toolsTab, structureTab);
        this.getChildren().add(iconContainer);
    }

    private Button createTabButton(String icon, String fullText, String shortText) {
        Button button = new Button();
        button.getStyleClass().add("dual-panel-tab");
        button.setPrefHeight(40);
        button.setMinHeight(40);
        button.setMaxHeight(40);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(8, 12, 8, 12));

        // Store text options in user data
        button.setUserData(new String[]{icon, fullText, shortText});

        // Initially show only icon
        updateButtonText(button, false);

        Tooltip.install(button, new Tooltip(fullText));
        return button;
    }

    private void updateButtonText(Button button, boolean showFullText) {
        String[] textOptions = (String[]) button.getUserData();
        if (textOptions != null) {
            if (showFullText) {
                // Claude-style: Icon + Text side by side
                button.setText(textOptions[0] + "  " + textOptions[2]); // Icon + short text
                button.setPrefWidth(180);
                button.setMinWidth(180);
            } else {
                // Icon only
                button.setText(textOptions[0]);
                button.setPrefWidth(38);
                button.setMinWidth(38);
            }
        }
    }

    private void createContentArea() {
        contentArea = new VBox(0);
        contentArea.getStyleClass().add("dual-panel-content");
        contentArea.setVisible(false);
        contentArea.setManaged(false);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        this.getChildren().add(contentArea);
    }

    private void setupTabBehavior() {
        // Listen for expansion state changes
        toolsExpanded.addListener((obs, oldVal, newVal) -> updateLayout());
        structureExpanded.addListener((obs, oldVal, newVal) -> updateLayout());

        // Combined expansion state
        isExpanded.bind(toolsExpanded.or(structureExpanded));
        isExpanded.addListener((obs, oldVal, newVal) -> {
            updateTabStyling();
            updateContentVisibility();
        });
    }

    private void handleTabClick(Button clickedTab, String tabName) {
        boolean isToolsTab = clickedTab == toolsTab;
        BooleanProperty targetProperty = isToolsTab ? toolsExpanded : structureExpanded;

        // Toggle the clicked tab
        targetProperty.set(!targetProperty.get());

        System.out.println("🔄 " + tabName + " tab " + (targetProperty.get() ? "expanded" : "collapsed"));
    }

    private void updateLayout() {
        boolean bothOpen = toolsExpanded.get() && structureExpanded.get();
        boolean oneOpen = toolsExpanded.get() || structureExpanded.get();

        double targetWidth;
        if (bothOpen) {
            targetWidth = dualPanelWidth; // 400px for horizontal split
        } else if (oneOpen) {
            targetWidth = singlePanelWidth; // 250px for single panel
        } else {
            targetWidth = collapsedWidth; // 50px for icons only
        }

        // Update content
        updateContentLayout();

        // Animate width change
        animateWidth(targetWidth);

        // Update button text based on expansion state
        updateButtonText(toolsTab, oneOpen);
        updateButtonText(structureTab, oneOpen);
    }

    private void updateContentLayout() {
        contentArea.getChildren().clear();

        boolean showTools = toolsExpanded.get();
        boolean showStructure = structureExpanded.get();

        if (showTools && showStructure) {
            // Both panels open - side by side or stacked
            VBox bothContent = createDualPanelLayout();
            contentArea.getChildren().add(bothContent);
        } else if (showTools) {
            // Only tools
            if (toolsContent != null) {
                contentArea.getChildren().add(toolsContent);
            }
        } else if (showStructure) {
            // Only structure
            if (structureContent != null) {
                contentArea.getChildren().add(structureContent);
            }
        }
    }

    private VBox createDualPanelLayout() {
        VBox dualLayout = new VBox(0);
        dualLayout.getStyleClass().add("dual-panel-layout");

        // Create horizontal split when both panels are open
        HBox horizontalSplit = new HBox(0);
        horizontalSplit.getStyleClass().add("horizontal-dual-panel");

        // Left side - Tools section
        if (toolsContent != null) {
            VBox toolsSection = new VBox(0);
            toolsSection.getStyleClass().add("dual-panel-left-section");
            toolsSection.setPrefWidth(200); // Fixed width for tools
            toolsSection.setMinWidth(200);
            toolsSection.setMaxWidth(200);

            // Compact tools header
            HBox toolsHeader = createCompactHeader("🛠️", "Tools", () -> toolsExpanded.set(false));

            // Tools content in scroll pane
            ScrollPane toolsScrollPane = new ScrollPane(toolsContent);
            toolsScrollPane.setFitToWidth(true);
            toolsScrollPane.getStyleClass().add("dual-panel-scroll");
            VBox.setVgrow(toolsScrollPane, Priority.ALWAYS);

            toolsSection.getChildren().addAll(toolsHeader, toolsScrollPane);
            horizontalSplit.getChildren().add(toolsSection);
        }

        // Vertical separator
        if (toolsContent != null && structureContent != null) {
            Region verticalSeparator = new Region();
            verticalSeparator.getStyleClass().add("dual-panel-vertical-separator");
            verticalSeparator.setPrefWidth(1);
            verticalSeparator.setMinWidth(1);
            verticalSeparator.setMaxWidth(1);
            horizontalSplit.getChildren().add(verticalSeparator);
        }

        // Right side - Structure section
        if (structureContent != null) {
            VBox structureSection = new VBox(0);
            structureSection.getStyleClass().add("dual-panel-right-section");

            // Structure takes remaining space
            HBox.setHgrow(structureSection, Priority.ALWAYS);

            // Compact structure header
            HBox structureHeader = createCompactHeader("📁", "Structure", () -> structureExpanded.set(false));

            // Structure content in scroll pane
            ScrollPane structureScrollPane = new ScrollPane(structureContent);
            structureScrollPane.setFitToWidth(true);
            structureScrollPane.getStyleClass().add("dual-panel-scroll");
            VBox.setVgrow(structureScrollPane, Priority.ALWAYS);

            structureSection.getChildren().addAll(structureHeader, structureScrollPane);
            horizontalSplit.getChildren().add(structureSection);
        }

        dualLayout.getChildren().add(horizontalSplit);
        return dualLayout;
    }

    private HBox createCompactHeader(String icon, String title, Runnable onClose) {
        HBox header = new HBox(6);
        header.getStyleClass().add("dual-panel-compact-header");
        header.setPadding(new Insets(6, 8, 6, 8));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(28);
        header.setMaxHeight(28);

        Label titleLabel = new Label(icon + " " + title);
        titleLabel.getStyleClass().add("dual-panel-compact-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("×");
        closeBtn.getStyleClass().add("dual-panel-close-btn");
        closeBtn.setOnAction(e -> onClose.run());
        closeBtn.setPrefSize(16, 16);
        closeBtn.setMinSize(16, 16);
        closeBtn.setMaxSize(16, 16);

        header.getChildren().addAll(titleLabel, spacer, closeBtn);
        return header;
    }

    private void animateWidth(double targetWidth) {
        // Stop any existing animation
        if (currentTransition != null) {
            currentTransition.stop();
        }

        double startWidth = this.getPrefWidth();

        // Create smooth width transition
        currentTransition = new Transition() {
            {
                setCycleDuration(Duration.millis(300));
                setInterpolator(Interpolator.EASE_BOTH);
            }

            @Override
            protected void interpolate(double frac) {
                double currentWidth = startWidth + (targetWidth - startWidth) * frac;
                setPrefWidth(currentWidth);
                setMinWidth(currentWidth);
                setMaxWidth(currentWidth);
            }
        };

        currentTransition.setOnFinished(e -> updateContentVisibility());
        currentTransition.play();
    }

    private void updateTabStyling() {
        // Reset all tab styles
        toolsTab.getStyleClass().removeAll("dual-panel-tab-active");
        structureTab.getStyleClass().removeAll("dual-panel-tab-active");

        // Apply active style to open tabs
        if (toolsExpanded.get()) {
            toolsTab.getStyleClass().add("dual-panel-tab-active");
        }
        if (structureExpanded.get()) {
            structureTab.getStyleClass().add("dual-panel-tab-active");
        }
    }

    private void updateContentVisibility() {
        if (isExpanded.get()) {
            contentArea.setVisible(true);
            contentArea.setManaged(true);
        } else {
            contentArea.setVisible(false);
            contentArea.setManaged(false);
        }
    }

    private void applyInitialState() {
        // Start collapsed
        setPrefWidth(collapsedWidth);
        setMinWidth(collapsedWidth);
        setMaxWidth(collapsedWidth);

        updateTabStyling();
        updateContentVisibility();
    }

    // Public API for adding content
    public void setToolsContent(Node content) {
        this.toolsContent = content;
        System.out.println("🛠️ Tools content added to EnhancedDualPanelGroup");
    }

    public void setStructureContent(Node content) {
        this.structureContent = content;
        System.out.println("📁 Structure content added to EnhancedDualPanelGroup");
    }

    // Public API for programmatic control
    public void expandToolsTab() {
        toolsExpanded.set(true);
    }

    public void expandStructureTab() {
        structureExpanded.set(true);
    }

    public void collapseToolsTab() {
        toolsExpanded.set(false);
    }

    public void collapseStructureTab() {
        structureExpanded.set(false);
    }

    public void collapseAll() {
        toolsExpanded.set(false);
        structureExpanded.set(false);
    }

    public void expandBoth() {
        toolsExpanded.set(true);
        structureExpanded.set(true);
    }

    // Properties for external monitoring
    public boolean isExpanded() {
        return isExpanded.get();
    }

    public BooleanProperty expandedProperty() {
        return isExpanded;
    }

    public boolean isToolsExpanded() {
        return toolsExpanded.get();
    }

    public boolean isStructureExpanded() {
        return structureExpanded.get();
    }

    public BooleanProperty toolsExpandedProperty() {
        return toolsExpanded;
    }

    public BooleanProperty structureExpandedProperty() {
        return structureExpanded;
    }

    // Method to get content area width for layout calculations
    public double getContentWidth() {
        boolean bothOpen = toolsExpanded.get() && structureExpanded.get();
        boolean oneOpen = toolsExpanded.get() || structureExpanded.get();

        if (bothOpen) {
            return dualPanelWidth;
        } else if (oneOpen) {
            return singlePanelWidth;
        } else {
            return collapsedWidth;
        }
    }

    // Method to set custom widths if needed
    public void setWidths(double collapsed, double single, double dual) {
        this.collapsedWidth = collapsed;
        this.singlePanelWidth = single;
        this.dualPanelWidth = dual;

        if (!isExpanded.get()) {
            setPrefWidth(collapsed);
            setMinWidth(collapsed);
            setMaxWidth(collapsed);
        }
    }

    // Get current layout state for debugging
    public String getCurrentState() {
        if (toolsExpanded.get() && structureExpanded.get()) {
            return "Both panels open";
        } else if (toolsExpanded.get()) {
            return "Tools panel open";
        } else if (structureExpanded.get()) {
            return "Structure panel open";
        } else {
            return "All panels collapsed";
        }
    }
}