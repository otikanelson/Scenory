package com.example.scenory.database;

import com.example.scenory.model.Scene;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SceneDAO {

    /**
     * Save a scene to the database
     */
    public static void save(Scene scene, int projectId) {
        if (scene.getId() == null || isUUID(scene.getId())) {
            insert(scene, projectId);
        } else {
            update(scene);
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
     * Insert a new scene
     */
    private static void insert(Scene scene, int projectId) {
        String sql = """
            INSERT INTO scenes (project_id, title, description, background_color, 
                               estimated_duration_seconds, sequence_order, location, 
                               time_of_day, is_completed, notes, created_date, modified_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, projectId);
            stmt.setString(2, scene.getName());
            stmt.setString(3, scene.getDescription());
            stmt.setString(4, "#FFFFFF"); // Default background color
            stmt.setInt(5, scene.getEstimatedDurationSeconds());
            stmt.setInt(6, scene.getSequenceOrder());
            stmt.setString(7, scene.getLocation());
            stmt.setString(8, scene.getTimeOfDay());
            stmt.setBoolean(9, scene.isCompleted());
            stmt.setString(10, scene.getNotes());
            stmt.setTimestamp(11, Timestamp.valueOf(scene.getCreatedDate()));
            stmt.setTimestamp(12, Timestamp.valueOf(scene.getModifiedDate()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        scene.setId(String.valueOf(generatedKeys.getInt(1)));
                        System.out.println("✅ Scene saved with DB ID: " + scene.getId());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saving scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update existing scene
     */
    private static void update(Scene scene) {
        String sql = """
            UPDATE scenes 
            SET title = ?, description = ?, estimated_duration_seconds = ?, 
                sequence_order = ?, location = ?, time_of_day = ?, 
                is_completed = ?, notes = ?, modified_date = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, scene.getName());
            stmt.setString(2, scene.getDescription());
            stmt.setInt(3, scene.getEstimatedDurationSeconds());
            stmt.setInt(4, scene.getSequenceOrder());
            stmt.setString(5, scene.getLocation());
            stmt.setString(6, scene.getTimeOfDay());
            stmt.setBoolean(7, scene.isCompleted());
            stmt.setString(8, scene.getNotes());
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(10, Integer.parseInt(scene.getId()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                scene.setModifiedDate(LocalDateTime.now());
                System.out.println("✅ Scene updated: " + scene.getName());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load scenes by project ID
     */
    public static List<Scene> loadByProjectId(int projectId) {
        List<Scene> scenes = new ArrayList<>();
        String sql = """
            SELECT id, title, description, background_color, estimated_duration_seconds,
                   sequence_order, location, time_of_day, is_completed, notes,
                   created_date, modified_date
            FROM scenes WHERE project_id = ? ORDER BY sequence_order ASC
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Scene scene = new Scene();
                    scene.setId(String.valueOf(rs.getInt("id")));
                    scene.setName(rs.getString("title"));
                    scene.setDescription(rs.getString("description"));
                    scene.setEstimatedDurationSeconds(rs.getInt("estimated_duration_seconds"));
                    scene.setSequenceOrder(rs.getInt("sequence_order"));
                    scene.setLocation(rs.getString("location"));
                    scene.setTimeOfDay(rs.getString("time_of_day"));
                    scene.setCompleted(rs.getBoolean("is_completed"));
                    scene.setNotes(rs.getString("notes"));
                    scene.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                    scene.setModifiedDate(rs.getTimestamp("modified_date").toLocalDateTime());

                    // Load associated panels (will implement after PanelDAO)
                     scene.setPanels(PanelDAO.loadBySceneId(Integer.parseInt(scene.getId())));

                    scenes.add(scene);
                }
            }

            System.out.println("📋 Loaded " + scenes.size() + " scenes for project " + projectId);

        } catch (SQLException e) {
            System.err.println("❌ Error loading scenes: " + e.getMessage());
            e.printStackTrace();
        }

        return scenes;
    }

    /**
     * Delete a scene and all associated panels
     */
    public static boolean delete(int sceneId) {
        String sql = "DELETE FROM scenes WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sceneId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("🗑️ Scene deleted (ID: " + sceneId + ")");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting scene: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}