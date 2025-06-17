package com.example.scenory.utils;

import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

public class CanvasPersistence {

    /**
     * Save canvas content as byte array (PNG format)
     */
    public static byte[] saveCanvasToBytes(Canvas canvas) {
        try {
            if (canvas == null) {
                return null;
            }

            // Create snapshot of canvas
            WritableImage snapshot = canvas.snapshot(null, null);

            // Convert to BufferedImage
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            // Convert to byte array as PNG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean success = ImageIO.write(bufferedImage, "PNG", baos);

            if (success) {
                byte[] result = baos.toByteArray();
                System.out.println("Canvas saved: " + result.length + " bytes");
                return result;
            } else {
                System.err.println("Failed to write canvas image");
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error saving canvas: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Restore canvas content from byte array
     */
    public static boolean restoreCanvasFromBytes(Canvas canvas, byte[] imageData) {
        try {
            if (canvas == null || !isValidImageData(imageData)) {
                return false;
            }

            // Convert byte array to JavaFX Image
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            Image image = new Image(bais);

            if (image.isError()) {
                System.err.println("Error loading image from bytes");
                return false;
            }

            // Get graphics context
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Clear canvas first
            clearCanvas(canvas);

            // Draw the restored image
            gc.drawImage(image, 0, 0);

            System.out.println("Canvas restored: " + imageData.length + " bytes");
            return true;

        } catch (Exception e) {
            System.err.println("Error restoring canvas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clear canvas with white background
     */
    public static void clearCanvas(Canvas canvas) {
        if (canvas instanceof DrawingCanvas) {
            ((DrawingCanvas) canvas).clearCanvas();
        } else {
            // Fallback for regular Canvas
            if (canvas == null) return;

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2.0);
        }
    }
    /**
     * Check if image data is valid
     */
    public static boolean isValidImageData(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            return false;
        }

        // Quick validation - check if it starts with PNG header
        if (imageData.length >= 8) {
            // PNG signature: 89 50 4E 47 0D 0A 1A 0A
            return imageData[0] == (byte)0x89 &&
                    imageData[1] == 0x50 &&
                    imageData[2] == 0x4E &&
                    imageData[3] == 0x47;
        }

        return true; // Assume valid for other cases
    }

    /**
     * Check if canvas has any drawing (FIXED VERSION)
     */
    public static boolean canvasHasDrawing(Canvas canvas) {
        try {
            WritableImage snapshot = canvas.snapshot(null, null);

            int width = (int) snapshot.getWidth();
            int height = (int) snapshot.getHeight();

            // Sample more points and be more lenient with color detection
            int sampleCount = 0;
            int nonWhiteCount = 0;

            // Check a grid of points across the canvas
            for (int x = 0; x < width; x += 20) { // Every 20 pixels
                for (int y = 0; y < height; y += 20) {
                    if (x < width && y < height) {
                        Color color = snapshot.getPixelReader().getColor(x, y);
                        sampleCount++;

                        // More lenient check - any pixel that's noticeably different from pure white
                        if (color.getRed() < 0.95 || color.getGreen() < 0.95 || color.getBlue() < 0.95) {
                            nonWhiteCount++;
                        }
                    }
                }
            }

            // If more than 0.1% of sampled pixels are non-white, consider it has drawing
            double nonWhitePercentage = (double) nonWhiteCount / sampleCount;
            boolean hasDrawing = nonWhitePercentage > 0.001; // 0.1% threshold

            System.out.println("🔍 Canvas analysis: " + nonWhiteCount + "/" + sampleCount +
                    " non-white pixels (" + String.format("%.2f", nonWhitePercentage * 100) +
                    "%) - HasDrawing: " + hasDrawing);

            return hasDrawing;

        } catch (Exception e) {
            System.err.println("Error checking canvas content: " + e.getMessage());
            return true; // Assume it has drawing if we can't check
        }
    }

    /**
     * Alternative method: Always save canvas content regardless of content detection
     */
    public static boolean canvasHasDrawingSimple(Canvas canvas) {
        // For debugging: always return true to force saving
        return true;
    }

    /**
     * Get size information about canvas data
     */
    public static String getCanvasDataInfo(byte[] imageData) {
        if (!isValidImageData(imageData)) {
            return "No canvas data";
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bais);
            if (image != null) {
                return String.format("Canvas: %dx%d pixels (%d bytes)",
                        image.getWidth(), image.getHeight(), imageData.length);
            }
        } catch (Exception e) {
            // Ignore exception, fall through to default
        }

        return String.format("Canvas data: %d bytes", imageData.length);
    }
}