package com.example.scenory.view.panels;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SmartTabbedPanelGroup extends VBox {

    // Tab system properties
    private VBox tabContainer;
    private VBox contentArea;
    private Button toolsTab, structureTab;
    private Node toolsContent, structureContent;
    private Button currentActiveTab = null;

    // Panel state
    private BooleanProperty isExpanded = new SimpleBooleanProperty(false);
    private double collapsedWidth = 50;   // Just enough for icon tabs
    private double expandedWidth = 250;   // Full content width
    private Transition currentTransition;

    public SmartTabbedPanelGroup() {
        initializeComponent();
        createIconTabs();
        createContentArea();
        setupTabBehavior();
        applyInitialState();

        System.out.println("🔧 SmartTabbedPanelGroup created with icon tabs");
    }

    private void initializeComponent() {
        this.getStyleClass().add("smart-tabbed-panel-group");
        this.setSpacing(0);
        this.setAlignment(Pos.TOP_LEFT);
    }

    private void createIconTabs() {
        tabContainer = new VBox(4); // Minimal spacing between tabs
        tabContainer.setAlignment(Pos.TOP_LEFT);
        tabContainer.setPadding(new Insets(8, 4, 8, 4)); // Close to left edge
        tabContainer.getStyleClass().add("icon-tab-container");

        // Tools tab with icon only
        toolsTab = new Button("🛠️");
        toolsTab.getStyleClass().addAll("icon-tab");
        toolsTab.setPrefSize(40, 50);
        toolsTab.setMinSize(40, 50);
        toolsTab.setMaxSize(40, 50);
        toolsTab.setOnAction(e -> handleTabClick(toolsTab, "Tools"));
        Tooltip.install(toolsTab, new Tooltip("Tools"));

        // Structure tab with icon only
        structureTab = new Button("📁");
        structureTab.getStyleClass().addAll("icon-tab");
        structureTab.setPrefSize(40, 50);
        structureTab.setMinSize(40, 50);
        structureTab.setMaxSize(40, 50);
        structureTab.setOnAction(e -> handleTabClick(structureTab, "Structure"));
        Tooltip.install(structureTab, new Tooltip("File Structure"));

        tabContainer.getChildren().addAll(toolsTab, structureTab);
        this.getChildren().add(tabContainer);
    }

    private void createContentArea() {
        contentArea = new VBox();
        contentArea.getStyleClass().add("smart-panel-content");
        contentArea.setVisible(false);
        contentArea.setManaged(false);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        this.getChildren().add(contentArea);
    }

    private void setupTabBehavior() {
        // Listen for expansion state changes
        isExpanded.addListener((obs, oldVal, newVal) -> {
            updateTabStyling();
            updateContentVisibility();
        });
    }

    private void handleTabClick(Button clickedTab, String tabName) {
        if (currentActiveTab == clickedTab && isExpanded.get()) {
            // Collapse if same tab clicked while expanded
            collapsePanel();
            System.out.println("🔄 Collapsed " + tabName + " tab");
        } else {
            // Expand with new content or switch content
            Node content = getContentForTab(clickedTab);
            if (content != null) {
                expandPanel(clickedTab, content);
                System.out.println("🔄 Expanded " + tabName + " tab");
            }
        }
    }

    private Node getContentForTab(Button tab) {
        if (tab == toolsTab) {
            return toolsContent;
        } else if (tab == structureTab) {
            return structureContent;
        }
        return null;
    }

    private void expandPanel(Button activeTab, Node content) {
        currentActiveTab = activeTab;

        // Update content immediately
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);

        if (!isExpanded.get()) {
            // If currently collapsed, animate expansion
            isExpanded.set(true);
            animateWidth(expandedWidth);
        } else {
            // If already expanded, just switch content (no animation needed)
            updateTabStyling();
        }
    }

    private void collapsePanel() {
        currentActiveTab = null;
        isExpanded.set(false);
        animateWidth(collapsedWidth);
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
                setCycleDuration(Duration.millis(300)); // Smooth animation
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

        // When animation finishes, update content visibility
        currentTransition.setOnFinished(e -> updateContentVisibility());
        currentTransition.play();
    }

    private void updateTabStyling() {
        // Reset all tab styles
        toolsTab.getStyleClass().removeAll("icon-tab-active");
        structureTab.getStyleClass().removeAll("icon-tab-active");

        // Apply active style to current tab
        if (currentActiveTab != null && isExpanded.get()) {
            currentActiveTab.getStyleClass().add("icon-tab-active");
        }
    }

    private void updateContentVisibility() {
        if (isExpanded.get() && currentActiveTab != null) {
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
        System.out.println("🛠️ Tools content added to SmartTabbedPanelGroup");
    }

    public void setStructureContent(Node content) {
        this.structureContent = content;
        System.out.println("📁 Structure content added to SmartTabbedPanelGroup");
    }

    // Public API for programmatic control
    public void expandToolsTab() {
        if (toolsContent != null) {
            handleTabClick(toolsTab, "Tools");
        }
    }

    public void expandStructureTab() {
        if (structureContent != null) {
            handleTabClick(structureTab, "Structure");
        }
    }

    public void collapseAll() {
        collapsePanel();
    }

    // Properties
    public boolean isExpanded() {
        return isExpanded.get();
    }

    public BooleanProperty expandedProperty() {
        return isExpanded;
    }

    public Button getCurrentActiveTab() {
        return currentActiveTab;
    }

    public String getCurrentTabName() {
        if (currentActiveTab == toolsTab) {
            return "Tools";
        } else if (currentActiveTab == structureTab) {
            return "Structure";
        }
        return "None";
    }
}