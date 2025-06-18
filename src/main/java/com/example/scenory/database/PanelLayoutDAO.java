package com.example.scenory.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Panel Layout persistence
 * Handles saving and loading UI panel states
 */
public class PanelLayoutDAO {

    /**
     * Panel Layout data structure
     */
    public static class PanelLayout {
        private boolean toolPanelCollapsed = false;
        private boolean fileStructureCollapsed = false;
        private boolean sceneConstructorVisible = true;
        private String sceneConstructorPosition = "RIGHT";
        private double leftPanelWidth = 250.0;
        private double rightPanelWidth = 300.0;

        // Getters and setters
        public boolean isToolPanelCollapsed() { return toolPanelCollapsed; }
        public void setToolPanelCollapsed(boolean toolPanelCollapsed) { this.toolPanelCollapsed = toolPanelCollapsed; }

        public boolean isFileStructureCollapsed() { return fileStructureCollapsed; }
        public void setFileStructureCollapsed(boolean fileStructureCollapsed) { this.fileStructureCollapsed = fileStructureCollapsed; }

        public boolean isSceneConstructorVisible() { return sceneConstructorVisible; }
        public void setSceneConstructorVisible(boolean sceneConstructorVisible) { this.sceneConstructorVisible = sceneConstructorVisible; }

        public String getSceneConstructorPosition() { return sceneConstructorPosition; }
        public void setSceneConstructorPosition(String sceneConstructorPosition) { this.sceneConstructorPosition = sceneConstructorPosition; }

        public double getLeftPanelWidth() { return leftPanelWidth; }
        public void setLeftPanelWidth(double leftPanelWidth) { this.leftPanelWidth = leftPanelWidth; }

        public double getRightPanelWidth() { return rightPanelWidth; }
        public void setRightPanelWidth(double rightPanelWidth) { this.rightPanelWidth = rightPanelWidth; }
    }

    /**
     * Save panel layout to database
     */
    public static void saveLayout(String userId, String layoutName, PanelLayout layout) {
        if (!DatabaseManager.getInstance().isDatabaseAvailable()) {
            System.out.println("💾 Database not available, using in-memory panel layout");
            return;
        }

        String sql = """
            INSERT INTO panel_layouts (user_id, layout_name, tool_panel_collapsed, file_structure_collapsed,
                                     scene_constructor_visible, scene_constructor_position, 
                                     left_panel_width, right_panel_width)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                tool_panel_collapsed = VALUES(tool_panel_collapsed),
                file_structure_collapsed = VALUES(file_structure_collapsed),
                scene_constructor_visible = VALUES(scene_constructor_visible),
                scene_constructor_position = VALUES(scene_constructor_position),
                left_panel_width = VALUES(left_panel_width),
                right_panel_width = VALUES(right_panel_width),
                modified_date = CURRENT_TIMESTAMP
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, layoutName);
            stmt.setBoolean(3, layout.isToolPanelCollapsed());
            stmt.setBoolean(4, layout.isFileStructureCollapsed());
            stmt.setBoolean(5, layout.isSceneConstructorVisible());
            stmt.setString(6, layout.getSceneConstructorPosition());
            stmt.setDouble(7, layout.getLeftPanelWidth());
            stmt.setDouble(8, layout.getRightPanelWidth());

            stmt.executeUpdate();
            System.out.println("💾 Panel layout saved successfully");

        } catch (SQLException e) {
            System.err.println("❌ Error saving panel layout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load panel layout from database
     */
    public static PanelLayout loadLayout(String userId, String layoutName) {
        if (!DatabaseManager.getInstance().isDatabaseAvailable()) {
            System.out.println("📐 Using default panel layout for user: " + userId);
            return createDefaultLayout();
        }

        String sql = """
            SELECT tool_panel_collapsed, file_structure_collapsed, scene_constructor_visible,
                   scene_constructor_position, left_panel_width, right_panel_width
            FROM panel_layouts 
            WHERE user_id = ? AND layout_name = ?
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, layoutName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PanelLayout layout = new PanelLayout();
                    layout.setToolPanelCollapsed(rs.getBoolean("tool_panel_collapsed"));
                    layout.setFileStructureCollapsed(rs.getBoolean("file_structure_collapsed"));
                    layout.setSceneConstructorVisible(rs.getBoolean("scene_constructor_visible"));
                    layout.setSceneConstructorPosition(rs.getString("scene_constructor_position"));
                    layout.setLeftPanelWidth(rs.getDouble("left_panel_width"));
                    layout.setRightPanelWidth(rs.getDouble("right_panel_width"));

                    System.out.println("📐 Panel layout loaded from database");
                    return layout;
                } else {
                    System.out.println("📐 No saved layout found, using defaults");
                    return createDefaultLayout();
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading panel layout: " + e.getMessage());
            e.printStackTrace();
            return createDefaultLayout();
        }
    }

    /**
     * Create default panel layout
     */
    private static PanelLayout createDefaultLayout() {
        PanelLayout layout = new PanelLayout();
        layout.setToolPanelCollapsed(false);
        layout.setFileStructureCollapsed(false);
        layout.setSceneConstructorVisible(true);
        layout.setSceneConstructorPosition("RIGHT");
        layout.setLeftPanelWidth(250.0);
        layout.setRightPanelWidth(300.0);
        return layout;
    }

    /**
     * Delete a specific layout
     */
    public static boolean deleteLayout(String userId, String layoutName) {
        if (!DatabaseManager.getInstance().isDatabaseAvailable()) {
            return false;
        }

        String sql = "DELETE FROM panel_layouts WHERE user_id = ? AND layout_name = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, layoutName);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting panel layout: " + e.getMessage());
            return false;
        }
    }
}