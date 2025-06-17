package com.example.scenory.view.panels;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CollapsiblePanel extends VBox {
    private BooleanProperty collapsed = new SimpleBooleanProperty(false);
    private VBox contentArea;
    private Button toggleButton;
    private Label titleLabel;
    private String title;
    private Node content;

    // Animation properties
    private double expandedWidth = 250;
    private double collapsedWidth = 40;
    private Transition currentTransition;

    public CollapsiblePanel(String title, Node content, boolean startCollapsed) {
        this.title = title;
        this.content = content;
        this.collapsed.set(startCollapsed);

        initializeComponent();
        setupAnimation();
        applyInitialState();

        System.out.println("🔧 CollapsiblePanel created: " + title + " (collapsed: " + startCollapsed + ")");
    }

    private void initializeComponent() {
        this.getStyleClass().add("collapsible-panel");
        this.setSpacing(0);

        // Create header
        HBox header = createHeader();

        // Create content area
        contentArea = new VBox();
        contentArea.getStyleClass().add("collapsible-panel-content");
        contentArea.getChildren().add(content);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        this.getChildren().addAll(header, contentArea);
    }

    private HBox createHeader() {
        HBox header = new HBox(8);
        header.getStyleClass().add("collapsible-panel-header");
        header.setPadding(new Insets(8));
        header.setMinHeight(32);
        header.setMaxHeight(32);

        // Toggle button with arrow
        toggleButton = new Button();
        toggleButton.getStyleClass().add("panel-toggle-button");
        toggleButton.setOnAction(e -> toggleCollapse());
        toggleButton.setPrefSize(16, 16);
        toggleButton.setMinSize(16, 16);
        toggleButton.setMaxSize(16, 16);

        // Title label
        titleLabel = new Label(title);
        titleLabel.getStyleClass().add("panel-title");

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(toggleButton, titleLabel, spacer);

        // Add hover effect
        header.setOnMouseEntered(e -> header.getStyleClass().add("panel-header-hover"));
        header.setOnMouseExited(e -> header.getStyleClass().remove("panel-header-hover"));

        return header;
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
            toggleButton.setText("▶");
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
        } else {
            toggleButton.setText("◀");
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
        System.out.println("🔄 " + title + " " + (collapsed.get() ? "collapsed" : "expanded"));
    }

    private void animateToggle() {
        // Stop any existing animation
        if (currentTransition != null) {
            currentTransition.stop();
        }

        double startWidth = this.getPrefWidth();
        double targetWidth = collapsed.get() ? collapsedWidth : expandedWidth;
        boolean shouldShowContent = !collapsed.get();

        if (collapsed.get()) {
            // Collapse: hide content immediately for smooth animation
            contentArea.setVisible(false);
            contentArea.setManaged(false);
        }

        // Create smooth width transition with EASE_BOTH interpolator
        currentTransition = new Transition() {
            {
                setCycleDuration(Duration.millis(250)); // Slightly longer for smoother feel
                setInterpolator(Interpolator.EASE_BOTH); // Smooth easing - starts slow, speeds up, slows down
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
        } else {
            setPrefWidth(expandedWidth);
            setMinWidth(expandedWidth);
            setMaxWidth(expandedWidth);
            contentArea.setVisible(true);
            contentArea.setManaged(true);
        }
    }

    // Public API
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

    public void setCollapsedWidth(double width) {
        this.collapsedWidth = width;
        if (collapsed.get()) {
            setPrefWidth(width);
        }
    }

    public String getTitle() {
        return title;
    }

    public Node getContent() {
        return content;
    }
}