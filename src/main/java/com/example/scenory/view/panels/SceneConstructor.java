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

/**
 * Simplified Scene Constructor with clean collapsed state like the tool panel
 */
public class SceneConstructor extends VBox {
    private BooleanProperty collapsed = new SimpleBooleanProperty(false);
    private VBox contentArea;
    private VBox collapsedStateContainer;
    private Button toggleButton;
    private Label titleLabel;
    private String title;

    // Scene/Panel info components
    private Label currentSceneLabel;
    private Label currentPanelLabel;
    private Label panelCountLabel;
    private Button prevPanelBtn;
    private Button nextPanelBtn;

    // Content components
    private Node thumbnailContent;

    // Animation properties
    private double expandedWidth = 300;
    private double collapsedWidth = 50; // Same as tool panel
    private Transition currentTransition;

    // Navigation callbacks
    private Runnable onPreviousPanel;
    private Runnable onNextPanel;

    public SceneConstructor(String title, Node thumbnailContent) {
        this.title = title;
        this.thumbnailContent = thumbnailContent;
        this.collapsed.set(false);

        initializeComponent();
        setupAnimation();
        applyInitialState();

        System.out.println("🔧 SceneConstructor created: " + title);
    }

    private void initializeComponent() {
        this.getStyleClass().add("simple-scene-constructor");
        this.setSpacing(0);

        // Create main header (when expanded)
        HBox header = createExpandedHeader();

        // Create content area with navigation
        contentArea = createContentArea();
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // FIXED: Create simple collapsed state like tool panel
        collapsedStateContainer = createSimpleCollapsedState();

        this.getChildren().addAll(header, contentArea, collapsedStateContainer);
    }

    private HBox createExpandedHeader() {
        HBox header = new HBox(12);
        header.getStyleClass().add("simple-scene-constructor-header");
        header.setPadding(new Insets(8, 12, 8, 12));
        header.setMinHeight(40);
        header.setMaxHeight(40);
        header.setAlignment(Pos.CENTER_LEFT);

        // Title
        titleLabel = new Label(title);
        titleLabel.getStyleClass().add("scene-constructor-title");

        // Center spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Scene/Panel info
        VBox sceneInfo = createSceneInfoDisplay();

        // Collapse button (arrow pointing right when expanded)
        toggleButton = new Button("▶");
        toggleButton.getStyleClass().add("scene-constructor-toggle-button");
        toggleButton.setOnAction(e -> toggleCollapse());
        toggleButton.setPrefSize(20, 20);
        toggleButton.setMinSize(20, 20);
        toggleButton.setMaxSize(20, 20);
        Tooltip.install(toggleButton, new Tooltip("Collapse Scene Panels"));

        header.getChildren().addAll(titleLabel, spacer, sceneInfo, toggleButton);
        return header;
    }

    private VBox createSimpleCollapsedState() {
        VBox collapsedContainer = new VBox(0);
        collapsedContainer.getStyleClass().add("simple-collapsed-container");
        collapsedContainer.setAlignment(Pos.TOP_CENTER);
        collapsedContainer.setPadding(new Insets(8, 4, 8, 4));
        collapsedContainer.setVisible(false);
        collapsedContainer.setManaged(false);

        // FIXED: Single icon button like the tool panel
        Button collapsedButton = new Button("📋");
        collapsedButton.getStyleClass().add("collapsed-scene-icon-button");
        collapsedButton.setOnAction(e -> toggleCollapse());
        collapsedButton.setPrefSize(40, 40);
        collapsedButton.setMinSize(40, 40);
        collapsedButton.setMaxSize(40, 40);
        Tooltip.install(collapsedButton, new Tooltip("Scene Panels"));

        collapsedContainer.getChildren().add(collapsedButton);
        return collapsedContainer;
    }

    private VBox createSceneInfoDisplay() {
        VBox sceneInfo = new VBox(1);
        sceneInfo.setAlignment(Pos.CENTER_RIGHT);
        sceneInfo.getStyleClass().add("scene-info-container");

        currentSceneLabel = new Label("Scene 1");
        currentSceneLabel.getStyleClass().add("current-scene-label");

        currentPanelLabel = new Label("Panel 1");
        currentPanelLabel.getStyleClass().add("current-panel-label");

        sceneInfo.getChildren().addAll(currentSceneLabel, currentPanelLabel);
        return sceneInfo;
    }

    private VBox createContentArea() {
        VBox content = new VBox(8);
        content.getStyleClass().add("simple-scene-constructor-content");
        content.setPadding(new Insets(8));

        // Panel navigation controls (clean, no dotted lines)
        HBox navigation = createCleanPanelNavigation();

        // FIXED: Add rich text display area
        VBox richTextArea = createRichTextDisplayArea();

        // Thumbnails container
        VBox thumbnailContainer = new VBox();
        thumbnailContainer.getStyleClass().add("thumbnail-container");

        if (thumbnailContent != null) {
            thumbnailContainer.getChildren().add(thumbnailContent);
        }

        ScrollPane thumbnailScrollPane = new ScrollPane(thumbnailContainer);
        thumbnailScrollPane.setFitToWidth(true);
        thumbnailScrollPane.getStyleClass().add("invisible-scroll-pane");
        VBox.setVgrow(thumbnailScrollPane, Priority.ALWAYS);

        content.getChildren().addAll(navigation, richTextArea, thumbnailScrollPane);
        return content;
    }

    private HBox createCleanPanelNavigation() {
        HBox navigation = new HBox(8);
        navigation.setAlignment(Pos.CENTER);
        navigation.getStyleClass().add("clean-panel-navigation");
        navigation.setPadding(new Insets(6, 8, 6, 8));

        // Previous panel button
        prevPanelBtn = new Button("◀");
        prevPanelBtn.getStyleClass().add("nav-icon-button");
        prevPanelBtn.setPrefSize(24, 20);
        prevPanelBtn.setOnAction(e -> {
            if (onPreviousPanel != null) {
                onPreviousPanel.run();
            }
        });
        Tooltip.install(prevPanelBtn, new Tooltip("Previous Panel"));

        // Panel count in center
        panelCountLabel = new Label("1 panels");
        panelCountLabel.getStyleClass().add("panel-count-label");

        // Spacers
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Next panel button
        nextPanelBtn = new Button("▶");
        nextPanelBtn.getStyleClass().add("nav-icon-button");
        nextPanelBtn.setPrefSize(24, 20);
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

    // FIXED: Add rich text display area
    private VBox createRichTextDisplayArea() {
        VBox richTextContainer = new VBox(4);
        richTextContainer.getStyleClass().add("rich-text-display-container");
        richTextContainer.setPadding(new Insets(4, 8, 4, 8));
        richTextContainer.setVisible(false); // Hidden by default
        richTextContainer.setManaged(false);

        Label richTextLabel = new Label("📝 Description");
        richTextLabel.getStyleClass().add("rich-text-label");

        ScrollPane richTextScrollPane = new ScrollPane();
        richTextScrollPane.getStyleClass().add("rich-text-scroll-pane");
        richTextScrollPane.setFitToWidth(true);
        richTextScrollPane.setPrefHeight(80);
        richTextScrollPane.setMaxHeight(120);

        Label richTextContent = new Label();
        richTextContent.getStyleClass().add("rich-text-content");
        richTextContent.setWrapText(true);
        richTextContent.setPadding(new Insets(6));

        richTextScrollPane.setContent(richTextContent);

        richTextContainer.getChildren().addAll(richTextLabel, richTextScrollPane);

        // Store references for updating
        richTextContainer.setUserData(richTextContent);

        return richTextContainer;
    }

    private HBox createPanelNavigation() {
        HBox navigation = new HBox(8);
        navigation.setAlignment(Pos.CENTER);
        navigation.getStyleClass().add("panel-navigation");
        navigation.setPadding(new Insets(6, 8, 6, 8));

        // Previous panel button
        prevPanelBtn = new Button("◀");
        prevPanelBtn.getStyleClass().add("nav-icon-button");
        prevPanelBtn.setPrefSize(24, 20);
        prevPanelBtn.setOnAction(e -> {
            if (onPreviousPanel != null) {
                onPreviousPanel.run();
            }
        });
        Tooltip.install(prevPanelBtn, new Tooltip("Previous Panel"));

        // Panel count in center
        panelCountLabel = new Label("1 panels");
        panelCountLabel.getStyleClass().add("panel-count-label");

        // Spacers
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Next panel button
        nextPanelBtn = new Button("▶");
        nextPanelBtn.getStyleClass().add("nav-icon-button");
        nextPanelBtn.setPrefSize(24, 20);
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
        if (currentTransition != null) {
            currentTransition.stop();
        }

        double startWidth = this.getPrefWidth();
        double targetWidth = collapsed.get() ? collapsedWidth : expandedWidth;

        // FIXED: Clean state management
        if (collapsed.get()) {
            // Collapse: hide expanded content, show collapsed state
            contentArea.setVisible(false);
            contentArea.setManaged(false);
            collapsedStateContainer.setVisible(true);
            collapsedStateContainer.setManaged(true);
        } else {
            // Expand: hide collapsed state, show expanded content
            collapsedStateContainer.setVisible(false);
            collapsedStateContainer.setManaged(false);
        }

        currentTransition = new Transition() {
            {
                setCycleDuration(Duration.millis(250));
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

        currentTransition.setOnFinished(e -> {
            if (!collapsed.get()) {
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

    // Public API
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

    // FIXED: Add method to update rich text display
    public void updateRichTextDisplay(String richText) {
        if (contentArea == null) return;

        // Find the rich text container
        VBox richTextContainer = null;
        for (javafx.scene.Node node : contentArea.getChildren()) {
            if (node instanceof VBox && node.getStyleClass().contains("rich-text-display-container")) {
                richTextContainer = (VBox) node;
                break;
            }
        }

        if (richTextContainer != null) {
            Label richTextContent = (Label) richTextContainer.getUserData();

            if (richText != null && !richText.trim().isEmpty()) {
                // Show rich text area
                richTextContainer.setVisible(true);
                richTextContainer.setManaged(true);
                richTextContent.setText(richText);
            } else {
                // Hide rich text area
                richTextContainer.setVisible(false);
                richTextContainer.setManaged(false);
                richTextContent.setText("");
            }
        }
    }

    public void setOnPreviousPanel(Runnable callback) {
        this.onPreviousPanel = callback;
    }

    public void setOnNextPanel(Runnable callback) {
        this.onNextPanel = callback;
    }

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
}