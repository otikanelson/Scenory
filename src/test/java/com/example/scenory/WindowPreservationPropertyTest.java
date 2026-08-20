package com.example.scenory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

/**
 * Preservation Property-Based Tests for JavaFX Window Positioning
 * 
 * **Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.5**
 * 
 * IMPORTANT: These tests verify that non-buggy window operations are preserved
 * by the fix. They should PASS on both UNFIXED and FIXED code.
 * 
 * GOAL: Establish baseline behavior for manual window operations that must
 * remain unchanged after implementing the bug fix.
 * 
 * Property 2: Preservation - Non-Startup Window Operations Preserved
 * 
 * These property-based tests generate many test cases automatically across
 * the input domain to ensure strong guarantees that behavior is unchanged.
 */
public class WindowPreservationPropertyTest {

    static {
        // Initialize JavaFX toolkit at class loading time
        try {
            System.out.println("Static initializer: Starting JavaFX toolkit initialization...");
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<Boolean> success = new AtomicReference<>(false);
            
            new Thread(() -> {
                try {
                    Platform.startup(() -> {
                        System.out.println("JavaFX toolkit initialized via Platform.startup");
                        success.set(true);
                        latch.countDown();
                    });
                } catch (IllegalStateException e) {
                    // Toolkit already initialized
                    System.out.println("JavaFX toolkit already initialized: " + e.getMessage());
                    success.set(true);
                    latch.countDown();
                }
            }).start();
            
            if (latch.await(15, TimeUnit.SECONDS) && success.get()) {
                Thread.sleep(500); // Give toolkit time to stabilize
                System.out.println("JavaFX toolkit ready for testing");
            } else {
                System.err.println("Failed to initialize JavaFX toolkit in static initializer");
            }
        } catch (Exception e) {
            System.err.println("Exception during JavaFX toolkit initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean javafxInitialized = true; // Assume initialized after static block
    
    /**
     * Initialize JavaFX toolkit once before all tests
     */
    @BeforeAll
    public static void initJavaFX() throws Exception {
        System.out.println("@BeforeAll: JavaFX should already be initialized from static block");
        // Additional safety check - wait a bit to ensure toolkit is ready
        Thread.sleep(200);
    }

    /**
     * Property 2a: Manual Window Move Preservation
     * 
     * For all valid window positions (X >= 0, Y >= 0) within reasonable screen bounds,
     * manually moving window to (X, Y) in normal state results in window remaining at (X, Y).
     * 
     * This test verifies that manual window positioning is preserved correctly.
     * 
     * **Validates: Requirements 3.1, 3.2**
     */
    @Property(tries = 10)
    void manualWindowMoveIsPreserved(
            @ForAll @IntRange(min = 0, max = 1000) int targetX,
            @ForAll @IntRange(min = 0, max = 500) int targetY
    ) throws Exception {
        System.out.println("\n=== Property 2a: Manual Window Move Test ===");
        System.out.println("Testing position: X=" + targetX + ", Y=" + targetY);
        
        AtomicReference<Double> actualX = new AtomicReference<>(0.0);
        AtomicReference<Double> actualY = new AtomicReference<>(0.0);
        CountDownLatch testLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = createTestStage();
                
                // Start in NORMAL state (not maximized) - this is non-buggy operation
                stage.setMaximized(false);
                stage.show();
                
                // Wait for window to be fully initialized
                Thread.sleep(300);
                
                // Manually move window to target position
                stage.setX(targetX);
                stage.setY(targetY);
                
                // Wait for position to be applied
                Thread.sleep(300);
                
                // Verify position is preserved
                double x = stage.getX();
                double y = stage.getY();
                actualX.set(x);
                actualY.set(y);
                
                System.out.println("Set position: X=" + targetX + ", Y=" + targetY);
                System.out.println("Actual position: X=" + x + ", Y=" + y);
                
                stage.close();
                testLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                testLatch.countDown();
            }
        });

        assertTrue(testLatch.await(10, TimeUnit.SECONDS), "Test did not complete in time");
        
        // Allow small tolerance for window manager positioning
        double tolerance = 10.0;
        double xDiff = Math.abs(actualX.get() - targetX);
        double yDiff = Math.abs(actualY.get() - targetY);
        
        assertTrue(xDiff <= tolerance,
            "Manual window move X position should be preserved. " +
            "Expected: " + targetX + ", Actual: " + actualX.get() + ", Diff: " + xDiff);
        
        assertTrue(yDiff <= tolerance,
            "Manual window move Y position should be preserved. " +
            "Expected: " + targetY + ", Actual: " + actualY.get() + ", Diff: " + yDiff);
    }

    /**
     * Property 2b: Manual Window Resize Preservation
     * 
     * For all valid window sizes (W >= minWidth, H >= minHeight), manually resizing
     * window to (W, H) in normal state results in window remaining at (W, H).
     * 
     * This test verifies that manual window sizing is preserved correctly.
     * 
     * **Validates: Requirements 3.1, 3.2, 3.5**
     */
    @Property(tries = 10)
    void manualWindowResizeIsPreserved(
            @ForAll @IntRange(min = 1000, max = 1600) int targetWidth,
            @ForAll @IntRange(min = 750, max = 1000) int targetHeight
    ) throws Exception {
        System.out.println("\n=== Property 2b: Manual Window Resize Test ===");
        System.out.println("Testing size: W=" + targetWidth + ", H=" + targetHeight);
        
        AtomicReference<Double> actualWidth = new AtomicReference<>(0.0);
        AtomicReference<Double> actualHeight = new AtomicReference<>(0.0);
        CountDownLatch testLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = createTestStage();
                
                // Start in NORMAL state (not maximized) - this is non-buggy operation
                stage.setMaximized(false);
                stage.show();
                
                // Wait for window to be fully initialized
                Thread.sleep(300);
                
                // Manually resize window to target size
                stage.setWidth(targetWidth);
                stage.setHeight(targetHeight);
                
                // Wait for size to be applied
                Thread.sleep(300);
                
                // Verify size is preserved
                double w = stage.getWidth();
                double h = stage.getHeight();
                actualWidth.set(w);
                actualHeight.set(h);
                
                System.out.println("Set size: W=" + targetWidth + ", H=" + targetHeight);
                System.out.println("Actual size: W=" + w + ", H=" + h);
                
                stage.close();
                testLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                testLatch.countDown();
            }
        });

        assertTrue(testLatch.await(10, TimeUnit.SECONDS), "Test did not complete in time");
        
        // Allow small tolerance for window manager sizing
        double tolerance = 15.0;
        double wDiff = Math.abs(actualWidth.get() - targetWidth);
        double hDiff = Math.abs(actualHeight.get() - targetHeight);
        
        assertTrue(wDiff <= tolerance,
            "Manual window resize width should be preserved. " +
            "Expected: " + targetWidth + ", Actual: " + actualWidth.get() + ", Diff: " + wDiff);
        
        assertTrue(hDiff <= tolerance,
            "Manual window resize height should be preserved. " +
            "Expected: " + targetHeight + ", Actual: " + actualHeight.get() + ", Diff: " + hDiff);
    }

    /**
     * Property 2c: Maximize/Restore From Normal State Preservation
     * 
     * For all valid starting positions, maximize followed by restore returns window
     * to starting position. This verifies that the normal maximize/restore cycle
     * (after valid bounds are established) works correctly.
     * 
     * This is different from the bug condition - here we start in NORMAL state,
     * manually position the window, THEN maximize/restore.
     * 
     * **Validates: Requirements 3.1, 3.2, 3.3**
     */
    @Property(tries = 10)
    void maximizeRestoreFromNormalStatePreservesPosition(
            @ForAll @IntRange(min = 100, max = 800) int startX,
            @ForAll @IntRange(min = 100, max = 400) int startY
    ) throws Exception {
        System.out.println("\n=== Property 2c: Maximize/Restore From Normal State Test ===");
        System.out.println("Starting position: X=" + startX + ", Y=" + startY);
        
        AtomicReference<Double> restoredX = new AtomicReference<>(0.0);
        AtomicReference<Double> restoredY = new AtomicReference<>(0.0);
        CountDownLatch testLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = createTestStage();
                
                // Start in NORMAL state - this is the key difference from bug condition
                stage.setMaximized(false);
                stage.show();
                
                // Wait for window to be fully initialized
                Thread.sleep(300);
                
                // Manually set position (establishing valid restored bounds)
                stage.setX(startX);
                stage.setY(startY);
                
                // Wait for position to be applied
                Thread.sleep(300);
                
                System.out.println("Set starting position: X=" + startX + ", Y=" + startY);
                System.out.println("Actual starting position: X=" + stage.getX() + ", Y=" + stage.getY());
                
                // Now maximize
                stage.setMaximized(true);
                Thread.sleep(300);
                System.out.println("Maximized window");
                
                // Now restore
                stage.setMaximized(false);
                Thread.sleep(300);
                
                // Verify position is restored to starting position
                double x = stage.getX();
                double y = stage.getY();
                restoredX.set(x);
                restoredY.set(y);
                
                System.out.println("Restored position: X=" + x + ", Y=" + y);
                
                stage.close();
                testLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                testLatch.countDown();
            }
        });

        assertTrue(testLatch.await(15, TimeUnit.SECONDS), "Test did not complete in time");
        
        // Allow tolerance for window manager positioning
        double tolerance = 20.0;
        double xDiff = Math.abs(restoredX.get() - startX);
        double yDiff = Math.abs(restoredY.get() - startY);
        
        assertTrue(xDiff <= tolerance,
            "Maximize/restore should return to starting X position. " +
            "Expected: " + startX + ", Actual: " + restoredX.get() + ", Diff: " + xDiff);
        
        assertTrue(yDiff <= tolerance,
            "Maximize/restore should return to starting Y position. " +
            "Expected: " + startY + ", Actual: " + restoredY.get() + ", Diff: " + yDiff);
    }

    /**
     * Property 2d: Minimize/Restore Is Identity Operation For Position
     * 
     * For all valid window positions, minimize followed by restore should not
     * change the window position. This verifies minimize/restore is distinct
     * from maximize/restore and does not suffer from the same bug.
     * 
     * **Validates: Requirements 3.1, 3.2**
     */
    @Property(tries = 10)
    void minimizeRestorePreservesPosition(
            @ForAll @IntRange(min = 50, max = 700) int posX,
            @ForAll @IntRange(min = 50, max = 300) int posY
    ) throws Exception {
        System.out.println("\n=== Property 2d: Minimize/Restore Preservation Test ===");
        System.out.println("Testing position: X=" + posX + ", Y=" + posY);
        
        AtomicReference<Double> beforeX = new AtomicReference<>(0.0);
        AtomicReference<Double> beforeY = new AtomicReference<>(0.0);
        AtomicReference<Double> afterX = new AtomicReference<>(0.0);
        AtomicReference<Double> afterY = new AtomicReference<>(0.0);
        CountDownLatch testLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = createTestStage();
                
                // Start in NORMAL state (not maximized)
                stage.setMaximized(false);
                stage.show();
                
                // Wait for window to be fully initialized
                Thread.sleep(300);
                
                // Set position
                stage.setX(posX);
                stage.setY(posY);
                
                // Wait for position to be applied
                Thread.sleep(300);
                
                // Record position before minimize
                double x1 = stage.getX();
                double y1 = stage.getY();
                beforeX.set(x1);
                beforeY.set(y1);
                
                System.out.println("Position before minimize: X=" + x1 + ", Y=" + y1);
                
                // Minimize window
                stage.setIconified(true);
                Thread.sleep(300);
                System.out.println("Window minimized");
                
                // Restore from minimize
                stage.setIconified(false);
                Thread.sleep(300);
                
                // Record position after restore
                double x2 = stage.getX();
                double y2 = stage.getY();
                afterX.set(x2);
                afterY.set(y2);
                
                System.out.println("Position after restore: X=" + x2 + ", Y=" + y2);
                
                stage.close();
                testLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                testLatch.countDown();
            }
        });

        assertTrue(testLatch.await(15, TimeUnit.SECONDS), "Test did not complete in time");
        
        // Minimize/restore should be identity operation - position unchanged
        double tolerance = 5.0;
        double xDiff = Math.abs(afterX.get() - beforeX.get());
        double yDiff = Math.abs(afterY.get() - beforeY.get());
        
        assertTrue(xDiff <= tolerance,
            "Minimize/restore should preserve X position (identity operation). " +
            "Before: " + beforeX.get() + ", After: " + afterX.get() + ", Diff: " + xDiff);
        
        assertTrue(yDiff <= tolerance,
            "Minimize/restore should preserve Y position (identity operation). " +
            "Before: " + beforeY.get() + ", After: " + afterY.get() + ", Diff: " + yDiff);
    }

    /**
     * Property 2e: Subsequent Maximize/Restore Cycles Use Same Restored Position
     * 
     * After establishing valid restored bounds, subsequent maximize/restore cycles
     * should use the same restored position consistently. This verifies that the
     * fix doesn't break the multi-cycle behavior.
     * 
     * **Validates: Requirements 3.1, 3.2, 3.3**
     */
    @Property(tries = 10)
    void subsequentMaximizeRestoreCyclesAreStable(
            @ForAll @IntRange(min = 150, max = 600) int startX,
            @ForAll @IntRange(min = 150, max = 350) int startY
    ) throws Exception {
        System.out.println("\n=== Property 2e: Subsequent Maximize/Restore Stability Test ===");
        System.out.println("Starting position: X=" + startX + ", Y=" + startY);
        
        AtomicReference<Double> firstRestoreX = new AtomicReference<>(0.0);
        AtomicReference<Double> firstRestoreY = new AtomicReference<>(0.0);
        AtomicReference<Double> secondRestoreX = new AtomicReference<>(0.0);
        AtomicReference<Double> secondRestoreY = new AtomicReference<>(0.0);
        AtomicReference<Double> thirdRestoreX = new AtomicReference<>(0.0);
        AtomicReference<Double> thirdRestoreY = new AtomicReference<>(0.0);
        CountDownLatch testLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = createTestStage();
                
                // Start in NORMAL state
                stage.setMaximized(false);
                stage.show();
                
                // Wait for window to be fully initialized
                Thread.sleep(300);
                
                // Establish starting position (valid restored bounds)
                stage.setX(startX);
                stage.setY(startY);
                Thread.sleep(300);
                
                System.out.println("Established starting position: X=" + stage.getX() + ", Y=" + stage.getY());
                
                // First maximize/restore cycle
                System.out.println("=== First Cycle ===");
                stage.setMaximized(true);
                Thread.sleep(300);
                stage.setMaximized(false);
                Thread.sleep(300);
                
                double x1 = stage.getX();
                double y1 = stage.getY();
                firstRestoreX.set(x1);
                firstRestoreY.set(y1);
                System.out.println("First restore position: X=" + x1 + ", Y=" + y1);
                
                // Second maximize/restore cycle
                System.out.println("=== Second Cycle ===");
                stage.setMaximized(true);
                Thread.sleep(300);
                stage.setMaximized(false);
                Thread.sleep(300);
                
                double x2 = stage.getX();
                double y2 = stage.getY();
                secondRestoreX.set(x2);
                secondRestoreY.set(y2);
                System.out.println("Second restore position: X=" + x2 + ", Y=" + y2);
                
                // Third maximize/restore cycle
                System.out.println("=== Third Cycle ===");
                stage.setMaximized(true);
                Thread.sleep(300);
                stage.setMaximized(false);
                Thread.sleep(300);
                
                double x3 = stage.getX();
                double y3 = stage.getY();
                thirdRestoreX.set(x3);
                thirdRestoreY.set(y3);
                System.out.println("Third restore position: X=" + x3 + ", Y=" + y3);
                
                stage.close();
                testLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                testLatch.countDown();
            }
        });

        assertTrue(testLatch.await(25, TimeUnit.SECONDS), "Test did not complete in time");
        
        // All three restore operations should yield same position (stable behavior)
        double tolerance = 10.0;
        
        double xDiff12 = Math.abs(secondRestoreX.get() - firstRestoreX.get());
        double yDiff12 = Math.abs(secondRestoreY.get() - firstRestoreY.get());
        double xDiff23 = Math.abs(thirdRestoreX.get() - secondRestoreX.get());
        double yDiff23 = Math.abs(thirdRestoreY.get() - secondRestoreY.get());
        
        assertTrue(xDiff12 <= tolerance,
            "Second restore X should match first restore X (stable behavior). " +
            "First: " + firstRestoreX.get() + ", Second: " + secondRestoreX.get() + ", Diff: " + xDiff12);
        
        assertTrue(yDiff12 <= tolerance,
            "Second restore Y should match first restore Y (stable behavior). " +
            "First: " + firstRestoreY.get() + ", Second: " + secondRestoreY.get() + ", Diff: " + yDiff12);
        
        assertTrue(xDiff23 <= tolerance,
            "Third restore X should match second restore X (stable behavior). " +
            "Second: " + secondRestoreX.get() + ", Third: " + thirdRestoreX.get() + ", Diff: " + xDiff23);
        
        assertTrue(yDiff23 <= tolerance,
            "Third restore Y should match second restore Y (stable behavior). " +
            "Second: " + secondRestoreY.get() + ", Third: " + thirdRestoreY.get() + ", Diff: " + yDiff23);
    }

    /**
     * Helper method to create a test stage with welcome view loaded.
     * This replicates the basic ScenoryApplication setup without the maximize call.
     */
    private Stage createTestStage() throws Exception {
        Stage stage = new Stage();
        
        FXMLLoader fxmlLoader = new FXMLLoader(
            ScenoryApplication.class.getResource("welcome-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // Load custom fonts
        loadCustomFonts();

        // Load CSS
        try {
            String cssFile = ScenoryApplication.class.getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(cssFile);
        } catch (Exception e) {
            System.out.println("⚠️ CSS file not found in test");
        }

        stage.setScene(scene);
        stage.setTitle("Scenory - Preservation Test");
        stage.setMinWidth(1000);
        stage.setMinHeight(750);
        
        // Do NOT call setMaximized(true) - we're testing normal window operations
        
        return stage;
    }

    /**
     * Helper method to load custom fonts (same as ScenoryApplication)
     */
    private void loadCustomFonts() {
        try {
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Inter/static/Inter-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Inter/static/Inter-Bold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Outfit/static/Outfit-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Outfit/static/Outfit-ExtraBold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-Medium.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-SemiBold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/Space_Grotesk/static/SpaceGrotesk-Bold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/com/example/scenory/fonts/JetBrainsMono/static/JetBrainsMono-Regular.ttf"), 14);
        } catch (Exception e) {
            System.out.println("⚠️ Could not load custom fonts in test: " + e.getMessage());
        }
    }
}
