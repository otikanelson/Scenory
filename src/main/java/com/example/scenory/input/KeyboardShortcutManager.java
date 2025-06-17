package com.example.scenory.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.lang.Runnable;


/**
 * Keyboard Shortcut Manager for Scenory
 * Handles global keyboard shortcuts and tool switching
 */
public class KeyboardShortcutManager {

    private final Map<KeyCombination, Runnable> shortcuts = new HashMap<>();
    private final Map<KeyCode, Runnable> singleKeyShortcuts = new HashMap<>();
    private Scene scene;

    public KeyboardShortcutManager(Scene scene) {
        this.scene = scene;
        setupEventHandlers();
        System.out.println("⌨️ Keyboard shortcut manager initialized");
    }

    private void setupEventHandlers() {
        scene.setOnKeyPressed(this::handleKeyPressed);
    }

    private void handleKeyPressed(KeyEvent event) {
        // Check for combination shortcuts first
        for (Map.Entry<KeyCombination, Runnable> entry : shortcuts.entrySet()) {
            if (entry.getKey().match(event)) {
                entry.getValue().run();
                event.consume();
                return;
            }
        }

        // Check for single key shortcuts (only if no modifiers)
        if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown()) {
            Runnable action = singleKeyShortcuts.get(event.getCode());
            if (action != null) {
                action.run();
                event.consume();
            }
        }
    }

    // =====================================
    // SHORTCUT REGISTRATION METHODS
    // =====================================

    /**
     * Register a keyboard combination shortcut
     */
    public void registerShortcut(KeyCombination combination, Runnable action) {
        shortcuts.put(combination, action);
    }

    /**
     * Register a single key shortcut
     */
    public void registerShortcut(KeyCode key, Runnable action) {
        singleKeyShortcuts.put(key, action);
    }

    /**
     * Register a Ctrl+Key shortcut
     */
    public void registerCtrlShortcut(KeyCode key, Runnable action) {
        KeyCombination combination = new KeyCodeCombination(key, KeyCombination.CONTROL_DOWN);
        registerShortcut(combination, action);
    }

    /**
     * Register a Ctrl+Shift+Key shortcut
     */
    public void registerCtrlShiftShortcut(KeyCode key, Runnable action) {
        KeyCombination combination = new KeyCodeCombination(key,
                KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        registerShortcut(combination, action);
    }

    // =====================================
    // SHORTCUT SETUP HELPERS
    // =====================================

    /**
     * Setup default Scenory shortcuts
     */
    public void setupDefaultShortcuts(ShortcutCallbacks callbacks) {
        System.out.println("⌨️ Setting up default keyboard shortcuts...");

        // ===== UNDO/REDO =====
        registerCtrlShortcut(KeyCode.Z, () -> {
            System.out.println("⌨️ Ctrl+Z pressed");
            callbacks.undo();
        });

        registerCtrlShortcut(KeyCode.Y, () -> {
            System.out.println("⌨️ Ctrl+Y pressed");
            callbacks.redo();
        });

        registerCtrlShiftShortcut(KeyCode.Z, () -> {
            System.out.println("⌨️ Ctrl+Shift+Z pressed");
            callbacks.redo();
        });

        // ===== TOOL SHORTCUTS =====
        registerShortcut(KeyCode.P, () -> {
            System.out.println("⌨️ P - Pen tool");
            callbacks.selectPenTool();
        });

        registerShortcut(KeyCode.B, () -> {
            System.out.println("⌨️ B - Brush tool");
            callbacks.selectBrushTool();
        });

        registerShortcut(KeyCode.E, () -> {
            System.out.println("⌨️ E - Eraser tool");
            callbacks.selectEraserTool();
        });

        registerShortcut(KeyCode.R, () -> {
            System.out.println("⌨️ R - Rectangle tool");
            callbacks.selectRectangleTool();
        });

        registerShortcut(KeyCode.C, () -> {
            System.out.println("⌨️ C - Circle tool");
            callbacks.selectCircleTool();
        });

        registerShortcut(KeyCode.L, () -> {
            System.out.println("⌨️ L - Line tool");
            callbacks.selectLineTool();
        });

        registerShortcut(KeyCode.T, () -> {
            System.out.println("⌨️ T - Text tool");
            callbacks.selectTextTool();
        });

        // ===== PROJECT SHORTCUTS =====
        registerCtrlShortcut(KeyCode.N, () -> {
            System.out.println("⌨️ Ctrl+N - New project");
            callbacks.newProject();
        });

        registerCtrlShortcut(KeyCode.O, () -> {
            System.out.println("⌨️ Ctrl+O - Open project");
            callbacks.openProject();
        });

        registerCtrlShortcut(KeyCode.S, () -> {
            System.out.println("⌨️ Ctrl+S - Save project");
            callbacks.saveProject();
        });

        // ===== PANEL NAVIGATION =====
        registerShortcut(KeyCode.LEFT, () -> {
            System.out.println("⌨️ Left Arrow - Previous panel");
            callbacks.previousPanel();
        });

        registerShortcut(KeyCode.RIGHT, () -> {
            System.out.println("⌨️ Right Arrow - Next panel");
            callbacks.nextPanel();
        });

        // ===== SCENE/PANEL SHORTCUTS =====
        registerCtrlShortcut(KeyCode.ENTER, () -> {
            System.out.println("⌨️ Ctrl+Enter - New panel");
            callbacks.newPanel();
        });

        registerCtrlShortcut(KeyCode.D, () -> {
            System.out.println("⌨️ Ctrl+D - Duplicate panel");
            callbacks.duplicatePanel();
        });

        registerShortcut(KeyCode.DELETE, () -> {
            System.out.println("⌨️ Delete - Delete panel");
            callbacks.deletePanel();
        });

        // ===== VIEW SHORTCUTS =====
        registerShortcut(KeyCode.SPACE, () -> {
            System.out.println("⌨️ Space - Toggle panels");
            callbacks.togglePanels();
        });

        registerShortcut(KeyCode.F1, () -> {
            System.out.println("⌨️ F1 - Toggle tools panel");
            callbacks.toggleToolsPanel();
        });

        registerShortcut(KeyCode.F2, () -> {
            System.out.println("⌨️ F2 - Toggle structure panel");
            callbacks.toggleStructurePanel();
        });

        // ===== ZOOM SHORTCUTS =====
        registerCtrlShortcut(KeyCode.PLUS, () -> {
            System.out.println("⌨️ Ctrl++ - Zoom in");
            callbacks.zoomIn();
        });

        registerCtrlShortcut(KeyCode.EQUALS, () -> { // For keyboards without numpad
            System.out.println("⌨️ Ctrl+= - Zoom in");
            callbacks.zoomIn();
        });

        registerCtrlShortcut(KeyCode.MINUS, () -> {
            System.out.println("⌨️ Ctrl+- - Zoom out");
            callbacks.zoomOut();
        });

        registerCtrlShortcut(KeyCode.DIGIT0, () -> {
            System.out.println("⌨️ Ctrl+0 - Reset zoom");
            callbacks.resetZoom();
        });

        System.out.println("✅ Default shortcuts registered");
    }

    // =====================================
    // MANAGEMENT METHODS
    // =====================================

    /**
     * Remove a shortcut
     */
    public void removeShortcut(KeyCombination combination) {
        shortcuts.remove(combination);
    }

    /**
     * Remove a single key shortcut
     */
    public void removeShortcut(KeyCode key) {
        singleKeyShortcuts.remove(key);
    }

    /**
     * Clear all shortcuts
     */
    public void clearAllShortcuts() {
        shortcuts.clear();
        singleKeyShortcuts.clear();
    }

    /**
     * Print all registered shortcuts
     */
    public void printShortcuts() {
        System.out.println("⌨️ Registered Shortcuts:");
        System.out.println("  Combination shortcuts: " + shortcuts.size());
        shortcuts.entrySet().forEach(entry ->
                System.out.println("    " + entry.getKey().getDisplayText()));

        System.out.println("  Single key shortcuts: " + singleKeyShortcuts.size());
        singleKeyShortcuts.keySet().forEach(key ->
                System.out.println("    " + key.getName()));
    }

    // =====================================
    // CALLBACK INTERFACE
    // =====================================

    /**
     * Interface for shortcut callbacks
     * Implement this in MainController to handle shortcuts
     */
    public interface ShortcutCallbacks {
        // Undo/Redo
        void undo();
        void redo();

        // Tools
        void selectPenTool();
        void selectBrushTool();
        void selectEraserTool();
        void selectRectangleTool();
        void selectCircleTool();
        void selectLineTool();
        void selectTextTool();

        // Project
        void newProject();
        void openProject();
        void saveProject();

        // Navigation
        void previousPanel();
        void nextPanel();

        // Scene/Panel
        void newPanel();
        void duplicatePanel();
        void deletePanel();

        // View
        void togglePanels();
        void toggleToolsPanel();
        void toggleStructurePanel();

        // Zoom
        void zoomIn();
        void zoomOut();
        void resetZoom();
    }
}