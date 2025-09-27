package com.example.scenory.database;

import com.example.scenory.model.Panel;
import javafx.util.Duration;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PanelDAO {

    /**
     * Save a panel to the database (insert or update)
     */
    public static void save(Panel panel, int sceneId) {
        if (panel.getId() == null || isUUID(panel.getId())) {
            insert(panel, sceneId);
        } else {
            update(panel);
        }
    }

    /**
     * Check if ID is a UUID vs database ID
     */
    private static boolean isUUID(String id) {
        if (id == null) return false;
        return id.contains("-");
    }

    /**
     * Insert a new panel with Phase 1 enhancements
     */
    private static void insert(Panel panel, int sceneId) {
        String sql = """
            INSERT INTO panels (scene_id, title, description_rich_text, description_plain_text,
                               canvas_background_color, canvas_width, canvas_height, 
                               display_duration_seconds, sequence_order, canvas_data, 
                               thumbnail_data, shot_type, camera_angle, dialogue, action,
                               camera_movement, is_key_frame, transition_type, audio_notes,
                               created_date, modified_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, sceneId);
            stmt.setString(2, panel.getName());

            // Phase 1: Rich text fields
            stmt.setString(3, panel.getDescriptionRichText());
            stmt.setString(4, panel.getDescriptionPlainText());

            // Phase 1: Visual customization
            stmt.setString(5, panel.getCanvasBackgroundColor() != null ?
                    panel.getCanvasBackgroundColor() : "#FFFFFF");
            stmt.setInt(6, 800); // Default canvas width
            stmt.setInt(7, 600); // Default canvas height

            // Phase 1: Video timing
            double durationSeconds = panel.getDisplayDuration() != null ?
                    panel.getDisplayDuration().toSeconds() : 3.0;
            stmt.setDouble(8, durationSeconds);

            stmt.setInt(9, panel.getSequenceOrder());

            // Canvas content
            stmt.setBytes(10, panel.getCanvasImageData());
            stmt.setBytes(11, panel.getThumbnailData());

            // Panel metadata
            stmt.setString(12, panel.getShotType());
            stmt.setString(13, panel.getCameraAngle());
            stmt.setString(14, panel.getDialogue());
            stmt.setString(15, panel.getAction());
            stmt.setString(16, panel.getCameraMovement());
            stmt.setBoolean(17, panel.isKeyFrame());
            stmt.setString(18, panel.getTransitionType());
            stmt.setString(19, panel.getAudioNotes());

            // Timestamps
            stmt.setTimestamp(20, Timestamp.valueOf(panel.getCreatedDate()));
            stmt.setTimestamp(21, Timestamp.valueOf(panel.getModifiedDate()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        panel.setId(String.valueOf(generatedKeys.getInt(1)));
                        System.out.println("✅ Panel saved with DB ID: " + panel.getId());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saving panel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update existing panel with Phase 1 fields
     */
    private static void update(Panel panel) {
        String sql = """
            UPDATE panels 
            SET title = ?, description_rich_text = ?, description_plain_text = ?,
                canvas_background_color = ?, display_duration_seconds = ?,
                sequence_order = ?, canvas_data = ?, thumbnail_data = ?,
                shot_type = ?, camera_angle = ?, dialogue = ?, action = ?,
                camera_movement = ?, is_key_frame = ?, transition_type = ?,
                audio_notes = ?, modified_date = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, panel.getName());

            // Phase 1: Rich text fields
            stmt.setString(2, panel.getDescriptionRichText());
            stmt.setString(3, panel.getDescriptionPlainText());

            // Phase 1: Visual customization
            stmt.setString(4, panel.getCanvasBackgroundColor());

            // Phase 1: Video timing
            double durationSeconds = panel.getDisplayDuration() != null ?
                    panel.getDisplayDuration().toSeconds() : 3.0;
            stmt.setDouble(5, durationSeconds);

            stmt.setInt(6, panel.getSequenceOrder());

            // Canvas content
            stmt.setBytes(7, panel.getCanvasImageData());
            stmt.setBytes(8, panel.getThumbnailData());

            // Panel metadata
            stmt.setString(9, panel.getShotType());
            stmt.setString(10, panel.getCameraAngle());
            stmt.setString(11, panel.getDialogue());
            stmt.setString(12, panel.getAction());
            stmt.setString(13, panel.getCameraMovement());
            stmt.setBoolean(14, panel.isKeyFrame());
            stmt.setString(15, panel.getTransitionType());
            stmt.setString(16, panel.getAudioNotes());
            stmt.setTimestamp(17, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(18, Integer.parseInt(panel.getId()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                panel.setModifiedDate(LocalDateTime.now());
                System.out.println("✅ Panel updated: " + panel.getName());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating panel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load panels by scene ID
     */
    public static List<Panel> loadBySceneId(int sceneId) {
        List<Panel> panels = new ArrayList<>();
        String sql = """
            SELECT id, title, description_rich_text, description_plain_text,
                   canvas_background_color, canvas_width, canvas_height,
                   display_duration_seconds, sequence_order, canvas_data,
                   thumbnail_data, shot_type, camera_angle, dialogue, action,
                   camera_movement, is_key_frame, transition_type, audio_notes,
                   created_date, modified_date
            FROM panels WHERE scene_id = ? ORDER BY sequence_order ASC
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sceneId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Panel panel = new Panel();
                    panel.setId(String.valueOf(rs.getInt("id")));
                    panel.setName(rs.getString("title"));

                    // Phase 1: Rich text fields
                    panel.setDescriptionRichText(rs.getString("description_rich_text"));
                    panel.setDescriptionPlainText(rs.getString("description_plain_text"));

                    // Phase 1: Visual customization
                    panel.setCanvasBackgroundColor(rs.getString("canvas_background_color"));

                    // Phase 1: Video timing
                    double durationSeconds = rs.getDouble("display_duration_seconds");
                    panel.setDisplayDuration(Duration.seconds(durationSeconds));

                    panel.setSequenceOrder(rs.getInt("sequence_order"));

                    // Canvas content
                    panel.setCanvasImageData(rs.getBytes("canvas_data"));
                    panel.setThumbnailData(rs.getBytes("thumbnail_data"));

                    // Panel metadata
                    panel.setShotType(rs.getString("shot_type"));
                    panel.setCameraAngle(rs.getString("camera_angle"));
                    panel.setDialogue(rs.getString("dialogue"));
                    panel.setAction(rs.getString("action"));
                    panel.setCameraMovement(rs.getString("camera_movement"));
                    panel.setKeyFrame(rs.getBoolean("is_key_frame"));
                    panel.setTransitionType(rs.getString("transition_type"));
                    panel.setAudioNotes(rs.getString("audio_notes"));

                    // Timestamps
                    panel.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                    panel.setModifiedDate(rs.getTimestamp("modified_date").toLocalDateTime());

                    panels.add(panel);
                }
            }

            System.out.println("📋 Loaded " + panels.size() + " panels for scene " + sceneId);

        } catch (SQLException e) {
            System.err.println("❌ Error loading panels: " + e.getMessage());
            e.printStackTrace();
        }

        return panels;
    }

    /**
     * Delete a panel
     */
    public static boolean delete(int panelId) {
        String sql = "DELETE FROM panels WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, panelId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("🗑️ Panel deleted (ID: " + panelId + ")");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting panel: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}