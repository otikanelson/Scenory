package com.example.scenory.view.panels;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CollapsibleSceneConstructor extends VBox {
    private BooleanProperty collapsed = new SimpleBooleanProperty(false);
    private VBox contentArea;
    private VBox collapsedStateContainer;
    private Button toggleButton;
    private String title;

    // Scene/Panel info components (moved to header)
    private Label currentSceneLabel;
    private Label currentPanelLabel;

    // Navigation components (moved above thumbnails)
    private Button prevPanelBtn;
    private Button nextPanelBtn;
    private Label panelCountLabel;

    // Content components
    private Node thumbnailContent;

    // Animation properties
    private double expandedWidth = 280;
    private double collapsedWidth = 48; // Compact collapsed state
    private Transition currentTransition;

    // Navigation callbacks
    private Runnable onPreviousPanel;
    private Runnable onNextPanel;

    public CollapsibleSceneConstructor(String title, Node thumbnailContent) {
        this.title = title;
        this.thumbnailContent = thumbnailContent;
        this.collapsed.set(false); // Start expanded by default

        // Set minimum width to prevent cut-off
        this.setMinWidth(48);
        
        initializeComponent();
        setupAnimation();
        applyInitialState();

        System.out.println("🔧 Redesigned CollapsibleSceneConstructor created: " + title);
    }

    private void initializeComponent() {
        this.getStyleClass().add("enhanced-scene-constructor");
        this.setSpacing(0);

        // Create modern header
        HBox header = createModernHeader();
        header.managedProperty().bind(collapsed.not());
        header.visibleProperty().bind(collapsed.not());

        // Create content area
        contentArea = createEnhancedContentArea();
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Create collapsed state
        collapsedStateContainer = createModernCollapsedState();

        this.getChildren().addAll(header, contentArea, collapsedStateContainer);
    }

    private HBox createModernHeader() {
        HBox header = new HBox(12);
        header.getStyleClass().add("modern-scene-header");
        header.setPadding(new Insets(12, 16, 12, 16));
        header.setAlignment(Pos.CENTER_LEFT);

        // Title with icon
        Label titleLabel = new Label("📋 " + title);
        titleLabel.getStyleClass().add("modern-scene-title");
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #ffffff;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Collapse button
        toggleButton = new Button("◀");
        toggleButton.getStyleClass().add("modern-collapse-button");
        toggleButton.setOnAction(e -> toggleCollapse());
        toggleButton.setPrefSize(24, 24);
        toggleButton.setStyle("-fx-font-size: 12px; -fx-background-radius: 6; -fx-border-radius: 6;");
        Tooltip.install(toggleButton, new Tooltip("Collapse Panel"));

        header.getChildren().addAll(titleLabel, spacer, toggleButton);
        return header;
    }

    // Modern collapsed state with vertical icon button
    private VBox createModernCollapsedState() {
        VBox collapsedContainer = new VBox(0);
        collapsedContainer.getStyleClass().add("modern-collapsed-panel");
        collapsedContainer.setAlignment(Pos.TOP_CENTER);
        collapsedContainer.setPadding(new Insets(12, 0, 12, 0));
        collapsedContainer.setVisible(false);
        collapsedContainer.setManaged(false);

        // Modern icon button
        Button expandButton = new Button("📋");
        expandButton.getStyleClass().add("modern-expand-button");
        expandButton.setOnAction(e -> toggleCollapse());
        expandButton.setPrefSize(36, 36);
        expandButton.setMinSize(36, 36);
        expandButton.setMaxSize(36, 36);
        expandButton.setStyle("-fx-font-size: 18px; -fx-background-radius: 10; -fx-border-radius: 10;");
        Tooltip.install(expandButton, new Tooltip("Expand Scene Panels"));

        collapsedContainer.getChildren().add(expandButton);
        
        System.out.println("🔧 Modern collapsed button created: 📋");
        
        return collapsedContainer;
    }

    private VBox createEnhancedContentArea() {
        VBox content = new VBox(0);
        content.getStyleClass().add("modern-scene-content");
        content.setPadding(new Insets(0));

        // Modern navigation bar
        HBox navigation = createModernNavigation();

        // Scene info display
        VBox sceneInfoPanel = createModernSceneInfo();

        // Thumbnails container
        VBox thumbnailContainer = new VBox(8);
        thumbnailContainer.getStyleClass().add("modern-thumbnail-container");
        thumbnailContainer.setPadding(new Insets(12));

        if (thumbnailContent != null) {
            thumbnailContainer.getChildren().add(thumbnailContent);
        }

        // Scrollable thumbnail area
        ScrollPane thumbnailScrollPane = new ScrollPane(thumbnailContainer);
        thumbnailScrollPane.setFitToWidth(true);
        thumbnailScrollPane.getStyleClass().add("invisible-scroll-pane");
        VBox.setVgrow(thumbnailScrollPane, Priority.ALWAYS);

        content.getChildren().addAll(sceneInfoPanel, navigation, thumbnailScrollPane);
        return content;
    }

    private VBox createModernSceneInfo() {
        VBox infoPanel = new VBox(4);
        infoPanel.getStyleClass().add("modern-scene-info-panel");
        infoPanel.setPadding(new Insets(12, 16, 12, 16));
        infoPanel.setStyle("-fx-background-color: rgba(30, 30, 30, 0.95); -fx-border-color: rgba(255, 255, 255, 0.05); -fx-border-width: 0 0 1 0;");

        currentSceneLabel = new Label("Scene 1");
        currentSceneLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #999999; -fx-font-weight: 500;");

        currentPanelLabel = new Label("Panel 1");
        currentPanelLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff; -fx-font-weight: 600;");

        infoPanel.getChildren().addAll(currentSceneLabel, currentPanelLabel);
        return infoPanel;
    }

    private HBox createModernNavigation() {
        HBox navigation = new HBox(8);
        navigation.setAlignment(Pos.CENTER);
        navigation.getStyleClass().add("modern-panel-navigation");
        navigation.setPadding(new Insets(10, 12, 10, 12));
        navigation.setStyle("-fx-background-color: rgba(25, 25, 25, 0.95);");

        // Previous button
        prevPanelBtn = new Button("◀");
        prevPanelBtn.getStyleClass().add("modern-nav-button");
        prevPanelBtn.setPrefSize(28, 28);
        prevPanelBtn.setStyle("-fx-font-size: 11px; -fx-background-radius: 6; -fx-border-radius: 6;");
        prevPanelBtn.setOnAction(e -> {
            if (onPreviousPanel != null) onPreviousPanel.run();
        });
        Tooltip.install(prevPanelBtn, new Tooltip("Previous Panel"));

        // Panel count
        panelCountLabel = new Label("1 panels");
        panelCountLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #cccccc; -fx-font-weight: 500;");

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Next button
        nextPanelBtn = new Button("▶");
        nextPanelBtn.getStyleClass().add("modern-nav-button");
        nextPanelBtn.setPrefSize(28, 28);
        nextPanelBtn.setStyle("-fx-font-size: 11px; -fx-background-radius: 6; -fx-border-radius: 6;");
        nextPanelBtn.setOnAction(e -> {
            if (onNextPanel != null) onNextPanel.run();
        });
        Tooltip.install(nextPanelBtn, new Tooltip("Next Panel"));

        navigation.getChildren().addAll(prevPanelBtn, leftSpacer, panelCountLabel, rightSpacer, nextPanelBtn);
        return navigation;
    }

    private void setupAnimation() {
        // Listen for collapse state changes
        collapsed.addListener((obs, oldVal, newVal) -> {
            updateToggleButton();
            updateTooltip();
        });
    }

    private void updateToggleButton() {
        if (collapsed.get()) {
            toggleButton.setText("◀"); // Arrow pointing left (expand)
        } else {
            toggleButton.setText("▶"); // Arrow pointing right (collapse)
        }
    }

    private void updateTooltip() {
        if (collapsed.get()) {
            Tooltip.install(this, new Tooltip(title));
        } else {
            Tooltip.uninstall(this, null);
        }
    }

    public void toggleCollapse() {
        collapsed.set(!collapsed.get());
        animateToggle();
        System.out.println("🔄 Scene Constructor " + (collapsed.get() ? "collapsed" : "expanded"));
    }

    private void animateToggle() {
        // Stop any existing animation
        if (currentTransition != null) {
            currentTransition.stop();
        }

        double startWidth = this.getPrefWidth();
        double targetWidth = collapsed.get() ? collapsedWidth : expandedWidth;
        boolean shouldShowContent = !collapsed.get();
        boolean shouldShowCollapsed = collapsed.get();

        // FIXED: Manage visibility of different states
        if (collapsed.get()) {
            // Collapse: hide content, show collapsed state
            contentArea.setVisible(false);
            contentArea.setManaged(false);
            collapsedStateContainer.setVisible(true);
            collapsedStateContainer.setManaged(true);
        } else {
            // Expand: show content, hide collapsed state
            collapsedStateContainer.setVisible(false);
            collapsedStateContainer.setManaged(false);
        }

        // Create smooth width transition with EASE_BOTH interpolator
        currentTransition = new Transition() {
            {
                setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_BOTH);
            }

            @Override
            protected void interpolate(double frac) {
                // Smoothly interpolate between start and target width
                double currentWidth = startWidth + (targetWidth - startWidth) * frac;
                setPrefWidth(currentWidth);
                setMinWidth(currentWidth);
                setMaxWidth(currentWidth);
            }
        };

        // When animation finishes, show content if expanding
        currentTransition.setOnFinished(e -> {
            if (shouldShowContent) {
                contentArea.setVisible(true);
                contentArea.setManaged(true);
            }
        });

        currentTransition.play();
    }

    private void applyInitialState() {
        updateToggleButton();
        updateTooltip();

        if (collapsed.get()) {
            setPrefWidth(collapsedWidth);
            setMinWidth(collapsedWidth);
            setMaxWidth(collapsedWidth);
            contentArea.setVisible(false);
            contentArea.setManaged(false);
            collapsedStateContainer.setVisible(true);
            collapsedStateContainer.setManaged(true);
        } else {
            setPrefWidth(expandedWidth);
            setMinWidth(expandedWidth);
            setMaxWidth(expandedWidth);
            contentArea.setVisible(true);
            contentArea.setManaged(true);
            collapsedStateContainer.setVisible(false);
            collapsedStateContainer.setManaged(false);
        }
    }

    // Public API for updating scene/panel info
    public void updateSceneInfo(String sceneName, String panelName, int panelIndex, int totalPanels) {
        if (currentSceneLabel != null) {
            currentSceneLabel.setText(sceneName);
        }
        if (currentPanelLabel != null) {
            currentPanelLabel.setText(panelName);
        }
        if (panelCountLabel != null) {
            panelCountLabel.setText(totalPanels + " panels");
        }

        // Update navigation button states
        if (prevPanelBtn != null) {
            prevPanelBtn.setDisable(panelIndex <= 0);
        }
        if (nextPanelBtn != null) {
            nextPanelBtn.setDisable(panelIndex >= totalPanels - 1);
        }
    }

    // Public API for setting navigation callbacks
    public void setOnPreviousPanel(Runnable callback) {
        this.onPreviousPanel = callback;
    }

    public void setOnNextPanel(Runnable callback) {
        this.onNextPanel = callback;
    }

    // Standard CollapsiblePanel API
    public boolean isCollapsed() {
        return collapsed.get();
    }

    public void setCollapsed(boolean collapsed) {
        if (this.collapsed.get() != collapsed) {
            toggleCollapse();
        }
    }

    public BooleanProperty collapsedProperty() {
        return collapsed;
    }

    public void setExpandedWidth(double width) {
        this.expandedWidth = width;
        if (!collapsed.get()) {
            setPrefWidth(width);
        }
    }

    public String getTitle() {
        return title;
    }

    // Get references to UI components for external updates
    public Label getCurrentSceneLabel() {
        return currentSceneLabel;
    }

    public Label getCurrentPanelLabel() {
        return currentPanelLabel;
    }

    public Label getPanelCountLabel() {
        return panelCountLabel;
    }

    public Button getPrevPanelBtn() {
        return prevPanelBtn;
    }

    public Button getNextPanelBtn() {
        return nextPanelBtn;
    }
}