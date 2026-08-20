package com.example.scenory.view.templates;

import java.util.Objects;

import javafx.scene.image.Image;

/**
 * Data model representing a frame item in the scene panel.
 * <p>
 * This class encapsulates the essential information for displaying a frame
 * in the scene panel, including a unique frame identifier, thumbnail image,
 * and display label.
 * </p>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * Image thumbnail = new Image("file:path/to/image.png");
 * FrameItem frame = new FrameItem("frame-001", thumbnail, "Frame 1");
 * 
 * // Update thumbnail
 * frame.setThumbnail(newThumbnail);
 * 
 * // Update label
 * frame.setLabel("Scene 1 - Frame 1");
 * }</pre>
 * 
 * <h3>Validated Requirements:</h3>
 * <ul>
 *   <li><strong>Requirement 3.3</strong>: THE Scene_Panel Template SHALL support adding frame items with thumbnail image, label, and selection callback</li>
 *   <li><strong>Requirement 3.4</strong>: WHEN a frame item is added, THE Scene_Panel Template SHALL display the thumbnail and label in the panel</li>
 * </ul>
 * 
 * @see Image
 */
public class FrameItem {
    
    /**
     * The unique identifier for this frame. Cannot be null.
     */
    private final String frameId;
    
    /**
     * The thumbnail image for this frame. May be null if no thumbnail is available.
     */
    private Image thumbnail;
    
    /**
     * The display label for this frame. May be null or empty.
     */
    private String label;
    
    /**
     * Constructs a new FrameItem with the specified frame ID, thumbnail, and label.
     * <p>
     * The frame ID is immutable after construction and must not be null.
     * The thumbnail and label can be updated using their respective setter methods.
     * </p>
     * 
     * @param frameId The unique identifier for this frame (must not be null)
     * @param thumbnail The thumbnail image for this frame (may be null)
     * @param label The display label for this frame (may be null)
     * @throws NullPointerException if frameId is null
     */
    public FrameItem(String frameId, Image thumbnail, String label) {
        this.frameId = Objects.requireNonNull(frameId, "Frame ID cannot be null");
        this.thumbnail = thumbnail;
        this.label = label;
    }
    
    /**
     * Returns the unique frame identifier.
     * <p>
     * The frame ID is immutable and is guaranteed to be non-null.
     * </p>
     * 
     * @return The frame ID (never null)
     */
    public String getFrameId() {
        return frameId;
    }
    
    /**
     * Returns the thumbnail image for this frame.
     * 
     * @return The thumbnail image, or null if no thumbnail is set
     */
    public Image getThumbnail() {
        return thumbnail;
    }
    
    /**
     * Sets the thumbnail image for this frame.
     * <p>
     * The thumbnail may be null if no image is available.
     * </p>
     * 
     * @param thumbnail The new thumbnail image (may be null)
     */
    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    /**
     * Returns the display label for this frame.
     * 
     * @return The label, or null if no label is set
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Sets the display label for this frame.
     * <p>
     * The label may be null or empty.
     * </p>
     * 
     * @param label The new label (may be null)
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Returns a string representation of this FrameItem.
     * <p>
     * The string includes the frame ID and label for debugging purposes.
     * </p>
     * 
     * @return A string representation of this frame item
     */
    @Override
    public String toString() {
        return "FrameItem{" +
                "frameId='" + frameId + '\'' +
                ", label='" + label + '\'' +
                ", hasThumbnail=" + (thumbnail != null) +
                '}';
    }
    
    /**
     * Compares this FrameItem with another object for equality.
     * <p>
     * Two FrameItem objects are considered equal if they have the same frameId.
     * This is because frameId is the unique identifier for frames.
     * </p>
     * 
     * @param o The object to compare with
     * @return true if the objects are equal (same frameId), false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrameItem frameItem = (FrameItem) o;
        return Objects.equals(frameId, frameItem.frameId);
    }
    
    /**
     * Returns a hash code for this FrameItem.
     * <p>
     * The hash code is based solely on the frameId, consistent with the equals() method.
     * </p>
     * 
     * @return A hash code value for this frame item
     */
    @Override
    public int hashCode() {
        return Objects.hash(frameId);
    }
}
