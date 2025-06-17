package com.example.scenory.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ThumbnailGenerator {

    public static final int THUMBNAIL_WIDTH = 120;
    public static final int THUMBNAIL_HEIGHT = 90;

    /**
     * Generate thumbnail from canvas
     */
    public static byte[] generateThumbnail(Canvas canvas) {
        return generateThumbnail(canvas, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
    }

    /**
     * Generate thumbnail with custom size
     */
    public static byte[] generateThumbnail(Canvas canvas, int width, int height) {
        try {
            // Create snapshot of canvas
            WritableImage snapshot = canvas.snapshot(null, null);

            // Convert JavaFX image to BufferedImage
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            // Resize to thumbnail size
            BufferedImage thumbnail = resizeImage(bufferedImage, width, height);

            // Convert to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "PNG", baos);

            return baos.toByteArray();

        } catch (Exception e) {
            System.err.println("Error generating thumbnail: " + e.getMessage());
            e.printStackTrace();
            return createPlaceholderThumbnail(width, height);
        }
    }

    /**
     * Convert byte array back to JavaFX Image
     */
    public static Image bytesToImage(byte[] imageBytes) {
        try {
            if (imageBytes == null || imageBytes.length == 0) {
                return null;
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            return new Image(bais);

        } catch (Exception e) {
            System.err.println("Error converting bytes to image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Resize image with high quality
     */
    private static BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();

        // High quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        // Calculate aspect ratio to maintain proportions
        double originalRatio = (double) original.getWidth() / original.getHeight();
        double targetRatio = (double) width / height;

        int drawWidth, drawHeight, drawX, drawY;

        if (originalRatio > targetRatio) {
            // Original is wider, fit to width
            drawWidth = width;
            drawHeight = (int) (width / originalRatio);
            drawX = 0;
            drawY = (height - drawHeight) / 2;
        } else {
            // Original is taller, fit to height
            drawWidth = (int) (height * originalRatio);
            drawHeight = height;
            drawX = (width - drawWidth) / 2;
            drawY = 0;
        }

        // Fill background with white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw the resized image
        g2d.drawImage(original, drawX, drawY, drawWidth, drawHeight, null);

        g2d.dispose();
        return resized;
    }

    /**
     * Create placeholder thumbnail when generation fails
     */
    private static byte[] createPlaceholderThumbnail(int width, int height) {
        try {
            BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = placeholder.createGraphics();

            // Enable antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Gray background
            g2d.setColor(new Color(128, 128, 128));
            g2d.fillRect(0, 0, width, height);

            // Darker border
            g2d.setColor(new Color(64, 64, 64));
            g2d.drawRect(0, 0, width - 1, height - 1);

            // "No Preview" text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));

            String text = "No Preview";
            java.awt.FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            int x = (width - textWidth) / 2;
            int y = (height + textHeight) / 2 - fm.getDescent();

            g2d.drawString(text, x, y);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(placeholder, "PNG", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            System.err.println("Error creating placeholder thumbnail: " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Check if thumbnail data is valid
     */
    public static boolean isValidThumbnail(byte[] thumbnailData) {
        return thumbnailData != null && thumbnailData.length > 0;
    }

    /**
     * Get thumbnail dimensions info
     */
    public static String getThumbnailInfo(byte[] thumbnailData) {
        if (!isValidThumbnail(thumbnailData)) {
            return "No thumbnail data";
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(thumbnailData);
            BufferedImage image = ImageIO.read(bais);
            if (image != null) {
                return String.format("%dx%d pixels, %d bytes",
                        image.getWidth(), image.getHeight(), thumbnailData.length);
            }
        } catch (Exception e) {
            // Ignore
        }

        return String.format("%d bytes", thumbnailData.length);
    }

    /**
     * Create thumbnail directly from BufferedImage (utility method)
     */
    public static byte[] createThumbnailFromBufferedImage(BufferedImage image, int width, int height) {
        try {
            BufferedImage thumbnail = resizeImage(image, width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "PNG", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            System.err.println("Error creating thumbnail from BufferedImage: " + e.getMessage());
            return createPlaceholderThumbnail(width, height);
        }
    }
}