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
    private VBox collapsedStateContainer; // NEW: Container for collapsed state
    private Button toggleButton;
    private Label titleLabel;
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
    private double expandedWidth = 300;
    private double collapsedWidth = 60; // INCREASED: More space for toggle button
    private Transition currentTransition;

    // Navigation callbacks
    private Runnable onPreviousPanel;
    private Runnable onNextPanel;

    public CollapsibleSceneConstructor(String title, Node thumbnailContent) {
        this.title = title;
        this.thumbnailContent = thumbnailContent;
        this.collapsed.set(false); // Start expanded by default

        initializeComponent();
        setupAnimation();
        applyInitialState();

        System.out.println("🔧 FIXED CollapsibleSceneConstructor created: " + title);
    }

    private void initializeComponent() {
        this.getStyleClass().add("enhanced-scene-constructor");
        this.setSpacing(0);

        // Create enhanced header with scene/panel info at top-right
        HBox header = createEnhancedHeader();

        // Create content area with navigation above thumbnails
        contentArea = createEnhancedContentArea();
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // NEW: Create collapsed state container with vertical toggle
        collapsedStateContainer = createCollapsedStateContainer();

        this.getChildren().addAll(header, contentArea, collapsedStateContainer);
    }

    private HBox createEnhancedHeader() {
        HBox header = new HBox(12);
        header.getStyleClass().add("enhanced-scene-constructor-header");
        header.setPadding(new Insets(8, 12, 8, 12));
        header.setMinHeight(40);
        header.setMaxHeight(40);
        header.setAlignment(Pos.CENTER_LEFT);

        // Left side: Title
        titleLabel = new Label(title);
        titleLabel.getStyleClass().add("scene-constructor-title");

        // Center spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right side: Scene/Panel info + collapse button
        HBox rightSection = new HBox(8);
        rightSection.setAlignment(Pos.CENTER_RIGHT);

        // Scene/Panel info container
        VBox sceneInfo = createSceneInfoDisplay();

        // Collapse button
        toggleButton = new Button();
        toggleButton.getStyleClass().add("scene-constructor-toggle-button");
        toggleButton.setOnAction(e -> toggleCollapse());
        toggleButton.setPrefSize(18, 18);
        toggleButton.setMinSize(18, 18);
        toggleButton.setMaxSize(18, 18);

        rightSection.getChildren().addAll(sceneInfo, toggleButton);
        header.getChildren().addAll(titleLabel, spacer, rightSection);

        return header;
    }

    // NEW: Create collapsed state container with vertical toggle button
    private VBox createCollapsedStateContainer() {
        VBox collapsedContainer = new VBox(8);
        collapsedContainer.getStyleClass().add("scene-constructor-collapsed");
        collapsedContainer.setAlignment(Pos.TOP_CENTER);
        collapsedContainer.setPadding(new Insets(8, 4, 8, 4));
        collapsedContainer.setVisible(false);
        collapsedContainer.setManaged(false);

        // Vertical toggle button for collapsed state
        Button collapsedToggleButton = new Button("◀");
        collapsedToggleButton.getStyleClass().add("scene-constructor-collapsed-toggle");
        collapsedToggleButton.setOnAction(e -> toggleCollapse());
        collapsedToggleButton.setPrefSize(40, 30);
        collapsedToggleButton.setMinSize(40, 30);
        collapsedToggleButton.setMaxSize(40, 30);
        Tooltip.install(collapsedToggleButton, new Tooltip("Expand Scene Panels"));

        // Optional: Add small scene info vertically
        Label collapsedSceneInfo = new Label();
        collapsedSceneInfo.getStyleClass().add("collapsed-scene-info");
        collapsedSceneInfo.setText("S42"); // Will be updated
        collapsedSceneInfo.setStyle("-fx-font-size: 9px; -fx-text-fill: #888888;");

        collapsedContainer.getChildren().addAll(collapsedToggleButton, collapsedSceneInfo);
        return collapsedContainer;
    }

    private VBox createSceneInfoDisplay() {
        VBox sceneInfo = new VBox(1);
        sceneInfo.setAlignment(Pos.CENTER_RIGHT);
        sceneInfo.getStyleClass().add("scene-info-container");

        // Current scene label
        currentSceneLabel = new Label("Scene 1");
        currentSceneLabel.getStyleClass().add("current-scene-label");

        // Current panel label
        currentPanelLabel = new Label("Panel 1");
        currentPanelLabel.getStyleClass().add("current-panel-label");

        sceneInfo.getChildren().addAll(currentSceneLabel, currentPanelLabel);
        return sceneInfo;
    }

    private VBox createEnhancedContentArea() {
        VBox content = new VBox(8);
        content.getStyleClass().add("enhanced-scene-constructor-content");
        content.setPadding(new Insets(8));

        // Panel navigation controls ABOVE thumbnails (moved from bottom)
        HBox navigation = createPanelNavigation();

        // Thumbnails container
        VBox thumbnailContainer = new VBox();
        thumbnailContainer.getStyleClass().add("thumbnail-container");

        if (thumbnailContent != null) {
            thumbnailContainer.getChildren().add(thumbnailContent);
        }

        // Scrollable thumbnail area
        ScrollPane thumbnailScrollPane = new ScrollPane(thumbnailContainer);
        thumbnailScrollPane.setFitToWidth(true);
        thumbnailScrollPane.getStyleClass().add("invisible-scroll-pane");
        VBox.setVgrow(thumbnailScrollPane, Priority.ALWAYS);

        content.getChildren().addAll(navigation, thumbnailScrollPane);
        return content;
    }

    private HBox createPanelNavigation() {
        HBox navigation = new HBox(8);
        navigation.setAlignment(Pos.CENTER);
        navigation.getStyleClass().add("panel-navigation");
        navigation.setPadding(new Insets(6, 8, 6, 8));

        // Previous panel button (icon only)
        prevPanelBtn = new Button("◀");
        prevPanelBtn.getStyleClass().add("nav-icon-button");
        prevPanelBtn.setPrefSize(24, 20);
        prevPanelBtn.setMinSize(24, 20);
        prevPanelBtn.setMaxSize(24, 20);
        prevPanelBtn.setOnAction(e -> {
            if (onPreviousPanel != null) {
                onPreviousPanel.run();
            }
        });
        Tooltip.install(prevPanelBtn, new Tooltip("Previous Panel"));

        // Panel count/info in center
        panelCountLabel = new Label("1 panels");
        panelCountLabel.getStyleClass().add("panel-count-label");

        // Spacers to center the count
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Next panel button (icon only)
        nextPanelBtn = new Button("▶");
        nextPanelBtn.getStyleClass().add("nav-icon-button");
        nextPanelBtn.setPrefSize(24, 20);
        nextPanelBtn.setMinSize(24, 20);
        nextPanelBtn.setMaxSize(24, 20);
        nextPanelBtn.setOnAction(e -> {
            if (onNextPanel != null) {
                onNextPanel.run();
            }
        });
        Tooltip.install(nextPanelBtn, new Tooltip("Next Panel"));

        navigation.getChildren().addAll(
                prevPanelBtn, leftSpacer, panelCountLabel, rightSpacer, nextPanelBtn
        );

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
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
        } else {
            toggleButton.setText("▶"); // Arrow pointing right (collapse)
            titleLabel.setVisible(true);
            titleLabel.setManaged(true);
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

        // Update collapsed state info too
        if (collapsedStateContainer != null && !collapsedStateContainer.getChildren().isEmpty()) {
            Label collapsedInfo = (Label) collapsedStateContainer.getChildren().get(1);
            collapsedInfo.setText("S" + (panelIndex + 1));
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