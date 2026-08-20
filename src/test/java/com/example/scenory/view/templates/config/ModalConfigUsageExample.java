package com.example.scenory.view.templates.config;

/**
 * Usage examples demonstrating how to use ModalConfig with the builder pattern.
 * <p>
 * This class provides practical examples for developers using the ModalConfig API.
 * These examples are not executable tests but serve as documentation.
 * </p>
 */
public class ModalConfigUsageExample {
    
    /**
     * Example 1: Creating a simple modal with default values
     */
    public static void exampleSimpleModal() {
        ModalConfig config = ModalConfig.builder()
                .title("Welcome")
                .build();
        
        // Results in:
        // - Title: "Welcome"
        // - Width: 600 (default)
        // - Height: 400 (default)
        // - Resizable: true (default)
        // - Closeable: true (default)
    }
    
    /**
     * Example 2: Creating a custom-sized modal
     */
    public static void exampleCustomSizeModal() {
        ModalConfig config = ModalConfig.builder()
                .title("Settings")
                .dimensions(800, 600)
                .minDimensions(640, 480)
                .build();
        
        // Results in:
        // - Title: "Settings"
        // - Width: 800
        // - Height: 600
        // - Min Width: 640
        // - Min Height: 480
        // - Resizable: true (default)
    }
    
    /**
     * Example 3: Creating a non-resizable modal with custom styling
     */
    public static void exampleNonResizableStyledModal() {
        ModalConfig config = ModalConfig.builder()
                .title("About")
                .dimensions(500, 400)
                .resizable(false)
                .styleClasses("about-modal", "dark-theme")
                .build();
        
        // Results in:
        // - Title: "About"
        // - Width: 500
        // - Height: 400
        // - Resizable: false
        // - Style classes: ["about-modal", "dark-theme"]
    }
    
    /**
     * Example 4: Creating a modal with close callback
     */
    public static void exampleModalWithCallback() {
        ModalConfig config = ModalConfig.builder()
                .title("Confirm Action")
                .dimensions(400, 200)
                .onClose(v -> {
                    System.out.println("Modal was closed");
                    // Perform cleanup or save state
                })
                .build();
        
        // When the modal is closed, the callback will be invoked
    }
    
    /**
     * Example 5: Creating a fully configured modal for a complex dialog
     */
    public static void exampleCompleteConfiguration() {
        ModalConfig config = ModalConfig.builder()
                .title("Advanced Settings")
                .dimensions(1024, 768)
                .minDimensions(800, 600)
                .resizable(true)
                .closeable(true)
                .styleClasses("settings-modal", "large", "scrollable")
                .onClose(v -> {
                    System.out.println("Settings dialog closed");
                    // Save settings or perform validation
                })
                .build();
        
        // This creates a fully configured modal with:
        // - Custom dimensions and constraints
        // - Multiple style classes for complex styling
        // - Close callback for cleanup
    }
    
    /**
     * Example 6: Creating a modal for the existing RichTextModal pattern
     */
    public static void exampleRichTextModalPattern() {
        // This demonstrates how ModalConfig could replace manual stage setup
        // in the existing RichTextModal class
        ModalConfig config = ModalConfig.builder()
                .title("Edit Panel Description - Panel Name")
                .dimensions(900, 700)
                .minDimensions(800, 600)
                .resizable(true)
                .onClose(v -> System.out.println("Rich text editor closed"))
                .build();
        
        // The ModalTemplate class (to be implemented in task 1.4) will use
        // this configuration to create the modal stage
    }
}
